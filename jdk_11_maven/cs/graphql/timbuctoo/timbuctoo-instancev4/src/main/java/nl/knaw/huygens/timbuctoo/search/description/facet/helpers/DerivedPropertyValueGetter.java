package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import nl.knaw.huygens.timbuctoo.search.description.facet.PropertyValueGetter;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.List;

public class DerivedPropertyValueGetter implements PropertyValueGetter {
  private final String[] relations;
  private final String[] relationNames;

  public DerivedPropertyValueGetter(String[] relationNames, String... relations) {
    this.relations = relations;
    this.relationNames = relationNames;
  }

  @Override
  public List<String> getValues(Vertex vertex, String propertyName) {
    List<String> result = new ArrayList<>();
    vertex.vertices(Direction.BOTH, relations).forEachRemaining(targetVertex -> {
      targetVertex.vertices(Direction.BOTH, relationNames).forEachRemaining(finalVertex -> {
        if (finalVertex.property(propertyName).isPresent()) {
          result.add((String) finalVertex.property(propertyName).value());
        }
      });
    });
    return result;
  }
}
