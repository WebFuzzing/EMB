package nl.knaw.huygens.timbuctoo.model.properties.converters;

import org.junit.Test;

import java.io.IOException;

public class StringToEncodedStringOfLimitedValuesConverterTest {

  @Test(expected = IOException.class)
  public void validateThrowsAnExceptionWhenTheValueIsNotAnExpectedValue() throws IOException {
    StringToEncodedStringOfLimitedValuesConverter instance =
      new StringToEncodedStringOfLimitedValuesConverter("val1", "val2");

    instance.validate("\"val3\"");
  }

  @Test
  public void validateDoesNothingWhenTheValueIsAnExpectedValue() throws IOException {
    StringToEncodedStringOfLimitedValuesConverter instance =
      new StringToEncodedStringOfLimitedValuesConverter("val1", "val2");

    instance.validate("\"val2\"");
  }
}
