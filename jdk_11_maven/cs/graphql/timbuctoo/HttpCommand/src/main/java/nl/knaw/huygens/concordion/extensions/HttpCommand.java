package nl.knaw.huygens.concordion.extensions;

import org.concordion.api.AbstractCommand;
import org.concordion.api.CommandCall;
import org.concordion.api.Element;
import org.concordion.api.Evaluator;
import org.concordion.api.ResultRecorder;

public class HttpCommand extends AbstractCommand {
  private final RequestCommand requestCommand;
  private final ResponseCommand responseCommand;
  private final String commandName;
  private final String namespace;
  private String variableName;

  public HttpCommand(RequestCommand requestCommand, ResponseCommand responseCommand, String commandName,
                     String namespace) {
    this.requestCommand = requestCommand;
    this.responseCommand = responseCommand;
    this.commandName = commandName;
    this.namespace = namespace;
  }

  @Override
  public void setUp(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    stripCommandAttribute(commandCall.getElement());
    variableName = commandCall.getExpression();
    if (!variableName.isEmpty() && !variableName.startsWith("#")) {
      throw new RuntimeException(variableName + " should start with a #, to be a valid Concordion variable.");
    }

    commandCall.getChildren().setUp(evaluator, resultRecorder);
  }

  @Override
  public void execute(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    commandCall.getChildren().execute(evaluator, resultRecorder);

    if (!variableName.isEmpty()) {
      evaluator.setVariable(variableName, requestCommand.getActualResult());
    }
  }

  @Override
  public void verify(CommandCall commandCall, Evaluator evaluator, ResultRecorder resultRecorder) {
    commandCall.getChildren().verify(evaluator, resultRecorder);
    cleanUp();
  }

  private void cleanUp() {
    variableName = "";
    requestCommand.cleanUp();
    responseCommand.cleanUp();
  }

  private void stripCommandAttribute(Element element) {
    element.removeAttribute(commandName, namespace);
  }

  public String getName() {
    return commandName;
  }
}
