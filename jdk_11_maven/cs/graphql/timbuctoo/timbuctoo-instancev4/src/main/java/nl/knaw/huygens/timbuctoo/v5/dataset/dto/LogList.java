package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

public class LogList {

  @JsonProperty
  private int processedUntil = -1;

  @JsonProperty
  private String lastStatus;

  @JsonProperty
  private String lastImportDate;

  @JsonProperty
  private TimeWithUnit lastImportDuration;

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  private List<String> listErrors = new ArrayList<>();

  @JsonProperty
  private List<LogEntry> logEntries = new ArrayList<>();

  public int addEntry(LogEntry entry) {
    logEntries.add(entry);
    return logEntries.size() - 1;
  }

  @JsonIgnore
  public ListIterator<LogEntry> getUnprocessed() {
    return logEntries.listIterator(processedUntil + 1);
  }

  public void markAsProcessed(int marker) {
    this.processedUntil = marker;
  }

  @JsonIgnore
  public List<LogEntry> getEntries() {
    return logEntries;
  }

  public String getLastStatus() {
    return lastStatus;
  }

  public void setLastStatus(String lastStatus) {
    this.lastStatus = lastStatus;
  }

  public String getLastImportDate() {
    return lastImportDate;
  }

  public void setLastImportDate(String dateString) {
    this.lastImportDate = dateString;
  }

  public Optional<TimeWithUnit> getLastImportDuration() {
    return Optional.ofNullable(lastImportDuration);
  }

  public void setLastImportDuration(TimeWithUnit lastImportDuration) {
    this.lastImportDuration = lastImportDuration;
  }

  public void addListError(String errorString) {
    listErrors.add(errorString);
  }

  public List<String> getListErrors() {
    return listErrors;
  }
}
