package nl.knaw.huygens.timbuctoo.model.properties.converters;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Converters {
  private static final Map<String, Class<? extends Converter>> CONVERTER_TYPES = new HashMap<>();

  static {
    CONVERTER_TYPES.put(PersonNamesConverter.TYPE, PersonNamesConverter.class);
    CONVERTER_TYPES.put(StringArrayToEncodedArrayOfLimitedValues.TYPE, StringArrayToEncodedArrayOfLimitedValues.class);
    CONVERTER_TYPES.put(StringToEncodedStringOfLimitedValuesConverter.TYPE,
      StringToEncodedStringOfLimitedValuesConverter.class);
    CONVERTER_TYPES.put(DatableConverter.TYPE, DatableConverter.class);
    CONVERTER_TYPES.put(StringToStringConverter.TYPE, StringToStringConverter.class);
    CONVERTER_TYPES.put(HyperlinksConverter.TYPE, HyperlinksConverter.class);
    CONVERTER_TYPES.put(DefaultFullPersonNameConverter.TYPE, DefaultFullPersonNameConverter.class);
    CONVERTER_TYPES.put(StringToUnencodedStringOfLimitedValuesConverter.TYPE,
      StringToUnencodedStringOfLimitedValuesConverter.class);
    CONVERTER_TYPES.put(ArrayToEncodedArrayConverter.TYPE, ArrayToEncodedArrayConverter.class);
    CONVERTER_TYPES.put(AltNamesConverter.TYPE, AltNamesConverter.class);
  }

  public static final StringToStringConverter stringToString = new StringToStringConverter();
  public static final ArrayToEncodedArrayConverter arrayToEncodedArray = new ArrayToEncodedArrayConverter();

  public static final HyperlinksConverter hyperlinks = new HyperlinksConverter();
  public static final DatableConverter datable = new DatableConverter();
  public static final StringToEncodedStringOfLimitedValuesConverter gender = stringToEncodedStringOf(
    "UNKNOWN",
    "MALE",
    "FEMALE",
    "NOT_APPLICABLE"
  );
  public static final PersonNamesConverter personNames = new PersonNamesConverter();
  public static final StringToUnencodedStringOfLimitedValuesConverter stringOfYesNoUnknown = stringToUnencodedStringOf(
    "YES",
    "NO",
    "UNKNOWN"
  );

  //Converters that have a more specific usecase
  public static final DefaultLocationNameConverter defaultLocationNameConverter = new DefaultLocationNameConverter();
  public static final DefaultFullPersonNameConverter defaultFullPersonNameConverter
    = new DefaultFullPersonNameConverter();
  // CNW AltNames converter
  public static final AltNamesConverter altNames = new AltNamesConverter();


  public static StringArrayToEncodedArrayOfLimitedValues stringArrayToEncodedArrayOf(String... values) {
    return new StringArrayToEncodedArrayOfLimitedValues(values);
  }

  public static StringToEncodedStringOfLimitedValuesConverter stringToEncodedStringOf(String... values) {
    return new StringToEncodedStringOfLimitedValuesConverter(values);
  }

  public static StringToUnencodedStringOfLimitedValuesConverter stringToUnencodedStringOf(String... values) {
    return new StringToUnencodedStringOfLimitedValuesConverter(values);
  }

  public static Converter forType(String type, String... options)
    throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
    final Class<? extends Converter> converterClass = CONVERTER_TYPES.get(type);

    if (HasOptions.class.isAssignableFrom(converterClass)) {
      final Constructor<? extends Converter> hasOptionsConstructor =
        converterClass.getDeclaredConstructor(String[].class);

      // Force varargs variant of constructor to be call by casting options to a single Object type
      return hasOptionsConstructor.newInstance((Object) options);
    } else {
      return converterClass.newInstance();
    }
  }
}
