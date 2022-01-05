package nl.knaw.huygens.timbuctoo.util;

import java.util.concurrent.TimeUnit;

// A class to configure timeouts without compromising the Timeout class.
public class TimeoutFactory {
  private long duration;
  private TimeUnit timeUnit;

  public TimeoutFactory() {
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
  }

  public Timeout createTimeout() {
    return new Timeout(duration, timeUnit);
  }
}
