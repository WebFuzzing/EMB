package nl.knaw.huygens.timbuctoo.v5.rml;

import nl.knaw.huygens.timbuctoo.rml.ErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.Row;

import java.util.Map;
import java.util.function.Consumer;

public class ReportingErrorHandler implements ErrorHandler {
  private final Consumer<String> reporter;

  public ReportingErrorHandler(Consumer<String> reporter) {
    this.reporter = reporter;
  }

  @Override
  public void linkError(Map<String, String> rowData, String childField, String parentCollection, String parentField) {
    if (rowData.get(childField) != null) {
      reporter.accept(String.format(
        "Row's field '%s' with value '%s' could not be linked to field '%s' of the collection '%s'",
        childField,
        rowData.get(childField),
        parentField,
        parentCollection
      ));
    }
  }

  @Override
  public void valueGenerateFailed(String key, String message) {
    reporter.accept("Value generating failed: " + key + ": " + message);
  }

  @Override
  public void subjectGenerationFailed(String uri, Row row) {
    reporter.accept("Could not generate subject for map " + uri + " using values: " + row);
  }
}
