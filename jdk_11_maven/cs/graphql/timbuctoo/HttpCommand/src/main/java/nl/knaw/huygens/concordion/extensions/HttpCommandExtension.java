package nl.knaw.huygens.concordion.extensions;

import org.concordion.api.extension.ConcordionExtender;
import org.concordion.api.extension.ConcordionExtension;

public class HttpCommandExtension implements ConcordionExtension {
  private final boolean embedCss;
  private HttpCommand httpCommand;
  private RequestCommand requestCommand;
  private ResponseCommand responseCommand;
  private static final String NAMESPACE = "http://huygens.knaw.nl/concordion-http-verifier";

  public HttpCommandExtension(HttpCaller caller, ResultValidator validator) {
    this(caller, validator, true, true);
  }

  public HttpCommandExtension(HttpCaller caller, ResultValidator validator, boolean addCaptions, boolean embedCss) {
    this.embedCss = embedCss;

    requestCommand = new RequestCommand(caller, "request", NAMESPACE, addCaptions);
    responseCommand = new ResponseCommand(requestCommand, validator, "response", NAMESPACE, addCaptions);
    httpCommand = new HttpCommand(requestCommand, responseCommand, "http", NAMESPACE);
  }

  @Override
  public void addTo(ConcordionExtender concordionExtender) {
    concordionExtender.withCommand(NAMESPACE, httpCommand.getName(), httpCommand);
    concordionExtender.withCommand(NAMESPACE, requestCommand.getName(), requestCommand);
    concordionExtender.withCommand(NAMESPACE, responseCommand.getName(), responseCommand);
    if (embedCss) {
      concordionExtender.withEmbeddedCSS(".defaultValue {\n" +
        "  color: gray;\n" +
        "}\n" +
        ".requestCaption, .responseCaption {\n" +
        "  margin-top: 1em;\n" +
        "  font-weight: bold;\n" +
        "  font-size: 0.8em;\n" +
        "}\n" +
        ".requestContent, .responseContent {\n" +
        "  background: #f6f9fc;\n" +
        "  padding: 10px;\n" +
        "  border: 1px solid #d0d4d9;\n" +
        "  font-family: monospace;\n" +
        "  border-radius: 3px;\n" +
        "}");
    }
  }
}
