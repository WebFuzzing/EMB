package nl.knaw.huygens.timbuctoo.search.description.property;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;

import java.util.Collections;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class RelatedPropertyDescriptor implements PropertyDescriptor {
  private static final Logger LOG = getLogger(RelatedPropertyDescriptor.class);
  private static final String DEFAULT_SEPARATOR = ";";
  private final String relationName;
  private final String propertyName;
  private final PropertyParser parser;
  private final String acceptedPropertyName;
  private final String separator;

  public RelatedPropertyDescriptor(String relationName, String propertyName, String acceptedPropertyName,
                                   PropertyParser parser) {
    this(relationName, propertyName, parser, acceptedPropertyName, DEFAULT_SEPARATOR);
  }

  public RelatedPropertyDescriptor(String relationName, String propertyName, PropertyParser parser,
                                   String acceptedPropertyName, String separator) {
    this.relationName = relationName;
    this.propertyName = propertyName;
    this.parser = parser;
    this.acceptedPropertyName = acceptedPropertyName;
    this.separator = separator;
  }


  @Override
  public String get(Vertex vertex) {
    List<String> values = Lists.newArrayList();

    vertex.edges(Direction.OUT, relationName).forEachRemaining(edge -> {
      if (getPropOrTrue(edge, this.acceptedPropertyName) && getPropOrTrue(edge, "isLatest")) {
        Vertex vertex1 = edge.inVertex();

        if (vertex1.keys().contains(propertyName)) {
          String value = parser.parse(vertex1.value(propertyName));
          if (value != null) {
            values.add(value);
          }
        }
      }
    });

    Collections.sort(values); // The values are sorted in the solrized version of Timbuctoo too.
    return values.isEmpty() ? null : String.join(separator, values);
  }

  private boolean getPropOrTrue(Edge edge, String propertyName) {
    if (edge.property(propertyName).isPresent()) {
      Object accepted = edge.value(propertyName);
      if (accepted instanceof Boolean) {
        return (boolean) accepted;
      } else {
        LOG.error(propertyName + " is not a boolean");
      }
    }
    return true;
  }
}
