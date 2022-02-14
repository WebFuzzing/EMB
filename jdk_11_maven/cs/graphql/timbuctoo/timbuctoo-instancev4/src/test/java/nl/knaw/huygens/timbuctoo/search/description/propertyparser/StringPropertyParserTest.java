package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class StringPropertyParserTest extends AbstractPropertyParserTest {

  private StringPropertyParser instance;

  @Before
  public void setUp() throws Exception {
    instance = new StringPropertyParser();
  }

  @Test
  public void parseReturnsTheInputValue() {
    String input = "input";

    String output = instance.parse(input);

    assertThat(output, is(equalTo(input)));
  }

  @Test
  public void parseForSortReturnsTheInputValue() {
    String input = "input";

    Object output = instance.parseForSort(input);

    assertThat(output, is(equalTo(input)));
  }

  @Test
  public void parseForSortRemovesTheTrailingWhitespaces() throws JsonProcessingException {
    String input = "input   ";

    Object output = instance.parseForSort(input);

    assertThat(output, is("input"));
  }

  @Test
  public void parseForSortRemovesTheLeadingWhitespaces() throws JsonProcessingException {
    String input = "   input";

    Object output = instance.parseForSort(input);

    assertThat(output, is(is("input")));
  }

  @Override
  protected PropertyParser getInstance() {
    return instance;
  }
}
