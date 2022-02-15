package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Lists;

import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.ListFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import static nl.knaw.huygens.timbuctoo.search.MockVertexBuilder.vertex;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static nl.knaw.huygens.timbuctoo.search.description.facet.CharterPortaalFondsFacetDescription.FONDS;
import static nl.knaw.huygens.timbuctoo.search.description.facet.CharterPortaalFondsFacetDescription.FONDS_NAAM;

public class CharterPortaalFondsFacetDescriptionTest {


  public static final String FACET_NAME = "facetName";

  @Test
  public void getValuesConcatenatesTheValuesOfFondAndFondsNaam() {
    Vertex vertex = vertex().withProperty(FONDS, "fonds")
                            .withProperty(FONDS_NAAM, "fondsNaam")
                            .build();

    CharterPortaalFondsFacetDescription instance = new CharterPortaalFondsFacetDescription(FACET_NAME,
        Mockito.mock(PropertyParser.class));

    List<String> values = instance.getValues(vertex);

    assertThat(values, contains("fondsNaam (fonds)"));
  }

  @Test
  public void filterChecksIfTheVertexContainsTheRightFondsAndFondsNaam() {
    // fonds is unique, but two fondsen could have the same name.
    Graph graph = newGraph().withVertex(v -> v.withTimId("id1")
                                              .withProperty(FONDS, "fonds")
                                              .withProperty(FONDS_NAAM, "fondsNaam"))
                            .withVertex(v -> v.withProperty(FONDS, "fonds1")
                                              .withProperty(FONDS_NAAM, "fondsNaam"))
                            .build();
    CharterPortaalFondsFacetDescription instance = new CharterPortaalFondsFacetDescription(FACET_NAME,
        Mockito.mock(PropertyParser.class));
    GraphTraversal<Vertex, Vertex> traversal = graph.traversal().V();
    instance.filter(traversal,
      Lists.newArrayList(new ListFacetValue("facetName", Lists.newArrayList("fondsNaam (fonds)"))));

    List<Vertex> actual = traversal.toList();
    assertThat(actual, contains(likeVertex().withTimId("id1")));
  }
}
