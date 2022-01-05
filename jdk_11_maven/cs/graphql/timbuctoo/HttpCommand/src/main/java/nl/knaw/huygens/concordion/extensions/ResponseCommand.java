package nl.knaw.huygens.concordion.extensions;

import nu.xom.Builder;
import nu.xom.Document;
import nu.xom.ParsingException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.impl.io.DefaultHttpResponseParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.Result;
import org.concordion.api.ResultRecorder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static nl.knaw.huygens.concordion.extensions.ExpectedResult.ExpectedResultBuilder.expectedResult;
import static nl.knaw.huygens.concordion.extensions.Utils.addClass;
import static nl.knaw.huygens.concordion.extensions.Utils.getTextAndRemoveIndent;
import static nl.knaw.huygens.concordion.extensions.Utils.replaceVariableReferences;
import static nl.knaw.huygens.concordion.extensions.Utils.replaceWithEmptyElement;

//a placeholder, the logic is handled in HttpCommand
class ResponseCommand extends AbstractCommand {

  private final RequestCommand requestCommand;
  private final ResultValidator defaultValidator;
  private final String namespace;
  private final boolean addCaptions;
  private String verificationMethod;
  private ExpectedResult expectation;
  private String name;

  public ResponseCommand(RequestCommand requestCommand, ResultValidator defaultValidator, String name,
                         String namespace, boolean addCaptions) {
    this.requestCommand = requestCommand;
    this.defaultValidator = defaultValidator;
    this.name = name;
    this.namespace = namespace;
    this.addCaptions = addCaptions;
  }

  @Override
  public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    verificationMethod = commandCall.getExpression();
    expectation = parseExpectedResponse(commandCall.getElement(), evaluator, resultRecorder);
  }

  private ExpectedResult parseExpectedResponse(Element element, Evaluator evaluator, ResultRecorder resultRecorder) {
    String contents = getTextAndRemoveIndent(element);

    contents = replaceVariableReferences(evaluator, contents, resultRecorder);

    SessionInputBufferImpl buffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(), contents.length());
    buffer.bind(new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8)));
    DefaultHttpResponseParser defaultHttpResponseParser = new DefaultHttpResponseParser(buffer);

    ExpectedResult.ExpectedResultBuilder builder = expectedResult();
    String body = null;
    try {
      HttpResponse httpResponse = defaultHttpResponseParser.parse();
      StatusLine statusLine = httpResponse.getStatusLine();
      builder.withStatus(statusLine.getStatusCode());

      for (Header header : httpResponse.getAllHeaders()) {
        builder.withHeader(header.getName(), header.getValue());
      }

      if (buffer.hasBufferedData()) {
        body = "";

        while (buffer.hasBufferedData()) {
          body += (char) buffer.read();
        }
      }
      builder.withBody(body);
    } catch (IOException | HttpException e) {
      e.printStackTrace();
    }

    return builder.build();
  }

  @Override
  public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    ValidationResult validationResult;
    if (!StringUtils.isBlank(verificationMethod)) {
      evaluator.setVariable("#nl_knaw_huygens_httpcommand_result", requestCommand.getActualResult());
      evaluator.setVariable("#nl_knaw_huygens_httpcommand_expectation", expectation);
      validationResult = (ValidationResult) evaluator.evaluate(
        verificationMethod + "(#nl_knaw_huygens_httpcommand_expectation, #nl_knaw_huygens_httpcommand_result)"
      );
      evaluator.setVariable("#nl_knaw_huygens_httpcommand_result", null);
      evaluator.setVariable("#nl_knaw_huygens_httpcommand_expectation", null);
    } else {
      validationResult = defaultValidator.validate(expectation, requestCommand.getActualResult());
    }

    Element caption = null;
    if (addCaptions) {
      caption = new Element("div").addAttribute("class", "responseCaption").appendText("Response:");
    }

    Element resultElement = replaceWithEmptyElement(commandCall.getElement(), name, namespace, caption);
    addClass(resultElement, "responseContent");

    try {
      Builder builder = new Builder();
      Document document = builder.build(new StringReader(validationResult.getMessage()));
      //new Element() creates a deepcopy not attached to the doc
      nu.xom.Element rootElement = new nu.xom.Element(document.getRootElement());
      resultElement.appendChild(new Element(rootElement));
      resultRecorder.record(validationResult.isSucceeded() ? Result.SUCCESS : Result.FAILURE);
    } catch (ParsingException | IOException e) {
      resultRecorder.record(Result.FAILURE);
      if (e instanceof ParsingException) {
        resultElement.appendText(
          "Error at line " + ((ParsingException) e).getLineNumber() +
            ", column: " + ((ParsingException) e).getColumnNumber());
        resultElement.appendText(validationResult.getMessage());
      }
    }
  }

  public void cleanUp() {
    expectation = null;
  }


  public String getName() {
    return name;
  }
}
