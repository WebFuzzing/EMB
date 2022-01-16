package nl.knaw.huygens.timbuctoo.search.description.propertyparser;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StringListParserTest {

  @Test
  public void parseConcatenatesTheValuesOfTheListWithASemiColon() {
    String input = "[\"value1\", \"value2\"]";
    String expectedValue = "value1;value2";
    StringListParser instance = new StringListParser();

    String value = instance.parse(input);

    assertThat(value, is(equalTo(expectedValue)));
  }

  @Test
  public void parseReturnsNullIfTheInputIsNull() {
    String input = null;
    StringListParser instance = new StringListParser();

    String value = instance.parse(input);

    assertThat(value, is(nullValue()));
  }

  @Test
  public void parseConcatenatesTheValuesOfTheListWithAEn() {
    String input = "[\"value1\", \"value2\"]";
    String expectedValue = "value1 en value2";
    StringListParser instance = new StringListParser(" en ");

    String value = instance.parse(input);

    assertThat(value, is(equalTo(expectedValue)));
  }

  @Test
  public void parseDoesNotRemoveTheSpacesFromTheStringValuesInTheArray() {
    String input = "[\"value1\", \"value2 123 eb test\"]";
    String expectedValue = "value1;value2 123 eb test";
    StringListParser instance = new StringListParser(";");

    String value = instance.parse(input);

    assertThat(value, is(equalTo(expectedValue)));
  }

  @Test
  public void parseDoesNotRemoveCommasFromStringValuesInTheArray() {
    String input = "[\"value1\", \"value2 123, test\"]";
    String expectedValue = "value1 value2 123, test";
    StringListParser instance = new StringListParser(" ");

    String value = instance.parse(input);

    assertThat(value, is(equalTo(expectedValue)));
  }
}
