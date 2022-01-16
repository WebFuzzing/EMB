package nl.knaw.huygens.timbuctoo.server;

import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class BackgroundRunnerTest {

  @Test
  public void returnsEmptyBeforeStartWasCalled() throws Exception {
    final BackgroundRunner<String> instance = new BackgroundRunner<>(
      0,
      Clock.systemUTC(),
      mock(ScheduledExecutorService.class)
    );

    assertThat(instance.getMostRecentResult().isPresent(), is(false));
  }

  @Test
  public void returnsNonEmptyAfterRunReturnedRegardlessOfExecutorService() throws Exception {
    final BackgroundRunner<String> instance = new BackgroundRunner<>(
      0,
      Clock.systemUTC(),
      mock(ScheduledExecutorService.class)
    );

    instance.start(() -> "yeey!");
    assertThat(instance.getMostRecentResult().get(), is("yeey!"));
  }

  @Test
  public void schedulesTheFirstExecutionAtTheRequestedHour() throws Exception {
    final ScheduledExecutorTester mock = new ScheduledExecutorTester();
    final Instant two_oclock = Instant.parse("1970-01-01T14:00:00Z");
    //The clock determines what 16:00 means. In this case we have a clock at UTC, so 16 means 16:00 UTC
    BackgroundRunner<String> instance = new BackgroundRunner<>(16, Clock.fixed(two_oclock, ZoneId.of("Z")), mock);

    instance.start(() -> "");

    assertThat(mock.initialDelay, is(2L));
    assertThat(mock.unit, is(TimeUnit.HOURS));
  }

  @Test
  public void schedulesTheFirstExecutionAtTheRequestedHourForNonUtc() throws Exception {
    final ScheduledExecutorTester mock = new ScheduledExecutorTester();
    final Instant two_oclock_at_amsterdam = Instant.parse("1970-01-01T12:00:00Z");

    //The clock determines what 16:00 means. In this case we have a clock at a local timezone, so 16 means 14:00 UTC
    BackgroundRunner<String> instance = new BackgroundRunner<>(
      16,
      Clock.fixed(two_oclock_at_amsterdam, ZoneId.of("+0200")),
      mock
    );

    instance.start(() -> "");

    assertThat(mock.initialDelay, is(2L));
    assertThat(mock.unit, is(TimeUnit.HOURS));
  }

  @Test
  public void skipsTheFirstExecutionWhenStartedInTheRequestedHour() throws Exception {
    final ScheduledExecutorTester mock = new ScheduledExecutorTester();
    final Instant midnight = Instant.parse("1970-01-01T00:00:00Z");
    BackgroundRunner<String> instance = new BackgroundRunner<>(24, Clock.fixed(midnight, ZoneId.of("Z")), mock);

    instance.start(() -> "");

    assertThat(mock.initialDelay, is(24L));
  }

  @Test
  public void schedulesTheFirstExecutionAroundMidnightWhenRequestedJustBeforeMidnight() throws Exception {
    final ScheduledExecutorTester mock = new ScheduledExecutorTester();
    final Instant midnight = Instant.parse("1970-01-01T23:59:59Z");
    BackgroundRunner<String> instance = new BackgroundRunner<>(24, Clock.fixed(midnight, ZoneId.of("Z")), mock);

    instance.start(() -> "");

    assertThat(mock.initialDelay, is(1L));
  }

  @Test
  public void handlesScheduledAtLowerNumbersThanCurrentTime() throws Exception {
    final ScheduledExecutorTester mock = new ScheduledExecutorTester();
    final Instant midnight = Instant.parse("1970-01-01T14:00:00Z");
    BackgroundRunner<String> instance = new BackgroundRunner<>(2, Clock.fixed(midnight, ZoneId.of("Z")), mock);

    instance.start(() -> "");

    assertThat(mock.initialDelay, is(12L));
  }

  @Test
  public void schedulesSubsequentExecutions24HoursLater() throws Exception {
    final ScheduledExecutorTester mock = new ScheduledExecutorTester();
    BackgroundRunner<String> instance = new BackgroundRunner<>(
      16,
      Clock.fixed(Instant.ofEpochMilli(0), ZoneId.systemDefault()),
      mock
    );

    instance.start(() -> "");

    assertThat(mock.period, is(24L));
    assertThat(mock.unit, is(TimeUnit.HOURS));
  }

  private class ScheduledExecutorTester implements ScheduledExecutorService {
    public TimeUnit unit;
    public long period;
    public long initialDelay;

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelayArg, long periodArg,
                                                  TimeUnit unitArg) {
      initialDelay = initialDelayArg;
      period = periodArg;
      unit = unitArg;
      return null;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public void shutdown() {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public List<Runnable> shutdownNow() {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public boolean isShutdown() {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public boolean isTerminated() {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public Future<?> submit(Runnable task) {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
      throws InterruptedException, ExecutionException, TimeoutException {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }

    @Override
    public void execute(Runnable command) {
      throw new UnsupportedOperationException("Not an expected method call for this mock");
    }
  }
}
