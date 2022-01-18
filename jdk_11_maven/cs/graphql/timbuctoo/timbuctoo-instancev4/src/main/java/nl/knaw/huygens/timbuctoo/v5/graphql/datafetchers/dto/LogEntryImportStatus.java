package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;


import nl.knaw.huygens.timbuctoo.v5.dataset.dto.EntryImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogEntry;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ProgressItem;

import java.util.List;
import java.util.stream.Collectors;

import static nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel.DONE;
import static nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel.IMPORTING;
import static nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel.PENDING;

public class LogEntryImportStatus {
  private final String id;
  private final ImportStatusLabel status;
  private final List<String> errors;
  private final String source;
  private final List<ProgressStep> progress;
  private final List<ErrorObject> errorObjects;

  public LogEntryImportStatus(LogEntry logEntry, int id, boolean unprocessed) {
    this.id = "" + id;
    this.status = createStatus(logEntry, unprocessed);
    this.errors = logEntry.getImportStatus().getErrors();
    this.source = logEntry.getLogToken().orElse("");
    this.progress = createProgress(logEntry);
    this.errorObjects = errors.stream().map(ErrorObject::parse).collect(Collectors.toList());
  }

  private static ImportStatusLabel createStatus(LogEntry logEntry, boolean unprocessed) {
    EntryImportStatus importStatus = logEntry.getImportStatus();
    boolean hasDate = importStatus.getDate() != null; // means the import of the entry was started

    if (hasDate) {
      if (unprocessed) {
        return IMPORTING;
      } else {
        return DONE;
      }
    } else {
      return PENDING;
    }
  }

  private List<ProgressStep> createProgress(LogEntry logEntry) {
    return logEntry.getImportStatus().getProgressItems().entrySet().stream()
                   .filter(e -> e.getValue().getStatus() != DONE)
                   .map(e -> {
                     ProgressItem value = e.getValue();
                     return ProgressStep.create(e.getKey(), value.getStatus(), value.getProgress(), value.getSpeed());
                   }).collect(Collectors.toList());
  }

  public String getId() {
    return id;
  }

  public ImportStatusLabel getStatus() {
    return status;
  }

  public String getSource() {
    return source;
  }

  /**
   * @deprecated deprecated in the GraphQL schema
   */
  @Deprecated
  public List<String> getErrors() {
    return errors;
  }

  public List<ErrorObject> getErrorObjects() {
    return errorObjects;
  }

  public List<ProgressStep> getProgress() {
    return progress;
  }

}
