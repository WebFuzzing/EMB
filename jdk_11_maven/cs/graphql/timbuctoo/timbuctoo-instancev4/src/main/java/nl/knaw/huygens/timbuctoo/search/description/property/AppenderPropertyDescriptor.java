package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import org.apache.tinkerpop.gremlin.structure.Vertex;

class AppenderPropertyDescriptor implements PropertyDescriptor {
  private final PropertyDescriptor propertyDescriptor1;
  private final PropertyDescriptor propertyDescriptor2;
  private final String separator;

  public AppenderPropertyDescriptor(PropertyDescriptor propertyDescriptor1, PropertyDescriptor propertyDescriptor2,
                                    String separator) {
    this.propertyDescriptor1 = propertyDescriptor1;
    this.propertyDescriptor2 = propertyDescriptor2;
    this.separator = separator;
  }

  @Override
  public String get(Vertex vertex) {
    String value1 = propertyDescriptor1.get(vertex);
    String value2 = propertyDescriptor2.get(vertex);
    if (value1 == null) {
      return value2;
    } else if (value2 == null) {
      return value1;
    } else {
      return String.format("%s%s%s", value1, separator, value2);
    }
  }
}
