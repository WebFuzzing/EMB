package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import nl.knaw.huygens.concordion.extensions.ActualResult;
import nl.knaw.huygens.concordion.extensions.ExpectedResult;
import nl.knaw.huygens.concordion.extensions.HttpCommandExtension;
import nl.knaw.huygens.concordion.extensions.HttpRequest;
import nl.knaw.huygens.concordion.extensions.ReplaceEmbeddedStylesheetExtension;
import nl.knaw.huygens.concordion.extensions.ValidationResult;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.httpdiff.ExpectedHeadersAreEqualValidator;
import nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.concordion.api.extension.Extension;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Map;

import static com.google.common.xml.XmlEscapers.xmlContentEscaper;
import static nl.knaw.huygens.concordion.extensions.ValidationResult.xmlResult;
import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.jsonDiffer;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;

public abstract class AbstractV2_1EndpointFixture {

  @Extension
  public HttpCommandExtension commandExtension =
    new HttpCommandExtension(this::executeRequestUsingJaxRs, this::validate, true, false);
  @Extension
  public ReplaceEmbeddedStylesheetExtension removeExtension = new ReplaceEmbeddedStylesheetExtension(
    "/nl/knaw/huygens/timbuctoo/server/endpoints/v2/concordion.css"
  );

  /**
   * Implements the actual http call for the concordion HTTPCommand.
   */
  protected ActualResult executeRequestUsingJaxRs(HttpRequest httpRequest) {
    WebTarget target = returnUrlToMockedOrRealServer(httpRequest.server)
      .path(httpRequest.path);

    for (Map.Entry<String, String> queryParameter : httpRequest.queryParameters.entries()) {
      target = target.queryParam(queryParameter.getKey(), queryParameter.getValue());
    }

    Invocation.Builder request = target
      .request();

    for (Map.Entry<String, String> header : httpRequest.headers.entries()) {
      request = request.header(header.getKey(), header.getValue());
    }

    Response jerseyResult;
    if (httpRequest.body != null) {
      final String contentType = httpRequest.headers.entries().stream()
        .filter(x -> x.getKey().equalsIgnoreCase("content-type"))
        .map(Map.Entry::getValue)
        .findFirst().orElse("application/json");
      jerseyResult = request.method(httpRequest.method, Entity.entity(httpRequest.body, contentType));
    } else {
      jerseyResult = request.method(httpRequest.method);
    }
    return ActualResult.fromJaxRs(jerseyResult);
  }

  protected abstract WebTarget returnUrlToMockedOrRealServer(String serverAddress);

  protected Tuple<Boolean, String> testStatus(ExpectedResult expectation, ActualResult reality) {
    boolean statusTest = expectation.getStatus() == reality.getStatus();
    String result = String.format("<span class=\"defaultValue\">HTTP/1.1</span> <span class=\"respStatus %s\">%s</span>\n",
      statusTest ? "success" : "failure",
      reality.getStatus() + " " + reality.getStatusInfo()
    );
    return tuple(statusTest, result);
  }

  protected ValidationResult validate(ExpectedResult expectation, ActualResult reality) {
    Tuple<Boolean, String> statusResult = testStatus(expectation, reality);
    String result = statusResult.getRight();

    DiffResult headersResult = ExpectedHeadersAreEqualValidator.validate(expectation.getHeaders(), reality.getMultiHeaders());
    result += headersResult.asHtml();

    ValidationResult bodyResult = validateBody(expectation, reality);
    if (bodyResult.getMessage().length() > 0) {
      result += "\n";
    }
    result += bodyResult.getMessage();

    result = "<pre>" + result + "</pre>";

    return ValidationResult.xmlResult(
      statusResult.getLeft() && headersResult.wasSuccess() && bodyResult.isSucceeded(),
      result
    );
  }


  protected ValidationResult validateBody(ExpectedResult expectation, ActualResult reality) {
    if (expectation.hasBody()) {
      JsonDiffer differ = makeJsonDiffer();

      try {
        DiffResult result = differ.diff(reality.getBody(), expectation.getBody());
        return xmlResult(result.wasSuccess(), result.asHtml());
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      return xmlResult(true, xmlContentEscaper().escape(reality.getBody()));
    }

  }

  protected JsonDiffer makeJsonDiffer() {
    return jsonDiffer().build();
  }

  ;

}
