package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Clock;
import java.util.HashMap;
import java.util.function.Consumer;

public class ResultReporter {
  public static final Logger LOG = LoggerFactory.getLogger(ResultReporter.class);
  private final Consumer<String> statusUpdate;
  private int curRow;
  private int failures;
  private String currentSheet = "";
  private long lastLogTime;


  public ResultReporter(Consumer<String> statusUpdate) {
    this.statusUpdate = statusUpdate;
    this.curRow = 1;
    this.failures = 0;
    this.lastLogTime = -1;
  }

  public void startCollection(String name, Result result) {
    LOG.info("Start importing collection: '{}'", name);
    currentSheet = name;
    curRow = 1;
    logStatusMessage();
    result.handle(msg -> statusUpdate.accept("failure: " + msg));
  }

  private void logStatusMessage() {
    long curTime = Clock.systemUTC().millis();
    if (lastLogTime == -1 || curTime - lastLogTime > 100) {
      statusUpdate.accept(getStatusMessage());
      lastLogTime = curTime;
    }
  }

  public void registerPropertyName(int column, String value, Result result) {
    result.handle(msg -> log(column, msg));
  }

  public void startEntity() {
    logStatusMessage();
    curRow++;
  }

  public void finishEntity(HashMap<Integer, Result> extraResults) {
    extraResults.forEach((column, result) -> result.handle(msg -> log(column, msg)));
  }

  public void setValue(int column, String value, Result result) {

    result.handle(msg -> log(column, msg));
  }

  public void finishCollection() {
  }

  private void log(int columnNumber, String message) {
    this.failures++;
    LOG.error("Import failure \"{}\" on column \"{}\".", message, columnNumber);
    logStatusMessage();
  }

  private String getStatusMessage() {
    return failures > 0 ?
      String.format("processing %s (row %d, failures: %d)", currentSheet, curRow, failures) :
      String.format("processing %s (row %d)", currentSheet, curRow);
  }

  private String getExcelColumnName(int columnNumber) {
    int dividend = columnNumber;
    String columnName = "";
    int modulo;

    while (dividend > 0) {
      modulo = (dividend - 1) % 26;
      columnName = ((char) (65 + modulo)) + columnName;
      dividend = ((dividend - modulo) / 26);
    }

    return columnName;
  }

}
