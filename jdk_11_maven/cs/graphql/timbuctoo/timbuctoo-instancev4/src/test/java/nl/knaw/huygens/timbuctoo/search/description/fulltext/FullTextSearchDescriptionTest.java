package nl.knaw.huygens.timbuctoo.search.description.fulltext;

import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.FullTextSearchParameter;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription.createLocalFullTextSearchDescriptionWithBackupProperty;
import static nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription.createLocalSimpleFullTextSearchDescription;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class FullTextSearchDescriptionTest {

  public static final String BACKUP_PROPERTY = "backupProperty";
  protected static final String PROPERTY = "propertyName";
  protected static final String NAME = "name";
  private FullTextSearchDescription instance;

  @Before
  public void setUp() throws Exception {
    instance = createLocalSimpleFullTextSearchDescription(NAME, PROPERTY);
  }

  @Test
  public void getNameReturnsTheNameOfTheProperty() {
    String name = instance.getName();

    assertThat(name, is(NAME));
  }

  @Test
  public void filterFiltersTheVerticesOnTheValueOfTheProperty() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(PROPERTY, "value1"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, "number2"))
      .withVertex(vertex -> vertex.withTimId("v3").withProperty(PROPERTY, "value1"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter searchParameter = new FullTextSearchParameter(NAME, "value1");

    instance.filter(traversal, searchParameter);

    assertThat(traversal.toList(), containsInAnyOrder(likeVertex().withTimId("v1"), likeVertex().withTimId("v3")));
  }

  @Test
  public void filterIncludesTheVerticesWhereTheSearchedValueIsPartOfThePropertyValueOfTheVertex() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(PROPERTY, "value12344324"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, "another value"))
      .withVertex(vertex -> vertex.withTimId("v3").withProperty(PROPERTY, "value1"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "value");

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("v1"),
      likeVertex().withTimId("v2"),
      likeVertex().withTimId("v3")));
  }

  @Test
  public void filterDiscardsTheVerticesThatContainThePropertyWithADifferentTypeOfValue() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(PROPERTY, "value12344324"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, 12334))
      .withVertex(vertex -> vertex.withTimId("v3").withProperty(PROPERTY, "value1"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "value");

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("v1"),
      likeVertex().withTimId("v3")));
  }

  @Test
  public void filterFiltersOnEachOfTheTermIndividuallyEachPropertyHasToContainOnlyOne() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(PROPERTY, "value 12344324 value2"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, "value value2"))
      .withVertex(vertex -> vertex.withTimId("v3").withProperty(PROPERTY, "value1 value2"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "value value2");

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("v1"),
      likeVertex().withTimId("v2"),
      likeVertex().withTimId("v3")));
  }

  @Test
  public void filterFiltersCaseIndependent() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(PROPERTY, "Value"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, "VALUE"))
      .withVertex(vertex -> vertex.withTimId("v3").withProperty(PROPERTY, "vALUE"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "value");

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("v1"),
      likeVertex().withTimId("v2"),
      likeVertex().withTimId("v3")));
  }

  @Test
  public void filterFiltersOnABackupFieldIfTheAVertexDoesNotContainThePropertyField() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(PROPERTY, "value1"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, "value2"))
      .withVertex(vertex -> vertex.withTimId("v3").withProperty(BACKUP_PROPERTY, "value1"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "value1");
    FullTextSearchDescription instance =
      createLocalFullTextSearchDescriptionWithBackupProperty(NAME, PROPERTY, BACKUP_PROPERTY);

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("v1"),
      likeVertex().withTimId("v3")));
  }
  
  @Test
  public void filterFindsTheVertexViaTheBackupPropertyWhenThePropertyDoesNotMatch() {
    // In the real world, the backup property was never reached due to the .coalesce step being
    // satisfied that the PersonNames' empty list mapped to a blank string.
    // A .union step for both however is quite satisfactory because when a user searches, the user wants
    // to search in all available data.

    // This test fails when using .coalesce
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v3")
                                  .withProperty(PROPERTY, "") // empty string like in the real world scenario
                                  .withProperty(BACKUP_PROPERTY, "value1"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "value1");
    FullTextSearchDescription instance =
      createLocalFullTextSearchDescriptionWithBackupProperty(NAME, PROPERTY, BACKUP_PROPERTY);

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), contains(likeVertex().withTimId("v3")));
  }

  @Test
  public void filterAlsoFiltersOnTheBackupPropertyFieldIfTheVertexDoesContainTheProperty() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(BACKUP_PROPERTY, "value1"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, "value2"))
      .withVertex(vertex -> vertex.withTimId("v3")
                                  .withProperty(PROPERTY, "value")
                                  .withProperty(BACKUP_PROPERTY, "value1"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "value1");
    FullTextSearchDescription instance =
      createLocalFullTextSearchDescriptionWithBackupProperty(NAME, PROPERTY, BACKUP_PROPERTY);

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), containsInAnyOrder(
      likeVertex().withTimId("v1"),
      likeVertex().withTimId("v3")
    ));
  }

  @Test
  public void filterFiltersAllTheVerticesFromTheTraversalWhenNoneMatch() {
    GraphTraversal<Vertex, Vertex> traversal = newGraph()
      .withVertex(vertex -> vertex.withTimId("v1").withProperty(PROPERTY, "value1"))
      .withVertex(vertex -> vertex.withTimId("v2").withProperty(PROPERTY, "value2"))
      .withVertex(vertex -> vertex.withTimId("v3")
                                  .withProperty(PROPERTY, "value")
                                  .withProperty(BACKUP_PROPERTY, "value1"))
      .build()
      .traversal()
      .V();
    FullTextSearchParameter fullTextSearchParameter = new FullTextSearchParameter(NAME, "Not matching");
    FullTextSearchDescription instance =
      createLocalFullTextSearchDescriptionWithBackupProperty(NAME, PROPERTY, BACKUP_PROPERTY);

    instance.filter(traversal, fullTextSearchParameter);

    assertThat(traversal.toList(), is(empty()));

  }

}
