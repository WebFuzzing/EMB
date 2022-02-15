package nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps;

import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.rml.TestRow;
import nl.knaw.huygens.timbuctoo.rml.ThrowingErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfValue;
import org.junit.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RrTemplateTest {

  private static final String XSD_STRING = "http://www.w3.org/2001/XMLSchema#string";

  @Test
  public void replacesTemplatesWithEmptyStringIfValueIsEmpty() throws Exception {
    TestRow row = new TestRow(ImmutableMap.of("foo", ""), ImmutableMap.of(), new ThrowingErrorHandler());
    RrTemplate rrTemplate = new RrTemplate("start {foo} end", TermType.Literal, XSD_STRING);

    Optional<QuadPart> actual = rrTemplate.generateValue(row);
    assertThat(actual, is(Optional.of(new RdfValue("start  end", "http://www.w3.org/2001/XMLSchema#string"))));
  }

  @Test
  public void returnsEmptyIfOneValueIsMissing() throws Exception {
    TestRow row = new TestRow(ImmutableMap.of("foo", ""), ImmutableMap.of(), new ThrowingErrorHandler());
    RrTemplate rrTemplate = new RrTemplate("{foo} and {bar}", TermType.Literal, XSD_STRING);

    Optional<QuadPart> actual = rrTemplate.generateValue(row);
    assertThat(actual, is(Optional.empty()));
  }

  @Test
  public void iriEncodesValueIfResultTypeIsIri() throws Exception {
    TestRow row = new TestRow(
      ImmutableMap.of("foo", "some s\"tring with wéird characters"),
      ImmutableMap.of(),
      new ThrowingErrorHandler()
    );
    RrTemplate rrTemplate = new RrTemplate("http://{foo}", TermType.IRI, null);

    Optional<QuadPart> actual = rrTemplate.generateValue(row);
    assertThat(
      actual,
      is(Optional.of(new RdfUri("http://some%20s%22tring%20with%20w%C3%A9ird%20characters")))
    );
  }

}
