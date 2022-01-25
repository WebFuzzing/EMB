package nl.knaw.huygens.timbuctoo.rml;

import java.util.Map;

public interface ErrorHandler {
  /**
   * Handles the link errors when the value of the child field is not null, else it ignores the error.
   */
  void linkError(Map<String, String> rowData, String childField, String parentCollection, String parentField);

  void valueGenerateFailed(String key, String message);

  void subjectGenerationFailed(String uri, Row row);
}
