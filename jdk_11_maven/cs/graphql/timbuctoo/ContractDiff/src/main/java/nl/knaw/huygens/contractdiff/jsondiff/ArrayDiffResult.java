package nl.knaw.huygens.contractdiff.jsondiff;

import nl.knaw.huygens.contractdiff.diffresults.DiffResult;

import java.util.ArrayList;
import java.util.List;

public class ArrayDiffResult extends DiffResult {
  private List<DiffResult> innerResults;
  private boolean wasSuccess = true;

  public ArrayDiffResult() {
    this.innerResults = new ArrayList<>();
  }

  @Override
  public String asHtml() {
    String resultStr = "[\n";
    for (DiffResult result: innerResults) {
      resultStr += result.asHtml("", "  ", ",") + "\n";
    }
    return resultStr + "]\n";
  }

  @Override
  public String asHtml(String key, String indent, String delimiter) {
    String resultStr = indent + key + "[\n";
    for (DiffResult result: innerResults) {
      resultStr += result.asHtml("", indent + "  ", ",") + "\n";
    }
    return resultStr + indent + "]" + delimiter;
  }

  @Override
  public String asConsole() {
    String resultStr = RESET + "[\n";
    for (DiffResult result: innerResults) {
      resultStr += RESET + result.asConsole("", "  ", ",") + "\n";
    }
    return resultStr + RESET + "]\n";
  }

  @Override
  public String asConsole(String key, String indent, String delimiter) {
    String resultStr = indent + key + "[\n";
    for (DiffResult result: innerResults) {
      resultStr += RESET + indent + result.asConsole("", indent + "  ", ",") + "\n";
    }
    return resultStr + indent + "]" + delimiter;
  }


  @Override
  public boolean wasSuccess() {
    return wasSuccess;
  }

  public void add(int index, DiffResult diffResult) {
    this.innerResults.add(index, diffResult);
    if (!diffResult.wasSuccess()) {
      wasSuccess = false;
    }
  }
}
