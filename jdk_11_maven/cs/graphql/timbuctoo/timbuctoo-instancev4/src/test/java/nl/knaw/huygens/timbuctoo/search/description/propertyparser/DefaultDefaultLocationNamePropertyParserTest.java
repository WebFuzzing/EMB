package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.model.LocationNames;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class DefaultDefaultLocationNamePropertyParserTest extends AbstractPropertyParserTest {

  private DefaultLocationNamePropertyParser instance;

  @Before
  public void setUp() throws Exception {
    instance = new DefaultLocationNamePropertyParser();
  }

  @Test
  public void parseReturnsOutputOfPlaceNameGetDefaultName() throws JsonProcessingException {
    LocationNames locationNames = new LocationNames("defLang");
    locationNames.addCountryName("defLang", "Nederland");

    String value = instance.parse(new ObjectMapper().writeValueAsString(locationNames));

    assertThat(value, is(locationNames.getDefaultName()));
  }

  @Test
  public void parseReturnsNullIfTheInputStringCannotBeParsedToLocationNames() {
    String value = instance.parse("malformedLocationNames");

    assertThat(value, is(nullValue()));
  }

  @Test
  public void parseForSortReturnsOutputOfPlaceNameGetDefaultName() throws JsonProcessingException {
    LocationNames locationNames = new LocationNames("defLang");
    locationNames.addCountryName("defLang", "Nederland");

    Object value = instance.parseForSort(new ObjectMapper().writeValueAsString(locationNames));

    assertThat(value, is(locationNames.getDefaultName()));
  }

  @Test
  public void parseForSortReturnsNullIfTheInputStringCannotBeParsedToLocationNames() {
    Object value = instance.parseForSort("malformedLocationNames");

    assertThat(value, is(nullValue()));
  }

  @Test
  public void parseForSortRemovesTheTrailingWhitespaces() throws JsonProcessingException {
    LocationNames locationNames = new LocationNames("defLang");
    locationNames.addCountryName("defLang", "Nederland    ");

    Object value = instance.parseForSort(new ObjectMapper().writeValueAsString(locationNames));

    assertThat(value, is("Nederland"));
  }

  @Test
  public void parseForSortRemovesTheLeadingWhitespaces() throws JsonProcessingException {
    LocationNames locationNames = new LocationNames("defLang");
    locationNames.addCountryName("defLang", "  Nederland");

    Object value = instance.parseForSort(new ObjectMapper().writeValueAsString(locationNames));

    assertThat(value, is("Nederland"));
  }

  @Override
  protected PropertyParser getInstance() {
    return instance;
  }
}
