package nl.knaw.huygens.timbuctoo.core.dto.property;

import nl.knaw.huygens.timbuctoo.core.PropertyConverter;
import nl.knaw.huygens.timbuctoo.model.AltNames;
import nl.knaw.huygens.timbuctoo.util.Tuple;

import java.io.IOException;

public class AltNamesProperty extends TimProperty<AltNames> {
  public AltNamesProperty(String name, AltNames value) {
    super(name, value);
  }

  @Override
  public <TypeT> Tuple<String, TypeT> convert(PropertyConverter<TypeT> propertyConverter) throws IOException {
    return propertyConverter.to(this);
  }
}
