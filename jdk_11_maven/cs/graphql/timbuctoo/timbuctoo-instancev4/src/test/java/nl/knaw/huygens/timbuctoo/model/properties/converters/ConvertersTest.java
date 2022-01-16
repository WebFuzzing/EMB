package nl.knaw.huygens.timbuctoo.model.properties.converters;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class ConvertersTest {

  @Test
  public void forTypeReturnsAConverterMatchingTheTypeName()
    throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {

    assertThat(Converters.forType(PersonNamesConverter.TYPE), instanceOf(PersonNamesConverter.class));
    assertThat(Converters.forType(DatableConverter.TYPE), instanceOf(DatableConverter.class));
    assertThat(Converters.forType(StringToStringConverter.TYPE), instanceOf(StringToStringConverter.class));
    assertThat(Converters.forType(HyperlinksConverter.TYPE), instanceOf(HyperlinksConverter.class));
    assertThat(Converters.forType(DefaultFullPersonNameConverter.TYPE),
      instanceOf(DefaultFullPersonNameConverter.class));
    assertThat(Converters.forType(ArrayToEncodedArrayConverter.TYPE), instanceOf(ArrayToEncodedArrayConverter.class));
    assertThat(Converters.forType(AltNamesConverter.TYPE), instanceOf(AltNamesConverter.class));
  }


  @Test
  public void forTypeReturnsAConverterWithOptionsMatchingTheTypeName()
    throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
    final String[] options = {"a", "b"};

    final Converter encodedString = Converters.forType(StringToEncodedStringOfLimitedValuesConverter.TYPE, options);
    final Converter encodedArray = Converters.forType(StringArrayToEncodedArrayOfLimitedValues.TYPE, options);
    final Converter unEncodedString = Converters.forType(StringToUnencodedStringOfLimitedValuesConverter.TYPE, options);

    assertThat(encodedString,
      instanceOf(StringToEncodedStringOfLimitedValuesConverter.class));
    assertThat(encodedArray,
      instanceOf(StringArrayToEncodedArrayOfLimitedValues.class));
    assertThat(unEncodedString,
      instanceOf(StringToUnencodedStringOfLimitedValuesConverter.class));

    assertThat(((HasOptions) encodedString).getOptions(), contains("a", "b"));
    assertThat(((HasOptions) encodedArray).getOptions(), contains("a", "b"));
    assertThat(((HasOptions) unEncodedString).getOptions(), contains("a", "b"));
  }
}
