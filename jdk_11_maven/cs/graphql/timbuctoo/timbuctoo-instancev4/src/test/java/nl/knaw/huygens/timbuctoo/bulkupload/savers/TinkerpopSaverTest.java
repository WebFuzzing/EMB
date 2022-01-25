package nl.knaw.huygens.timbuctoo.bulkupload.savers;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.ImportPropertyDescriptions;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerpopSaver;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;

import java.util.Iterator;
import java.util.List;

import static nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerpopSaver.NEXT_RAW_ITEM_EDGE_NAME;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

public class TinkerpopSaverTest {

  private static final String VRE_NAME = "vre";
  private static final int MAX_VERTICES_PER_TRANSACTION = 100;
  private Vres vres;
  private GraphWrapper graphWrapper;
  private Vertex rawCollection;

  @Before
  public void setUp() throws Exception {
    vres = mock(Vres.class);
    graphWrapper = newGraph().withVertex(v -> v.withProperty("name", "rawCollection")).wrap();
    rawCollection = graphWrapper.getGraph().traversal().V().has("name", "rawCollection").next();
  }

  @Test
  public void theCreationAddsAVreToTheDatabase() {
    new TinkerpopSaver(vres, graphWrapper, VRE_NAME, VRE_NAME, MAX_VERTICES_PER_TRANSACTION, VRE_NAME);

    assertThat(graphWrapper.getGraph().traversal().V().hasLabel(Vre.DATABASE_LABEL).next(), is(
      likeVertex().withProperty(Vre.VRE_NAME_PROPERTY_NAME, VRE_NAME)
    ));
  }

  @Test
  public void addEntityAddsEachEntityToTheCollection() {
    TinkerpopSaver instance = new TinkerpopSaver(vres, graphWrapper, VRE_NAME, VRE_NAME, MAX_VERTICES_PER_TRANSACTION,
      VRE_NAME);

    Vertex first = instance.addEntity(rawCollection, Maps.newHashMap());
    Vertex second = instance.addEntity(rawCollection, Maps.newHashMap());

    List<Vertex> items = Lists.newArrayList(rawCollection.vertices(Direction.OUT, "hasItem"));
    assertThat(items, containsInAnyOrder(first, second));
  }

  @Test
  public void addEntityCreatesAChainOfEntities() {
    final TinkerpopSaver instance = new TinkerpopSaver(vres, graphWrapper, VRE_NAME, VRE_NAME,
      MAX_VERTICES_PER_TRANSACTION, VRE_NAME);

    Vertex first = instance.addEntity(rawCollection, Maps.newHashMap());
    Vertex second = instance.addEntity(rawCollection, Maps.newHashMap());
    Vertex third = instance.addEntity(rawCollection, Maps.newHashMap());

    List<Vertex> orderedList = graphWrapper.getGraph().traversal().V(rawCollection.id())
      .emit()
      .repeat(__.out(NEXT_RAW_ITEM_EDGE_NAME))
      .toList();
    assertThat(orderedList, contains(
      rawCollection,
      first,
      second,
      third
    ));
  }

  @Test
  public void addEntityAddsARelationThatIndicatesTheFirstRelation() {
    TinkerpopSaver instance = new TinkerpopSaver(vres, graphWrapper, VRE_NAME, VRE_NAME, MAX_VERTICES_PER_TRANSACTION,
      VRE_NAME);

    Vertex first = instance.addEntity(rawCollection, Maps.newHashMap());
    instance.addEntity(rawCollection, Maps.newHashMap());

    List<Vertex> hasFirstItems = Lists.newArrayList(rawCollection.vertices(Direction.OUT, NEXT_RAW_ITEM_EDGE_NAME));
    assertThat(hasFirstItems, hasSize(1));
    assertThat(hasFirstItems, contains(first));
  }

