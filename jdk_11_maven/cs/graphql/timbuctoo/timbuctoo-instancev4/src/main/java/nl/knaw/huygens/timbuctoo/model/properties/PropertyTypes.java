package nl.knaw.huygens.timbuctoo.model.properties;

import nl.knaw.huygens.timbuctoo.model.properties.converters.Converter;
import nl.knaw.huygens.timbuctoo.model.properties.converters.Converters;

public class PropertyTypes {

  public static LocalProperty localProperty(String propName) {
    return localProperty(propName, Converters.stringToString);
  }

  public static LocalProperty localProperty(String propName, Converter converter) {
    return new LocalProperty(propName, converter);
  }

  public static ReadableProperty wwPersonNameOrTempName() {
    return new WwPersonDisplayName();
  }

  public static ReadableProperty wwdocumentDisplayNameProperty() {
    return new WwDocumentDisplayName();
  }

}
