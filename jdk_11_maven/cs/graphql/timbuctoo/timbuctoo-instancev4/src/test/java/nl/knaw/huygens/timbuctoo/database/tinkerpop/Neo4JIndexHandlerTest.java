package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.core.dto.QuickSearch;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Transaction;

import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.EdgeMatcher.likeEdge;
import static nl.knaw.huygens.hamcrest.OptionalPresentMatcher.present;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Neo4JIndexHandlerTest {

  private static final String COLLECTION = "collection";
  private Collection collection;

  @Before
  public void setUp() throws Exception {
    collection = mock(Collection.class);
    when(collection.getCollectionName()).thenReturn(COLLECTION);
  }

  //=====================quick search index=====================
  @Test
  public void hasQuickSearchIndexForReturnsTrueIfTheIndexExists() {
    TinkerPopGraphManager tinkerPopGraphManager = newGraph().wrap();
    createIndexFor(tinkerPopGraphManager, collection);
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);

    boolean existingIndex = instance.hasQuickSearchIndexFor(collection);

    assertThat(existingIndex, is(true));
  }

  private void createIndexFor(TinkerPopGraphManager tinkerPopGraphManager, Collection collection) {
    Transaction tx = tinkerPopGraphManager.getGraphDatabase().beginTx();
    tinkerPopGraphManager.getGraphDatabase().index().forNodes(collection.getCollectionName());
    tx.close();
  }

  @Test
  public void hasQuickSearchForReturnsFalseIfTheIndexDoesNotExist() {
    TinkerPopGraphManager tinkerPopGraphManager = newGraph().wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);

    Collection collectionWithoutIndex = mock(Collection.class);
    when(collectionWithoutIndex.getCollectionName()).thenReturn(COLLECTION);

    boolean existingIndex = instance.hasQuickSearchIndexFor(collectionWithoutIndex);

    assertThat(existingIndex, is(false));
  }

  @Test
  public void findByQuickSearchRetrievesTheVerticesFromTheIndexAndCreatesTraversalForThem() {
    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();
    String id3 = UUID.randomUUID().toString();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1)
        .withProperty("displayName", "query"))
      .withVertex(v -> v
        .withTimId(id2)
        .withProperty("displayName", "query2"))
      .withVertex(v -> v
        .withTimId(id3)
        .withProperty("displayName", "notmatching")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id2).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id3).next());
    QuickSearch quickSearch = QuickSearch.fromQueryString("query*");

    GraphTraversal<Vertex, Vertex> vertices = instance.findByQuickSearch(collection, quickSearch);

    assertThat(vertices.map(v -> v.get().value("tim_id")).toList(), containsInAnyOrder(id1, id2));
  }

  @Test
  public void findByQuickSearchIsCaseInsensitive() {
    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();
    String id3 = UUID.randomUUID().toString();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1)
        .withProperty("displayName", "query"))
      .withVertex(v -> v
        .withTimId(id2)
        .withProperty("displayName", "QUERY2"))
      .withVertex(v -> v
        .withTimId(id3)
        .withProperty("displayName", "notmatching")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id2).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id3).next());
    QuickSearch quickSearch = QuickSearch.fromQueryString("query*");

    GraphTraversal<Vertex, Vertex> vertices = instance.findByQuickSearch(collection, quickSearch);

    assertThat(vertices.map(v -> v.get().value("tim_id")).toList(), containsInAnyOrder(id1, id2));
  }

  @Test
  public void findByQuickSearchReturnsAnEmtptyTraversalWhenNoVerticesAreFound() {
    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();
    String id3 = UUID.randomUUID().toString();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1)
        .withProperty("displayName", "query"))
      .withVertex(v -> v
        .withTimId(id2)
        .withProperty("displayName", "query2"))
      .withVertex(v -> v
        .withTimId(id3)
        .withProperty("displayName", "other")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id2).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id3).next());
    QuickSearch quickSearch = QuickSearch.fromQueryString("queryWithoutResult");

    GraphTraversal<Vertex, Vertex> vertices = instance.findByQuickSearch(collection, quickSearch);

    assertThat(vertices.map(v -> v.get().value("tim_id")).toList(), is(empty()));
  }

  @Test
  public void findKeywordsByQuickSearchFiltersTheIndexResultsOnTheRightKeywordType() {
    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();
    String id3 = UUID.randomUUID().toString();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1)
        .withProperty("keyword_type", "keywordType")
        .withProperty("displayName", "query"))
      .withVertex(v -> v
        .withProperty("keyword_type", "otherType")
        .withTimId(id2)
        .withProperty("displayName", "query2"))
      .withVertex(v -> v
        .withProperty("keyword_type", "otherType")
        .withTimId(id3)
        .withProperty("displayName", "notmatching")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id2).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id3).next());
    QuickSearch quickSearch = QuickSearch.fromQueryString("query");

    GraphTraversal<Vertex, Vertex> vertices =
      instance.findKeywordsByQuickSearch(collection, quickSearch, "keywordType");

    assertThat(vertices.map(v -> v.get().value("tim_id")).toList(), containsInAnyOrder(id1));
  }

  @Test
  public void findKeywordsByQuickSearchDoesNotFilterOnKeywordTypeWhenKeywordtypesIsNull() {
    String id1 = UUID.randomUUID().toString();
    String id2 = UUID.randomUUID().toString();
    String id3 = UUID.randomUUID().toString();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1)
        .withProperty("keyword_type", "keywordType")
        .withProperty("displayName", "query"))
      .withVertex(v -> v
        .withProperty("keyword_type", "otherType")
        .withTimId(id2)
        .withProperty("displayName", "query2"))
      .withVertex(v -> v
        .withProperty("keyword_type", "otherType")
        .withTimId(id3)
        .withProperty("displayName", "notmatching")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id2).next());
    addToQuickSearchIndex(instance, collection,
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id3).next());
    QuickSearch quickSearch = QuickSearch.fromQueryString("query");

    GraphTraversal<Vertex, Vertex> vertices =
      instance.findKeywordsByQuickSearch(collection, quickSearch, null);

    assertThat(vertices.map(v -> v.get().value("tim_id")).toList(), containsInAnyOrder(id1, id2));
  }

  @Test
  public void removeFromQuickSearchIndexRemovesTheVertexFromTheIndex() {
    String id1 = UUID.randomUUID().toString();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1)
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Vertex vertex = tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).next();
    instance.upsertIntoQuickSearchIndex(collection, "query", vertex, null);
    GraphTraversal<Vertex, Vertex> beforeRemoval =
      instance.findByQuickSearch(collection, QuickSearch.fromQueryString("query"));
    assertThat(beforeRemoval.hasNext(), is(true));

    instance.removeFromQuickSearchIndex(collection, vertex);

    GraphTraversal<Vertex, Vertex> afterRemoval =
      instance.findByQuickSearch(collection, QuickSearch.fromQueryString("query"));
    assertThat(afterRemoval.hasNext(), is(false));
  }

  @Test
  public void upsertIntoQuickSearchIndexMakesSureTheExistingEntryIsUpdated() {
    String id1 = UUID.randomUUID().toString();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1)
        .withProperty("rev", 1)
      )
      .withVertex(v -> v
        .withTimId(id1)
        .withProperty("rev", 2)
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Vertex vertex = tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).has("rev", 1).next();
    instance.upsertIntoQuickSearchIndex(collection, "firstValue", vertex, null);
    assertThat(instance.findByQuickSearch(collection, QuickSearch.fromQueryString("firstValue")).hasNext(), is(true));
    Vertex newVersion = tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1).has("rev", 2).next();

    instance.upsertIntoQuickSearchIndex(collection, "secondValue", newVersion, vertex);

    assertThat(instance.findByQuickSearch(collection, QuickSearch.fromQueryString("firstValue")).hasNext(), is(false));
    assertThat(instance.findByQuickSearch(collection, QuickSearch.fromQueryString("secondValue")).hasNext(), is(true));
  }

  private void addToQuickSearchIndex(Neo4jIndexHandler instance, Collection collection, Vertex vertex) {
    instance.upsertIntoQuickSearchIndex(collection, vertex.value("displayName"), vertex, null);
  }

  //=====================tim_id index=====================
  @Test
  public void findByIdReturnsTheVertexWithId() {
    UUID id1 = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1.toString())
        .withProperty("keyword_type", "keywordType")
        .withProperty("displayName", "query")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Vertex vertex = tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1.toString()).next();
    instance.upsertIntoIdIndex(id1, vertex);

    Optional<Vertex> result = instance.findById(id1);

    assertThat(result.get().value("tim_id"), is(id1.toString()));
  }

  @Test
  public void findByIdReturnsAnEmptyTraversalWhenNoResultsAreFound() {
    UUID id1 = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1.toString())
        .withProperty("keyword_type", "keywordType")
        .withProperty("displayName", "query")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);

    Optional<Vertex> result = instance.findById(id1);

    assertThat(result.isPresent(), is(false));
  }

  @Test
  public void upsertIntoIdIndexRemovesTheOldIndexEntryAndAddsANewOne() {
    UUID id = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id.toString())
        .withProperty("rev", 1)
      )
      .withVertex(v -> v
        .withTimId(id.toString())
        .withProperty("rev", 2)
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Vertex vertex = tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id.toString()).has("rev", 1).next();
    instance.upsertIntoIdIndex(id, vertex);
    assertThat(instance.findById(id).isPresent(), is(true));
    Vertex updatedVertex =
      tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id.toString()).has("rev", 2).next();

    instance.upsertIntoIdIndex(id, updatedVertex);

    Optional<Vertex> updatedVertexOpt = instance.findById(id);
    assertThat(updatedVertexOpt, is(present()));
    assertThat(updatedVertexOpt.get(), likeVertex().withProperty("rev", 2));
  }

  @Test
  public void removeFromIdIndexRemovesTheVertexFromTheIndex() {
    UUID id1 = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withTimId(id1.toString())
        .withProperty("keyword_type", "keywordType")
        .withProperty("displayName", "query")
      )
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Vertex vertex = tinkerPopGraphManager.getGraph().traversal().V().has("tim_id", id1.toString()).next();
    instance.upsertIntoIdIndex(id1, vertex);
    assertThat(instance.findById(id1).isPresent(), is(true));

    instance.removeFromIdIndex(vertex);

    assertThat(instance.findById(id1).isPresent(), is(false));
  }

  //=====================tim_id edge index=====================
  @Test
  public void findEdgeByIdReturnsTheEdgeWithId() {
    UUID edgeId = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withOutgoingRelation("rel", "other", e -> e.withTim_id(edgeId))
      )
      .withVertex("other", v -> {
      })
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Edge edge = tinkerPopGraphManager.getGraph().traversal().E().has("tim_id", edgeId.toString()).next();
    instance.upsertIntoEdgeIdIndex(edgeId, edge);

    Optional<Edge> edgeOpt = instance.findEdgeById(edgeId);

    assertThat(edgeOpt, is(present()));
    assertThat(edgeOpt.get(), is(likeEdge().withId(edgeId.toString())));

  }

  @Test
  public void findEdgeByIdReturnsAnEmtptyOptionalWhenTheEdgeIsNotFound() {
    UUID edgeId = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withOutgoingRelation("rel", "other", e -> e.withTim_id(edgeId))
      )
      .withVertex("other", v -> {
      })
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Edge edge = tinkerPopGraphManager.getGraph().traversal().E().has("tim_id", edgeId.toString()).next();
    instance.upsertIntoEdgeIdIndex(edgeId, edge);
    UUID otherEdgeId = UUID.randomUUID();

    Optional<Edge> edgeOpt = instance.findEdgeById(otherEdgeId);

    assertThat(edgeOpt, is(not(present())));
  }

  @Test
  public void upsertIntoEdgeIdIndexRemovesTheOldEntryAndAddsANewOne() {
    UUID edgeId = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withOutgoingRelation("rel", "other", e -> e.withTim_id(edgeId).withRev(1))
        .withOutgoingRelation("rel", "other", e -> e.withTim_id(edgeId).withRev(2))
      )
      .withVertex("other", v -> {
      })
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Edge edge1 = tinkerPopGraphManager.getGraph().traversal().E().has("rev", 1).next();
    instance.upsertIntoEdgeIdIndex(edgeId, edge1);
    Optional<Edge> edgeOpt = instance.findEdgeById(edgeId);
    assertThat(edgeOpt, is(present()));
    assertThat(edgeOpt.get(), is(likeEdge().withProperty("rev", 1)));

    Edge edge2 = tinkerPopGraphManager.getGraph().traversal().E().has("rev", 2).next();

    instance.upsertIntoEdgeIdIndex(edgeId, edge2);

    Optional<Edge> edgeOpt2 = instance.findEdgeById(edgeId);
    assertThat(edgeOpt2, is(present()));
    assertThat(edgeOpt2.get(), is(likeEdge().withProperty("rev", 2)));
  }

  @Test
  public void removeFromEdgeIdIndexRemovesTheVertexFromTheIndex() {
    UUID edgeId = UUID.randomUUID();
    TinkerPopGraphManager tinkerPopGraphManager = newGraph()
      .withVertex(v -> v
        .withOutgoingRelation("rel", "other", e -> e.withTim_id(edgeId))
      )
      .withVertex("other", v -> {
      })
      .wrap();
    Neo4jIndexHandler instance = new Neo4jIndexHandler(tinkerPopGraphManager);
    Edge edge = tinkerPopGraphManager.getGraph().traversal().E().has("tim_id", edgeId.toString()).next();
    instance.upsertIntoEdgeIdIndex(edgeId, edge);
    Optional<Edge> edgeOpt = instance.findEdgeById(edgeId);
    assertThat(edgeOpt, is(present()));
    assertThat(edgeOpt.get(), is(likeEdge().withId(edgeId.toString())));

    instance.removeEdgeFromIdIndex(edge);

    assertThat(instance.findEdgeById(edgeId), is(not(present())));
  }

}
