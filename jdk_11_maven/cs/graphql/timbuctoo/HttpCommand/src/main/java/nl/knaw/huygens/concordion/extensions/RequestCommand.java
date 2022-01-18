package nl.knaw.huygens.concordion.extensions;

import com.google.common.collect.LinkedListMultimap;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.impl.io.DefaultHttpRequestParser;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static nl.knaw.huygens.concordion.extensions.Utils.addClass;
import static nl.knaw.huygens.concordion.extensions.Utils.getTextAndRemoveIndent;
import static nl.knaw.huygens.concordion.extensions.Utils.replaceVariableReferences;
import static nl.knaw.huygens.concordion.extensions.Utils.replaceWithEmptyElement;

//a placeholder, the logic is handled in HttpCommand
class RequestCommand extends AbstractCommand {
  private final HttpCaller caller;
  private final String namespace;
  private final boolean addCaptions;
  private HttpRequest httpRequest;
  private ActualResult actualResult;
  private String name;

  public RequestCommand(HttpCaller caller, String name, String namespace, boolean addCaptions) {
    this.caller = caller;
    this.name = name;
    this.namespace = namespace;
    this.addCaptions = addCaptions;
  }

  @Override
  public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    httpRequest = parseRequest(commandCall.getElement(), evaluator, resultRecorder);
    formatRequest(commandCall.getElement(), httpRequest);
  }

  @Override
  public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    actualResult = caller.call(httpRequest);
  }

  private HttpRequest parseRequest(Element element, Evaluator evaluator, ResultRecorder resultRecorder) {
    String contents = getTextAndRemoveIndent(element);

    contents = replaceVariableReferences(evaluator, contents, resultRecorder);

    SessionInputBufferImpl buffer = new SessionInputBufferImpl(new HttpTransportMetricsImpl(), contents.length());
    buffer.bind(new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8)));
    DefaultHttpRequestParser defaultHttpRequestParser = new DefaultHttpRequestParser(buffer);
    LinkedListMultimap<String, String> queryParameters = LinkedListMultimap.create();

    String method = "";
    String url = "";
    LinkedListMultimap<String, String> headers = LinkedListMultimap.create();
    String body = null;
    String server = null;
    try {
      org.apache.http.HttpRequest httpRequest = defaultHttpRequestParser.parse();
      method = httpRequest.getRequestLine().getMethod();
      url = httpRequest.getRequestLine().getUri();
      if (url.startsWith("#")) {
        url = "" + evaluator.evaluate(url);
      }
      Matcher matcher = Pattern.compile("(https?://[^/]+)(/.*)").matcher(url);
      if (matcher.matches()) {
        server = matcher.group(1);
        url = matcher.group(2);
      }

      if (url.contains("?")) {
        String[] urlAndQueryParameters = url.split("\\?");
        url = urlAndQueryParameters[0];
        for (String queryParameter : urlAndQueryParameters[1].split("&")) {
          String[] parameter = queryParameter.split("=");

          queryParameters.put(parameter[0], parameter[1]);
        }
      }

      for (Header header : httpRequest.getAllHeaders()) {
        headers.put(header.getName(), header.getValue());
      }

      if (buffer.hasBufferedData()) {
        body = "";

        while (buffer.hasBufferedData()) {
          body += (char) buffer.read();
        }
      }

    } catch (IOException | HttpException e) {
      e.printStackTrace();
    }


    return new HttpRequest(method, url, headers, body, server, queryParameters);
  }

  private void formatRequest(Element origRequestElement, HttpRequest httpRequest) {
    Element caption = null;
    if (addCaptions) {
      caption = new Element("div").addAttribute("class", "requestCaption").appendText("Request:");
      if (httpRequest.server != null) {
        caption.appendChild(new Element("small").appendText(" (to " + httpRequest.server + ")"));
      }
    }

    Element newRequestElement = replaceWithEmptyElement(origRequestElement, name, namespace, caption);
    addClass(newRequestElement, "requestContent");

    Element requestPre = new Element("pre");
    newRequestElement.appendChild(requestPre);

    requestPre.appendChild(new Element("span").appendText(httpRequest.method + " "));
    requestPre.appendChild(new Element("b").appendText(httpRequest.getPathAndQuery() + " "));
    requestPre.appendChild(new Element("span").addAttribute("class", "defaultValue").appendText("HTTP/1.1"));
    for (Map.Entry<String, String> header : httpRequest.headers.entries()) {
      requestPre.appendText("\n" + header.getKey() + ": " + header.getValue());
    }
    if (httpRequest.body != null) {
      requestPre.appendText("\n\n");
      requestPre.appendChild(new Element("span").appendText(httpRequest.body).addAttribute("class", "reqBody"));
    }
  }

  public ActualResult getActualResult() {
    return actualResult;
  }

  public void cleanUp() {
    actualResult = null;
    httpRequest = null;
  }

  public String getName() {
    return name;
  }
}
