package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;


class CompositePropertyDescriptor implements PropertyDescriptor {

  private final PropertyDescriptor preferredDescriptor;
  private final PropertyDescriptor backUpDescriptor;

  public CompositePropertyDescriptor(PropertyDescriptor preferredDescriptor, PropertyDescriptor backUpDescriptor) {
    this.preferredDescriptor = preferredDescriptor;
    this.backUpDescriptor = backUpDescriptor;
  }

  @Override
  public String get(Vertex vertex) {
    String value = preferredDescriptor.get(vertex);
    if (StringUtils.isBlank(value)) {
      value = backUpDescriptor.get(vertex);
    }
    return value;

  }
}