  @Test
  public void addEntityAppendsTheNewVertexToThePrevious() {
    final TinkerpopSaver instance = new TinkerpopSaver(vres, graphWrapper, VRE_NAME, VRE_NAME,
      MAX_VERTICES_PER_TRANSACTION, VRE_NAME);

    final Vertex first = instance.addEntity(rawCollection, Maps.newHashMap());
    final Vertex second = instance.addEntity(rawCollection, Maps.newHashMap());
    final Vertex third = instance.addEntity(rawCollection, Maps.newHashMap());

    assertThat(first.vertices(Direction.OUT, NEXT_RAW_ITEM_EDGE_NAME).hasNext(), is(true));
    assertThat(first.vertices(Direction.OUT, NEXT_RAW_ITEM_EDGE_NAME).next(), is(second));
    assertThat(second.vertices(Direction.OUT, NEXT_RAW_ITEM_EDGE_NAME).hasNext(), is(true));
    assertThat(second.vertices(Direction.OUT, NEXT_RAW_ITEM_EDGE_NAME).next(), is(third));
  }

  @Test
  public void addPropertyDescriptionsAddsThePropertyDescriptionsToTheCollection() {
    final TinkerpopSaver instance = new TinkerpopSaver(vres, graphWrapper, VRE_NAME, VRE_NAME,
      MAX_VERTICES_PER_TRANSACTION, VRE_NAME);
    ImportPropertyDescriptions importPropertyDescriptions = new ImportPropertyDescriptions();
    importPropertyDescriptions.getOrCreate(6).setPropertyName("first");
    importPropertyDescriptions.getOrCreate(5).setPropertyName("second");
    importPropertyDescriptions.getOrCreate(7).setPropertyName("third");

    instance.addPropertyDescriptions(rawCollection, importPropertyDescriptions);

    List<Vertex> properties = graphWrapper.getGraph().traversal().V(rawCollection.id()).out("hasProperty").toList();
    assertThat(properties, hasSize(3));
    assertThat(properties, containsInAnyOrder(
      likeVertex().withProperty("order", 0).withProperty("id", 6).withProperty("name", "first"),
      likeVertex().withProperty("order", 1).withProperty("id", 5).withProperty("name", "second"),
      likeVertex().withProperty("order", 2).withProperty("id", 7).withProperty("name", "third")
    ));
  }

  @Test
  public void addPropertyDescriptionsStoresTheOrderOfThePropertyDescriptions() {
    final TinkerpopSaver instance = new TinkerpopSaver(vres, graphWrapper, VRE_NAME, VRE_NAME,
      MAX_VERTICES_PER_TRANSACTION, VRE_NAME);
    ImportPropertyDescriptions importPropertyDescriptions = new ImportPropertyDescriptions();
    importPropertyDescriptions.getOrCreate(6).setPropertyName("first");
    importPropertyDescriptions.getOrCreate(5).setPropertyName("second");
    importPropertyDescriptions.getOrCreate(7).setPropertyName("third");

    instance.addPropertyDescriptions(rawCollection, importPropertyDescriptions);

    Iterator<Vertex> hasFirstProperty = rawCollection.vertices(Direction.OUT, "hasFirstProperty");
    assertThat(hasFirstProperty.hasNext(), is(true));
    Vertex first = hasFirstProperty.next();
    assertThat(first, likeVertex().withProperty("id", 6).withProperty("name", "first"));
    Iterator<Vertex> hasNextProperty = first.vertices(Direction.OUT, "hasNextProperty");
    assertThat(hasNextProperty.hasNext(), is(true));
    Vertex second = hasNextProperty.next();
    assertThat(second, is(likeVertex().withProperty("id", 5).withProperty("name", "second")));
    Iterator<Vertex> hasNextProperty2 = second.vertices(Direction.OUT, "hasNextProperty");
    assertThat(hasNextProperty2.hasNext(), is(true));
    assertThat(hasNextProperty2.next(), is(likeVertex().withProperty("id", 7).withProperty("name", "third")));

  }


}
