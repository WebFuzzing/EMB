package nl.knaw.huygens.timbuctoo.v5.dataset;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Stopwatch;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.EntryImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogEntry;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.TimeWithUnit;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;

/**
 * Record current status of import on a dataSet.
 * <p>
 * Import cycles follow steps of this pattern:
 * <ul>
 * <li>{@link #start(String, String)} start the import process;</li>
 * <li>repeat 0 or more times:
 * <ul>
 * <li>{@link #startEntry(LogEntry)} start the import of one logEntry;</li>
 * <li>{@link #finishEntry()} finish the import of one logEntry;</li>
 * </ul>
 * </li>
 * <li>{@link #finishList()} finish the import process.</li>
 * </ul>
 * In between all steps methods {@link #setStatus(String)} and {@link #addError(String, Throwable)} can be called.
 * The last status and all error messages will be persisted via {@link LogList} and {@link LogEntry} respectively.
 * </p>
 */
@JsonTypeInfo(include = JsonTypeInfo.As.PROPERTY, use = JsonTypeInfo.Id.NAME)
public class ImportStatus {

  private static TimeUnit TIME_UNIT = TimeUnit.MILLISECONDS;

  private final LogList logList;
  private final Stopwatch stopwatch;
  private final ConcurrentLinkedDeque<String> messages;
  private final ConcurrentLinkedDeque<String> errors;

  private String methodName;
  private String baseUri;
  private String date;

  private LogEntry currentLogEntry;
  private long currentEntryStart;
  private String currentEntryName;

  public ImportStatus(LogList logList) {
    this.logList = logList;
    stopwatch = Stopwatch.createUnstarted();
    messages = new ConcurrentLinkedDeque<>();
    errors = new ConcurrentLinkedDeque<>();
  }

  public synchronized void start(String methodName, String baseUri) {
    messages.clear();
    errors.clear();
    currentEntryName = null;
    this.methodName = methodName;
    this.baseUri = baseUri;
    setStatus("Started " + this.methodName);
    if (!stopwatch.isRunning()) {
      stopwatch.start();
    }
  }

  public synchronized void startEntry(LogEntry logEntry) {
    logEntry.getLogToken().ifPresent(token -> setStatus("Adding entry with token " + token));
    logEntry.getRdfCreator()
      .ifPresent(creator -> setStatus("Creating entry with " + creator.getClass().getSimpleName()));
    currentLogEntry = logEntry;
    date = Instant.now().toString();
    currentLogEntry.getImportStatus().setDate(date);
    currentEntryStart = stopwatch.elapsed(TIME_UNIT);
  }

  public synchronized void addError(String message, Throwable error) {
    /* errors have a tight semantic coupling with nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ErrorObject
     * if this construction is changed the parse method of the ErrorObject should be changed as wel
     */
    StringBuilder errorMessage = new StringBuilder("[" + Instant.now().toString() + "] ");
    if (currentEntryName != null) {
      errorMessage.append("; file: " + currentEntryName);
    }
    errorMessage.append("; method: " + methodName);
    errorMessage.append("; message: " + message);
    errorMessage.append("; error: " + error.getMessage());
    String errorString = errorMessage.toString();
    if (currentLogEntry != null) {
      currentLogEntry.getImportStatus().addError(errorString);
    } else {
      logList.addListError(errorString);
    }
    errors.add(errorString);
    messages.add("ERROR: " + errorString);
  }

  public synchronized void finishEntry() {
    getCurrentLogEntry().ifPresent(entry -> {
      EntryImportStatus eis = entry.getImportStatus();
      int errorCount = eis.getErrors().size();
      setStatus("Finished entry with " + errorCount + " error" + (errorCount == 1 ? "" : "s"));
      date = Instant.now().toString();
      eis.setDate(date);
      eis.setElapsedTime(new TimeWithUnit(TIME_UNIT, stopwatch.elapsed(TIME_UNIT) - currentEntryStart));
      currentLogEntry = null;
      entry.getLogToken().ifPresent(token -> setStatus("Finished adding entry with token " + token));
      entry.getRdfCreator()
        .ifPresent(creator -> setStatus("Finished creating entry with " + creator.getClass().getSimpleName()));
    });
  }

  public synchronized void finishList() {
    stopwatch.stop();
    int errorCount = errors.size();
    setStatus("Finished import with " + errorCount + " error" + (errorCount == 1 ? "" : "s"));
    date = Instant.now().toString();
    logList.setLastImportDate(date);
    logList.setLastImportDuration(new TimeWithUnit(TIME_UNIT, stopwatch.elapsed(TIME_UNIT)));
    reset();
  }

  public synchronized String getMethodName() {
    return methodName;
  }

  public synchronized String getBaseUri() {
    return baseUri;
  }

  public synchronized String getStatus() {
    if (messages.isEmpty()) {
      return "No current import";
    } else {
      return messages.getLast();
    }
  }

  public synchronized void setStatus(String status) {
    if (currentLogEntry != null) {
      currentLogEntry.getImportStatus().setStatus(status);
    } else {
      logList.setLastStatus(status);
    }
    messages.add(status);
  }

  public synchronized String getDate() {
    return date;
  }

  public synchronized List<String> getMessages() {
    return new ArrayList<>(messages);
  }

  public synchronized List<String> getErrors() {
    return new ArrayList<>(errors);
  }

  public synchronized int getErrorCount() {
    return errors.size();
  }

  public synchronized boolean hasErrors() {
    return !errors.isEmpty();
  }

  public synchronized long getElapsedTime(String unit) {
    if (messages.isEmpty()) {
      return -1L;
    } else {
      return stopwatch.elapsed(TimeUnit.valueOf(unit));
    }
  }

  public synchronized TimeWithUnit getElapsedTime() {
    return new TimeWithUnit(TIME_UNIT, getElapsedTime(TIME_UNIT.name()));
  }

  public synchronized boolean isActive() {
    return stopwatch.isRunning();
  }

  private Optional<LogEntry> getCurrentLogEntry() {
    return Optional.ofNullable(currentLogEntry);
  }

  private void reset() {
    methodName = null;
    baseUri = null;
    currentLogEntry = null;
    stopwatch.reset();
  }

  public void addProgressItem(String itemName, ImportStatusLabel statusLabel) {
    currentLogEntry.getImportStatus().addProgressItem(itemName, statusLabel);
  }

  public void updateProgressItem(String itemName, long numberOfTriplesProcessed) {
    currentLogEntry.getImportStatus().updateProgressItem(itemName, numberOfTriplesProcessed);
  }

  public void finishProgressItem(String itemName) {
    currentLogEntry.getImportStatus().finishProgressItem(itemName);
  }

  public void startProgressItem(String itemName) {
    currentLogEntry.getImportStatus().startProgressItem(itemName);
  }

  public void setEntryName(String name) {
    currentEntryName = name;
  }
}
