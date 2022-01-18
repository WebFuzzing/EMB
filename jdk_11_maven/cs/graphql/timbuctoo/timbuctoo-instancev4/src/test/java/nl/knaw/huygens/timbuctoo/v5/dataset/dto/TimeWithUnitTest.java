package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Created on 2017-12-11 11:21.
 */
public class TimeWithUnitTest {

  @Test
  public void convertFromSecondsToMilliseconds() {
    TimeWithUnit twu = new TimeWithUnit(TimeUnit.SECONDS, 123);

    long duration = twu.getTime(TimeUnit.MILLISECONDS.name());
    assertThat(duration, is(123000L));
  }

  @Test
  public void convertFromMilliSecondsToSeconds() {
    TimeWithUnit twu = new TimeWithUnit(TimeUnit.MILLISECONDS, 123456);

    long duration = twu.getTime(TimeUnit.SECONDS.name());
    assertThat(duration, is(123L));
  }

  @Test(expected = IllegalArgumentException.class)
  public void unknownUnit() {
    TimeWithUnit twu = new TimeWithUnit(TimeUnit.DAYS, 123);
    twu.getTime("Foo");
  }
}
