package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.PersonName;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class PersonNamesDefaultNamePropertyParserTest extends AbstractPropertyParserTest {

  private PersonNamesDefaultNamePropertyParser instance;

  @Before
  public void setUp() throws Exception {
    instance = new PersonNamesDefaultNamePropertyParser();
  }

  @Test
  public void parseReturnsTheShortNameOfTheFirstNameOfThePersonNames() throws JsonProcessingException {
    PersonNames names = new PersonNames();
    PersonName name1 = PersonName.newInstance("forename", "surname");
    names.list.add(name1);
    names.list.add(PersonName.newInstance("forename2", "surname2"));
    String input = new ObjectMapper().writeValueAsString(names);

    String value = instance.parse(input);

    assertThat(value, is(name1.getShortName()));
  }

  @Test
  public void parseReturnsNullIfTheInputCannotBeParsed() throws JsonProcessingException {
    String value = instance.parse("invalid serialized names");

    assertThat(value, is(nullValue()));
  }

  @Test
  public void parseForSortReturnsAStringThatConsistsOfSortNameVariantOfTheDefaultName() throws JsonProcessingException {
    PersonNames names = new PersonNames();
    PersonName name1 = PersonName.newInstance("forename", "surname");
    names.list.add(name1);
    PersonName name2 = PersonName.newInstance("forename2", "surname2");
    names.list.add(name2);
    String input = new ObjectMapper().writeValueAsString(names);

    Object value = instance.parseForSort(input);

    assertThat(value, is(names.defaultName().getSortName()));
  }

  @Test
  public void parseForSortReturnsNullIfTheInputCannotBeParsed() throws JsonProcessingException {
    Object value = instance.parseForSort("invalid serialized names");

    assertThat(value, is(nullValue()));
  }

  @Test
  public void parserToRawReturnsNullIfThePersonNamesIsEmpty() throws JsonProcessingException {
    PersonNames names = new PersonNames();
    String input = new ObjectMapper().writeValueAsString(names);

    Object value = instance.parseForSort(input);

    assertThat(value, is(nullValue()));
  }

  @Override
  protected PropertyParser getInstance() {
    return instance;
  }
}
