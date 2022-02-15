package nl.knaw.huygens.timbuctoo.v5.dataset.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Maps;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel.IMPORTING;

public class EntryImportStatus {
  @JsonProperty
  private Map<String, ProgressItem> progressItemMap = Maps.newHashMap();

  @JsonProperty
  private String date;

  @JsonProperty
  private String status;

  private TimeWithUnit elapsedTime;

  private List<String> errors = new ArrayList<>();

  public synchronized String getStatus() {
    return status;
  }

  public synchronized void setStatus(String status) {
    this.status = status;
  }

  public synchronized String getDate() {
    return date;
  }

  public synchronized void setDate(String importDate) {
    this.date = importDate;
  }

  public synchronized void setElapsedTime(TimeWithUnit elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public synchronized TimeWithUnit getElapsedTime() {
    return elapsedTime;
  }

  public synchronized long getElapsedTime(String unit) {
    if (elapsedTime != null) {
      return elapsedTime.getTime(unit);
    } else {
      return -1L;
    }
  }

  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public synchronized List<String> getErrors() {
    return errors;
  }

  public synchronized void addError(String errorString) {
    errors.add(errorString);
  }

  public synchronized void addProgressItem(String itemName, ImportStatusLabel statusLabel) {
    ProgressItem progressItem = new ProgressItem();
    progressItemMap.put(itemName, progressItem);
    if (statusLabel == IMPORTING) {
      progressItem.start();
    }
  }

  public synchronized void updateProgressItem(String itemName, long numberOfTriplesProcessed) {
    ProgressItem progressItem = progressItemMap.get(itemName);
    if (progressItem != null) {
      progressItem.update(numberOfTriplesProcessed);
    }
  }

  public synchronized void finishProgressItem(String itemName) {
    ProgressItem progressItem = progressItemMap.get(itemName);
    if (progressItem != null) {
      progressItem.finish();
    }
  }

  public synchronized void startProgressItem(String itemName) {
    ProgressItem progressItem = progressItemMap.get(itemName);
    if (progressItem != null) {
      progressItem.start();
    }
  }

  @JsonIgnore
  public synchronized Map<String, ProgressItem> getProgressItems() {
    return progressItemMap;
  }
}
