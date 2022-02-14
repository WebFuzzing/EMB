package nl.knaw.huygens.timbuctoo.server;

import org.glassfish.jersey.internal.util.Producer;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BackgroundRunner<E> {

  private final int hourToRunAt;
  private final Clock clock;
  private final ScheduledExecutorService healthchecksExecutor;
  private E previousResult;

  public BackgroundRunner(int hourToRunAt, Clock clock, ScheduledExecutorService healthchecksExecutor) {
    this.clock = clock;
    this.healthchecksExecutor = healthchecksExecutor;
    this.previousResult = null;
    if (hourToRunAt == 0) {
      hourToRunAt = 24;
    }
    this.hourToRunAt = hourToRunAt;
  }

  public void start(Producer<E> task) {
    previousResult = task.call();
    final int currentHour = LocalDateTime.ofInstant(clock.instant(), clock.getZone()).getHour();
    final int initialDelay;
    if (currentHour > hourToRunAt) {
      initialDelay = 24 - currentHour + hourToRunAt;
    } else {
      initialDelay = hourToRunAt - currentHour;
    }
    healthchecksExecutor.scheduleAtFixedRate(() -> previousResult = task.call(), initialDelay, 24, TimeUnit.HOURS);
  }

  public Optional<E> getMostRecentResult() {
    return Optional.ofNullable(previousResult);
  }

}
