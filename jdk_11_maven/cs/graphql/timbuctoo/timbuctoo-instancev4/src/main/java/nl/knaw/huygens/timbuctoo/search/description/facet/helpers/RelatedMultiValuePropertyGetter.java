package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.ArrayList;
import java.util.List;

public class RelatedMultiValuePropertyGetter extends MultiValuePropertyGetter {

  private final String[] relations;

  public RelatedMultiValuePropertyGetter(String... relations) {
    this.relations = relations;
  }

  @Override
  public List<String> getValues(Vertex vertex, String propertyName) {
    List<String> result = new ArrayList<>();
    vertex.vertices(Direction.BOTH, relations).forEachRemaining(targetVertex -> {
      List<String> values = super.getValues(targetVertex, propertyName);
      values.forEach(result::add);
    });
    return result;
  }
}
