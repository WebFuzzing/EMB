package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class GenderPropertyParserTest extends AbstractPropertyParserTest {

  private GenderPropertyParser instance;

  @Before
  public void setUp() throws Exception {
    instance = new GenderPropertyParser();
  }


  @Test
  public void parseReturnsUnquotedString() {
    String input = "\"FEMALE\"";
    String expected = "FEMALE";

    String output = instance.parse(input);

    assertThat(output, is(equalTo(expected)));
  }

  @Test
  public void parseForSortReturnsUnquotedString() {
    String input = "\"FEMALE\"";
    String expected = "FEMALE";

    Object output = instance.parseForSort(input);

    assertThat(output, is(equalTo(expected)));
  }

  @Override
  protected PropertyParser getInstance() {
    return instance;
  }
}
