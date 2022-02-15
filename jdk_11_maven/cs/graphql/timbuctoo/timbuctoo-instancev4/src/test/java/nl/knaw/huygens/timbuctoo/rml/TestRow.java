package nl.knaw.huygens.timbuctoo.rml;

import java.util.List;
import java.util.Map;

public class TestRow implements Row {

  private final Map<String, String> data;
  private final ErrorHandler errorHandler;
  private final Map<String, List<String>> joinData;

  public TestRow(Map<String, String> data, Map<String, List<String>> joinData, ErrorHandler errorHandler) {
    this.data = data;
    this.joinData = joinData;
    this.errorHandler = errorHandler;
  }

  @Override
  public String getRawValue(String key) {
    return data.get(key);
  }

  @Override
  public List<String> getJoinValue(String key) {
    return joinData.get(key);
  }

  @Override
  public void handleLinkError(String childField, String parentCollection, String parentField) {
    errorHandler.linkError(data, childField, parentCollection, parentField);
  }
}
