package nl.knaw.huygens.timbuctoo.rml;

import java.util.Map;

public class ThrowingErrorHandler implements ErrorHandler {
  @Override
  public void linkError(Map<String, String> rowData, String childField, String parentCollection, String parentField) {
    throw new RuntimeException("Linking failed!");
  }

  @Override
  public void valueGenerateFailed(String key, String message) {
    throw new RuntimeException(key + ": " + message);
  }

  @Override
  public void subjectGenerationFailed(String uri, Row row) {
    throw new RuntimeException("Could not generate subject for map " + uri + " using values: " + row);
  }
}
