package nl.knaw.huygens.timbuctoo.core.dto.property;

import nl.knaw.huygens.timbuctoo.core.PropertyConverter;
import nl.knaw.huygens.timbuctoo.util.Tuple;

import java.io.IOException;

public abstract class TimProperty<ValueT> {
  private final String name;
  private final ValueT value;

  public TimProperty(String name, ValueT value) {
    this.name = name;
    this.value = value;
  }

  //Convenience method that allows you to convert a whole list of properties whose exact type you don't know
  public abstract <TypeT> Tuple<String, TypeT> convert(PropertyConverter<TypeT> propertyConverter) throws IOException;

  public String getName() {
    return name;
  }

  public ValueT getValue() {
    return value;
  }

  public String getPropertyType() {
    return this.getClass().getName();
  }
}
