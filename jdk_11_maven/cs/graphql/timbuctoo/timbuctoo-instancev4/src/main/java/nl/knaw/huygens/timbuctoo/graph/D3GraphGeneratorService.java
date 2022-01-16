package nl.knaw.huygens.timbuctoo.graph;

import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.GraphReadUtils;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class D3GraphGeneratorService {

  private static final int MAX_LINKS_PER_NODE = 50;
  private final GraphWrapper graphWrapper;
  private Vres mappings;

  public D3GraphGeneratorService(GraphWrapper wrapper, Vres mappings) {
    this.graphWrapper = wrapper;
    this.mappings = mappings;
  }

  public D3Graph get(String type, UUID uuid, List<String> relationNames, int depth) throws NotFoundException {

    final String vreId = (String) graphWrapper.getGraph().traversal().V()
                                              .hasLabel(Collection.DATABASE_LABEL)
                                              .has(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, type)
                                              .in(Vre.HAS_COLLECTION_RELATION_NAME).next()
                                              .property(Vre.VRE_NAME_PROPERTY_NAME).value();

    final String relationTypeName = (String) graphWrapper.getGraph().traversal().V()
                                     .hasLabel(Collection.DATABASE_LABEL)
                                     .has(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, type)
                                     .in(Vre.HAS_COLLECTION_RELATION_NAME).out(Vre.HAS_COLLECTION_RELATION_NAME)
                                     .where(__.has(Collection.IS_RELATION_COLLECTION_PROPERTY_NAME, true)).next()
                                     .property(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME).value();


    GraphTraversal<Vertex, Vertex> result = graphWrapper.getGraph().traversal().V()
            .has("tim_id", uuid.toString()).filter(
              x -> ((String) x.get().property("types").value()).contains("\"" + type + "\"")
            ).has("isLatest", true)
            .not(__.has("deleted", true));

    if (!result.hasNext()) {
      throw new NotFoundException();
    }


    Vertex vertex = result.next();
    D3Graph d3Graph = new D3Graph();

    generateD3Graph(relationTypeName, vreId, d3Graph, vertex, relationNames, depth, 1);

    return d3Graph;
  }

  private void generateD3Graph(String relationTypeName, String vreId, D3Graph d3Graph, Vertex vertex,
                               List<String> relationNames,
                               int depth, int currentDepth) {

    final Optional<Collection> sourceCollection = GraphReadUtils.getCollectionByVreId(vertex, mappings, vreId);

    d3Graph.addNode(vertex, sourceCollection.get().getEntityTypeName());
    AtomicInteger count = new AtomicInteger(0);

    vertex.edges(Direction.BOTH, relationNames.toArray(new String[relationNames.size()])).forEachRemaining(edge -> {
      if (count.get() < MAX_LINKS_PER_NODE) {
        final Boolean isAccepted = edge.property(relationTypeName + "_accepted").isPresent() ?
          (Boolean) edge.property(relationTypeName + "_accepted").value() : false;

        final Boolean isLatest = edge.property("isLatest").isPresent() ?
          (Boolean) edge.property("isLatest").value() : false;

        if (isAccepted && isLatest) {
          count.incrementAndGet();
          loadLinks(relationTypeName, vreId, d3Graph, relationNames, depth, currentDepth, edge);
        }
      }
    });
  }

  private void loadLinks(String relationTypeName, String vreId, D3Graph d3Graph, List<String> relationNames,
                         int depth, int currentDepth, Edge edge) {

    Vertex source = edge.inVertex();
    Vertex target = edge.outVertex();
    final Optional<Collection> sourceCollection = GraphReadUtils.getCollectionByVreId(source, mappings, vreId);
    final Optional<Collection> targetCollection = GraphReadUtils.getCollectionByVreId(target, mappings, vreId);

    if (sourceCollection.isPresent() && targetCollection.isPresent()) {
      final String sourceEntityTypeName = sourceCollection.get().getEntityTypeName();
      final String targetEntityTypeName = targetCollection.get().getEntityTypeName();
      d3Graph.addNode(source, sourceEntityTypeName);
      d3Graph.addNode(target, targetEntityTypeName);
      d3Graph.addLink(edge, source, target, sourceEntityTypeName, targetEntityTypeName);

      if (currentDepth < depth) {
        generateD3Graph(relationTypeName, vreId, d3Graph, source, relationNames, depth, currentDepth + 1);
        generateD3Graph(relationTypeName, vreId, d3Graph, target, relationNames, depth, currentDepth + 1);
      }
    }
  }
}
