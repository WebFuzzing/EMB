package nl.knaw.huygens.timbuctoo.database.tinkerpop.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.core.dto.property.AltNamesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.ArrayOfLimitedValuesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.ArrayProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.DatableProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.DefaultFullPersonNameProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.DefaultLocationNameProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.EncodedStringOfLimitedValuesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.HyperLinksProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.PersonNamesProperty;
import nl.knaw.huygens.timbuctoo.core.PropertyConverter;
import nl.knaw.huygens.timbuctoo.core.dto.property.StringOfLimitedValuesProperty;
import nl.knaw.huygens.timbuctoo.core.dto.property.StringProperty;
import nl.knaw.huygens.timbuctoo.model.AltNames;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.model.EdtfPattern;
import nl.knaw.huygens.timbuctoo.model.LocationNames;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.util.Tuple;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;

public class TinkerPopPropertyConverter extends PropertyConverter<Object> {

  private final ObjectMapper objectMapper;

  public TinkerPopPropertyConverter(Collection collection) {
    super(collection);
    objectMapper = new ObjectMapper();
  }

  @Override
  protected AltNamesProperty createAltNamesProperty(String propertyName, Object value) throws IOException {
    return new AltNamesProperty(propertyName, convertTo(value, AltNames.class));
  }

  @Override
  protected DatableProperty createDatableProperty(String propertyName, Object value) throws IOException {
    String decodedValue = objectMapper.readValue(toString(value), String.class);
    return new DatableProperty(propertyName, decodedValue);
  }

  @Override
  protected DefaultFullPersonNameProperty createDefaultFullPersonNameProperty(String propertyName, Object value)
    throws IOException {
    return new DefaultFullPersonNameProperty(propertyName, convertTo(value, PersonNames.class));
  }

  @Override
  protected DefaultLocationNameProperty createDefaultLocationNameProperty(String propertyName, Object value)
    throws IOException {
    return new DefaultLocationNameProperty(propertyName, convertTo(value, LocationNames.class));
  }

  @Override
  protected HyperLinksProperty createHyperLinksProperty(String propertyName, Object value) throws IOException {
    return new HyperLinksProperty(propertyName, toString(value));
  }

  @Override
  protected PersonNamesProperty createPersonNamesProperty(String propertyName, Object value) throws IOException {
    return new PersonNamesProperty(propertyName, convertTo(value, PersonNames.class));
  }

  @Override
  protected ArrayOfLimitedValuesProperty createArrayOfLimitedValuesProperty(String propertyName, Object value)
    throws IOException {
    return new ArrayOfLimitedValuesProperty(propertyName, toString(value));
  }

  @Override
  protected ArrayProperty createArrayProperty(String propertyName, Object value) throws IOException {
    return new ArrayProperty(propertyName, toString(value));
  }

  @Override
  protected EncodedStringOfLimitedValuesProperty createEncodedStringOfLimitedValuesProperty(String propertyName,
                                                                                            Object value)
    throws IOException {
    return new EncodedStringOfLimitedValuesProperty(propertyName, toString(value));
  }


  @Override
  protected StringProperty createStringProperty(String propertyName, Object value) throws IOException {
    return new StringProperty(propertyName, toString(value));
  }

  @Override
  protected StringOfLimitedValuesProperty createStringOfLimitedValues(String propertyName, Object value)
    throws IOException {
    return new StringOfLimitedValuesProperty(propertyName, toString(value));
  }

  private <T> T convertTo(Object value, Class<T> valueType) throws IOException {
    return objectMapper.readValue(toString(value), valueType);
  }

  private String toString(Object value) throws IOException {
    if (!(value instanceof String)) {
      throw new IOException(String.format("'%s' is not a String", value));
    }
    return (String) value;
  }


  @Override
  public Tuple<String, Object> to(AltNamesProperty property) throws IOException {
    return tuple(property.getName(), objectMapper.valueToTree(property.getValue().list));
  }

  @Override
  public Tuple<String, Object> to(DatableProperty property) throws IOException {
    String value = property.getValue();

    if (!EdtfPattern.isValidEdtf(value)) {
      throw new IOException("'" + property.getValue() + "' is not a supported datable format");
    }

    return tuple(property.getName(), objectMapper.writeValueAsString(value));
  }

  @Override
  public Tuple<String, Object> to(DefaultFullPersonNameProperty property) throws IOException {
    throw readOnlyProperty(property.getName(), property.getClass());
  }

  @Override
  public Tuple<String, Object> to(DefaultLocationNameProperty property) throws IOException {
    throw readOnlyProperty(property.getName(), property.getClass());
  }

  @Override
  public Tuple<String, Object> to(HyperLinksProperty property) throws IOException {
    return tuple(property.getName(), property.getValue());
  }

  @Override
  public Tuple<String, Object> to(PersonNamesProperty property) throws IOException {
    return tuple(property.getName(), objectMapper.writeValueAsString(property.getValue()));
  }

  @Override
  public Tuple<String, Object> to(ArrayOfLimitedValuesProperty property) throws IOException {
    return tuple(property.getName(), property.getValue());
  }

  @Override
  public Tuple<String, Object> to(ArrayProperty property) throws IOException {
    return tuple(property.getName(), property.getValue());
  }

  @Override
  public Tuple<String, Object> to(EncodedStringOfLimitedValuesProperty property) throws IOException {
    return tuple(property.getName(), property.getValue());
  }

  @Override
  public Tuple<String, Object> to(StringProperty property) throws IOException {
    return tuple(property.getName(), property.getValue());
  }

  @Override
  public Tuple<String, Object> to(StringOfLimitedValuesProperty property) throws IOException {
    return tuple(property.getName(), property.getValue());
  }

}
