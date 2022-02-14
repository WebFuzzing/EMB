package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.TimeUnit;

public class TimeWithUnit {

  @JsonProperty
  private String timeUnit;

  @JsonProperty
  private long time;

  private TimeWithUnit() {

  }

  public TimeWithUnit(TimeUnit unit, long time) {
    this.timeUnit = unit.name();
    this.time = time;
  }

  public String getTimeUnit() {
    return timeUnit;
  }

  public long getTime() {
    return time;
  }

  public long getTime(String unit) {
    return getTime(TimeUnit.valueOf(unit));
  }

  public long getTime(TimeUnit unit) {
    return unit.convert(time, TimeUnit.valueOf(timeUnit));
  }
}
