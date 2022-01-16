package nl.knaw.huygens.contractdiff.jsondiff;

import nl.knaw.huygens.contractdiff.diffresults.DiffResult;

import java.util.HashMap;
import java.util.Map;

import static nl.knaw.huygens.contractdiff.JsonBuilder.jsn;

public class ObjectDiffResult extends DiffResult {
  private Map<String, DiffResult> innerResults = new HashMap<>();
  private boolean wasSuccess = true;

  public void add(String key, DiffResult propResult) {
    innerResults.put(key, propResult);
    if (!propResult.wasSuccess()) {
      wasSuccess = false;
    }
  }

  @Override
  public String asHtml() {
    String resultStr = "{\n";
    for (Map.Entry<String, DiffResult> result: innerResults.entrySet()) {
      resultStr += result.getValue().asHtml(jsn(result.getKey()).toString() + ": ", "  ", ",") + "\n";
    }
    return resultStr + "}\n";
  }

  @Override
  public String asHtml(String key, String indent, String delimiter) {
    String resultStr = indent + key + "{\n";
    for (Map.Entry<String, DiffResult> result: innerResults.entrySet()) {
      resultStr += result.getValue().asHtml(jsn(result.getKey()).toString() + ": ", indent + "  ", ",") + "\n";
    }
    return resultStr + indent + "}" + delimiter;
  }

  @Override
  public String asConsole() {
    String resultStr = RESET + "{\n";
    for (Map.Entry<String, DiffResult> result: innerResults.entrySet()) {
      resultStr += result.getValue().asConsole(jsn(result.getKey()).toString() + ": ", "  ", ",") + RESET + "\n";
    }
    return resultStr + RESET + "}\n";
  }

  @Override
  public String asConsole(String key, String indent, String delimiter) {
    String resultStr = indent + key + "{\n";
    for (Map.Entry<String, DiffResult> result: innerResults.entrySet()) {
      resultStr += indent + result.getValue().asConsole(jsn(result.getKey()).toString() + ": ", indent + "  ", ",") + RESET + "\n";
    }
    return resultStr + indent + "}" + delimiter;
  }

  @Override
  public boolean wasSuccess() {
    return wasSuccess;
  }
}
