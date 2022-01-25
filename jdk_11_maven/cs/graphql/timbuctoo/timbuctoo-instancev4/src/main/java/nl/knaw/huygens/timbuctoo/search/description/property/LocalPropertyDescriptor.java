package nl.knaw.huygens.timbuctoo.search.description.property;

import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.tinkerpop.gremlin.structure.Vertex;

class LocalPropertyDescriptor implements PropertyDescriptor {
  public static final String DEFAULT_PREFIX = "";
  public static final String DEFAULT_POSTFIX = "";
  private final String propertyName;
  private final PropertyParser parser;
  private final String prefix;
  private final String postfix;

  public LocalPropertyDescriptor(String propertyName, PropertyParser parser) {
    this(propertyName, parser, DEFAULT_PREFIX, DEFAULT_POSTFIX);
  }

  public LocalPropertyDescriptor(String propertyName, PropertyParser parser, String prefix, String postfix) {
    this.propertyName = propertyName;
    this.parser = parser;
    this.prefix = prefix;
    this.postfix = postfix;
  }

  @Override
  public String get(Vertex vertex) {
    if (vertex.keys().contains(propertyName)) {
      String value = parser.parse(vertex.value(propertyName));

      if (value != null) {
        return String.format("%s%s%s", prefix, value, postfix);
      }
    }

    return null;
  }
}
