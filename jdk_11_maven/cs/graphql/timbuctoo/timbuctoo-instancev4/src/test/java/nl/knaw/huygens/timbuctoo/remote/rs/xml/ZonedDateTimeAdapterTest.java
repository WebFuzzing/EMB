package nl.knaw.huygens.timbuctoo.remote.rs.xml;


import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

public class ZonedDateTimeAdapterTest {

  @Test
  public void testMarshalUnmarshal() throws Exception {
    ZonedDateTimeAdapter adapter = new ZonedDateTimeAdapter();

    // assumed timezone of strings with no timezone info
    ZoneId newZoneId = ZoneId.of("UTC+10:00");
    // keep old zoneId for reset
    ZoneId oldZoneId = ZonedDateTimeAdapter.setZoneId(newZoneId);

    String[] inpexp = {
      // local dates
      "2016", "2015-12-31T14:00Z",
      "2015-08", "2015-07-31T14:00Z",
      "2014-08-09", "2014-08-08T14:00Z",
      // local datetimes
      "2013-03-09T14", "2013-03-09T04:00Z",
      "2012-03-09T14:30", "2012-03-09T04:30Z",
      "2011-03-09T14:30:29", "2011-03-09T04:30:29Z",
      "2010-01-09T14:30:29.1", "2010-01-09T04:30:29.100Z",
      "2010-03-09T14:30:29.123", "2010-03-09T04:30:29.123Z",
      "2010-04-09T14:30:29.1234", "2010-04-09T04:30:29.123400Z",
      "2010-06-09T14:30:29.123456", "2010-06-09T04:30:29.123456Z",
      // zoned datetimes
      "2009-03-09T14:30:29.123+01:00", "2009-03-09T13:30:29.123Z",
      "2008-03-09T14:30:29.123-01:00", "2008-03-09T15:30:29.123Z",
      "2007-03-09T14:30:29.123Z", "2007-03-09T14:30:29.123Z",
      "2006-03-09T14:30:29.123456789Z", "2006-03-09T14:30:29.123456789Z",
      "2005-03-09T14:30Z", "2005-03-09T14:30Z"
    };

    for (int i = 0; i < inpexp.length; i += 2) {
      String input = inpexp[i];
      String expected = inpexp[i + 1];
      ZonedDateTime zdt1 = adapter.unmarshal(input);
      String str1 = adapter.marshal(zdt1);
      //System.out.println(input + " -> " + str1);
      assertThat(str1, equalTo(expected));

      ZonedDateTime zdt2 = adapter.unmarshal(str1);
      String str2 = adapter.marshal(zdt2);
      assertThat(str2, equalTo(expected));
    }

    // reset zoneId
    ZoneId replacedZoneId = ZonedDateTimeAdapter.setZoneId(oldZoneId);
    assertThat(replacedZoneId, equalTo(newZoneId));
  }

  @Test(expected = DateTimeParseException.class)
  public void testUnmarshalWithInvalidString() throws Exception {
    new ZonedDateTimeAdapter().unmarshal("ivalid string");
  }

  @Test
  public void testWithNullInput() throws Exception {
    ZonedDateTimeAdapter adapter = new ZonedDateTimeAdapter();

    String str = null;
    assertThat(adapter.unmarshal(str), nullValue(ZonedDateTime.class));

    ZonedDateTime zdt = null;
    assertThat(adapter.marshal(zdt), nullValue(String.class));
  }

}
