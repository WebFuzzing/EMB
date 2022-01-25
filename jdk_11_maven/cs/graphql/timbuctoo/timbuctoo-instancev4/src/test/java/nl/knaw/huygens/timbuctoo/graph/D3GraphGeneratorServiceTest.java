package nl.knaw.huygens.timbuctoo.graph;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import nl.knaw.huygens.timbuctoo.server.HuygensIng;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class D3GraphGeneratorServiceTest {


  private static final String RELATION_NAME = "relationName";
  private static final String RELATION_NAME_2 = "relationName2";
  private static final String UNREQUESTED_RELATION = "unrequested";

  private static final String TYPES = "[\"person\", \"wwperson\"]";
  private static final String THE_UUID = "077bf0b5-6b7d-45aa-89ff-6ecf2cfc549c";


  private Edge mockEdge(String relationName) {
    Edge edgeMock = mock(Edge.class);
    given(edgeMock.label()).willReturn(relationName);
    return edgeMock;
  }


  private GraphWrapper createGraphWrapper(Graph graph) {
    return new GraphWrapper() {
      @Override
      public Graph getGraph() {
        return graph;
      }

      @Override
      public GraphTraversalSource getLatestState() {
        return graph.traversal();
      }

      @Override
      public GraphTraversal<Vertex, Vertex> getCurrentEntitiesFor(String... nulls) {
        return graph.traversal().V();
      }
    };
  }

  @Test
  public void getShouldGenerateAD3Graph() throws NotFoundException, IOException {

    Graph graph = newGraph()
            .withVertex("relationCollection", v ->
              v.withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "wwrelation")
              .withProperty(Collection.IS_RELATION_COLLECTION_PROPERTY_NAME, true)
            )
            .withVertex("vreNode", v ->
              v.withProperty(Vre.VRE_NAME_PROPERTY_NAME, "WomenWriters")
               .withOutgoingRelation(Vre.HAS_COLLECTION_RELATION_NAME, "relationCollection")
            )
            .withVertex(v ->
              v.withLabel(Collection.DATABASE_LABEL)
               .withProperty(Collection.ENTITY_TYPE_NAME_PROPERTY_NAME, "wwperson")
               .withIncomingRelation(Vre.HAS_COLLECTION_RELATION_NAME, "vreNode")
            )
            // Should be first index in the node list because it is the requested entity
            .withVertex("v1", v -> v
                    .withProperty("isLatest", true)
                    .withTimId(THE_UUID)
                    .withProperty("types", TYPES)
                    .withProperty("wwperson_tempName", "name1")
            )
            // Should come back as direct relation to v1 because RELATION_NAME is requested
            .withVertex("v2", v -> v
                    .withTimId("2")
                    .withProperty("types", TYPES)
                    .withOutgoingRelation(RELATION_NAME, "v1", r ->
                      r.withAccepted("wwrelation", true)
                       .withIsLatest(true)
                    )
                    .withProperty("wwperson_tempName", "name2")
            )
            // Should come back as direct relation to v1 because RELATION_NAME_2 is requested
            .withVertex("v3", v -> v
                    .withTimId("3")
                    .withProperty("types", TYPES)
                    .withOutgoingRelation(RELATION_NAME_2, "v1", r ->
                      r.withAccepted("wwrelation", true)
                       .withIsLatest(true)
                    )
                    .withProperty("wwperson_tempName", "name3")
            )
            // Should come back as once removed relation to v2->v1 because depth=2
            .withVertex("v4", v -> v
                    .withTimId("4")
                    .withProperty("types", TYPES)
                    .withOutgoingRelation(RELATION_NAME, "v2", r ->
                      r.withAccepted("wwrelation", true)
                       .withIsLatest(true)
                    )
                    .withProperty("wwperson_tempName", "name4")
            )
            // Should not come back because UNREQUESTED_RELATION is not requested
            .withVertex("v5", v -> v
                    .withTimId("5")
                    .withProperty("types", TYPES)
                    .withOutgoingRelation(UNREQUESTED_RELATION, "v1", r ->
                      r.withAccepted("wwrelation_accepted", true)
                       .withIsLatest(true)
                    )
                    .withProperty("wwperson_tempName", "name5")
            )
            // Should not come back because it is twice removed and depth=2
            .withVertex("v6", v -> v
                    .withTimId("6")
                    .withProperty("types", TYPES)
                    .withOutgoingRelation(RELATION_NAME, "v4", r ->
                      r.withAccepted("wwrelation", true)
                       .withIsLatest(true)
                    )
                    .withProperty("wwperson_tempName", "name6")
            )
            .build();

    List<Vertex> vertices = graph.traversal().V()
                                 .has("wwperson_tempName")
                                 .asAdmin().clone().toList();
    Collections.sort(vertices,
      Comparator.comparing(vertexA -> ((String) vertexA.property("wwperson_tempName").value()))
    );

    GraphWrapper graphWrapper = createGraphWrapper(graph);
    D3GraphGeneratorService underTest = new D3GraphGeneratorService(graphWrapper, HuygensIng.mappings);
    D3Graph result = underTest.get(
            "wwperson", UUID.fromString(THE_UUID), Lists.newArrayList(RELATION_NAME, RELATION_NAME_2), 2);

    assertThat(result.getNodes().get(0), is(new Node(vertices.get(0), "wwperson")));
    assertThat(result.getNodes(), containsInAnyOrder(
            new Node(vertices.get(0), "wwperson"),
            new Node(vertices.get(1), "wwperson"),
            new Node(vertices.get(2), "wwperson"),
            new Node(vertices.get(3), "wwperson")
    ));
    int indexOfRoot = getIndex(result.getNodes(), "077bf0b5-6b7d-45aa-89ff-6ecf2cfc549c");
    int indexOfPerson1 = getIndex(result.getNodes(), "2");
    int indexOfPerson2 = getIndex(result.getNodes(), "3");
    int indexOfPerson3 = getIndex(result.getNodes(), "4");
    assertThat(result.getLinks(), containsInAnyOrder(
            new Link(mockEdge(RELATION_NAME), indexOfRoot, indexOfPerson1),
            new Link(mockEdge(RELATION_NAME_2), indexOfRoot, indexOfPerson2),
            new Link(mockEdge(RELATION_NAME), indexOfPerson1, indexOfPerson3)
    ));
  }

  private int getIndex(java.util.Collection<Node> nodes, String key) {
    int idx = 0;
    for (Node node : nodes) {
      if (node.getKey().equals("wwpersons/" + key)) {
        return idx;
      }
      idx++;
    }
    return -1;
  }
}
