package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.model.Gender;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetGetter;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

public class ListFacetGetterTest {

  private static final String FACET_NAME = "facet_name";

  @Test
  public void getFacetReturnsValueKeysAndTheirCounts() {
    FacetGetter instance = new ListFacetGetter();
    Map<String, Set<Vertex>> values = Maps.newHashMap();
    List<Vertex> vertices1 = newGraph()
            .withVertex(v -> v.withTimId("1"))
            .withVertex(v -> v.withTimId("2"))
            .build().traversal().V().toList();
    List<Vertex> vertices2 = newGraph()
            .withVertex(v -> v.withTimId("3"))
            .build().traversal().V().toList();
    values.put("val1", Sets.newHashSet(vertices1));
    values.put("val2", Sets.newHashSet(vertices2));

    Facet facet = instance.getFacet(FACET_NAME, values);

    assertThat(facet.getName(), equalTo(FACET_NAME));
    assertThat(facet.getOptions(),
            containsInAnyOrder(new Facet.DefaultOption("val2", 1), new Facet.DefaultOption("val1", 2)));
  }

  @Test
  public void getFacetReturnsParsedValueKeysAndTheirCounts() {
    FacetGetter instance = new ListFacetGetter(new PropertyParserFactory().getParser(Gender.class));
    Map<String, Set<Vertex>> values = Maps.newHashMap();
    List<Vertex> vertices1 = newGraph()
            .withVertex(v -> v.withTimId("1"))
            .withVertex(v -> v.withTimId("2"))
            .build().traversal().V().toList();
    List<Vertex> vertices2 = newGraph()
            .withVertex(v -> v.withTimId("3"))
            .build().traversal().V().toList();
    values.put("\"MALE\"", Sets.newHashSet(vertices1));
    values.put("\"FEMALE\"", Sets.newHashSet(vertices2));

    Facet facet = instance.getFacet(FACET_NAME, values);

    assertThat(facet.getName(), equalTo(FACET_NAME));
    assertThat(facet.getOptions(),
            containsInAnyOrder(new Facet.DefaultOption("MALE", 2), new Facet.DefaultOption("FEMALE", 1)));
  }
}
