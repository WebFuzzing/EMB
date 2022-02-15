package nl.knaw.huygens.timbuctoo.v5.serializable.serializations;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Created on 2017-06-12 08:43.
 */
public class CsvSerializationTest {

  @Test
  public void performSerialization() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    CsvSerialization cs = new CsvSerialization(out);

    cs.serialize(SourceData.simpleResult());

    assertThat(out.toString(), is(
      "a,b.0.c,b.0.d.0,b.0.d.1,b.0.d.2,b.1.c,b.1.d.0,b.1.d.1,b.2.c,b.2.d.0,b.2.d.1,b.2.d.2,b.e,b.f\r\n" +
      "1,2,3,4,,5,6,7,,,,\r\n" +
      "8,9,10,11,,12,13.0,14,15,16,17,18\r\n" +
      "19,,,,,,,,,,,,20,21\r\n"));
  }
}
