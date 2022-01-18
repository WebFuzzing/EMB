package nl.knaw.huygens.timbuctoo.relationtypes;


import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class RelationTypeService {
  public static final String RELATIONTYPE_INDEX_NAME = "relationtypes";
  private final GraphWrapper graphWrapper;

  public RelationTypeService(GraphWrapper wrapper) {
    this.graphWrapper = wrapper;
  }

  public List<RelationTypeDescription> get(String name) {
    GraphTraversal<Vertex, Vertex> traversal = graphWrapper.getGraph().traversal().V().has("relationtype_regularName");
    if (name != null) {
      traversal =  traversal.or(
        __.has("relationtype_sourceTypeName", name),
        __.has("relationtype_targetTypeName", name)
      );
    }

    return traversal.toList().stream()
            .map(RelationTypeDescription::new)
            .collect(toList());
  }
}
