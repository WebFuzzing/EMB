package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.core.dto.QuickSearch;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import nl.knaw.huygens.timbuctoo.util.StreamIterator;
import org.apache.lucene.queryparser.flexible.standard.QueryParserUtil;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.EmptyGraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.neo4j.helpers.collection.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

public class Neo4jIndexHandler implements IndexHandler {
  private static final String QUICK_SEARCH_PROP_NAME = "quickSearch";
  private static final String ID_INDEX = "idIndex";
  private static final String TIM_ID = "tim_id";
  private static final Logger LOG = LoggerFactory.getLogger(Neo4jIndexHandler.class);
  private final TinkerPopGraphManager tinkerPopGraphManager;
  private GraphTraversalSource cachedTraversal;

  public Neo4jIndexHandler(TinkerPopGraphManager tinkerPopGraphManager) {
    this.tinkerPopGraphManager = tinkerPopGraphManager;
  }

  //=====================quick search index=====================
  @Override
  public boolean hasQuickSearchIndexFor(Collection collection) {
    return indexManager().existsForNodes(getQuicksearchIndexName(collection));
  }

  @Override
  public GraphTraversal<Vertex, Vertex> findByQuickSearch(Collection collection, QuickSearch quickSearch) {
    return traversalFromIndex(collection, quickSearch);
  }

  @Override
  public GraphTraversal<Vertex, Vertex> findKeywordsByQuickSearch(Collection collection, QuickSearch quickSearch,
                                                                  String keywordType) {
    if (keywordType != null) {
      return traversalFromIndex(collection, quickSearch).has("keyword_type", keywordType);
    }
    return traversalFromIndex(collection, quickSearch);
  }


  private GraphTraversal<Vertex, Vertex> traversalFromIndex(Collection collection, QuickSearch quickSearch) {
    Index<Node> index = getQuickSearchIndex(collection);

    try {
      IndexHits<Node> hits = index.query(QUICK_SEARCH_PROP_NAME, createQuery(quickSearch));
      List<Long> ids = StreamIterator.stream(hits.iterator()).map(h -> h.getId()).collect(toList());
      return ids.isEmpty() ? EmptyGraphTraversal.instance() : traversal().V(ids);
    } catch (Exception e) {
      LOG.error("Unexpected exception during search", e);
      return EmptyGraphTraversal.instance();
    }

  }

  private Index<Node> getQuickSearchIndex(Collection collection) {
    // Add the config below, to make sure the index is case insensitive.
    Map<String, String> indexConfig = MapUtil.stringMap(IndexManager.PROVIDER, "lucene", "type", "fulltext");
    return indexManager().forNodes(getQuicksearchIndexName(collection), indexConfig);
  }

  private Object createQuery(QuickSearch quickSearch) {
    String fullMatches = quickSearch.fullMatches()
      .stream()
      .map(QueryParserUtil::escape)
      .collect(joining());
    String partialMatches = quickSearch.partialMatches()
      .stream()
      .map(QueryParserUtil::escape)
      .map(s -> s + "*")
      .collect(joining());
    if (fullMatches.isEmpty() && partialMatches.isEmpty()) {
      return "*";
    } else {
      return fullMatches + " " + partialMatches;
    }
  }

  @Override
  public void removeFromQuickSearchIndex(Collection collection, Vertex vertex) {
    Index<Node> index = getQuickSearchIndex(collection);
    // make sure only this field is removed from the index.
    final Optional<Node> node = vertexToNode(vertex);
    if (node.isPresent()) {
      index.remove(node.get(), QUICK_SEARCH_PROP_NAME);
    }
  }

  @Override
  public void upsertIntoQuickSearchIndex(Collection collection, String quickSearchValue, Vertex vertex,
                                         Vertex oldVertex) {
    if (oldVertex != null) {
      this.removeFromQuickSearchIndex(collection, oldVertex);
    }

    Index<Node> index = getQuickSearchIndex(collection);

    vertexToNode(vertex).ifPresent(node -> index.add(node, QUICK_SEARCH_PROP_NAME, quickSearchValue));
  }

  @Override
  public void deleteQuickSearchIndex(Collection collection) {
    Index<Node> index = getQuickSearchIndex(collection);
    index.delete();
  }

  private String getQuicksearchIndexName(Collection collection) {
    return collection.getCollectionName();
  }

  //=====================tim_id index=====================
  @Override
  public Optional<Vertex> findById(UUID timId) {
    IndexHits<Node> hits = getIdIndex().query(TIM_ID, timId.toString());

    return hits.hasNext() ? traversal().V(hits.next().getId()).tryNext() : Optional.empty();
  }

  @Override
  @Deprecated
  public void insertIntoIdIndex(UUID timId, Vertex vertex) {
    Index<Node> index = getIdIndex();
    final Optional<Node> node = vertexToNode(vertex);
    if (node.isPresent()) {
      index.add(node.get(), TIM_ID, timId.toString());
    }
  }

  @Override
  public void upsertIntoIdIndex(UUID timId, Vertex vertex) {
    findById(timId).ifPresent(this::removeFromIdIndex);
    insertIntoIdIndex(timId, vertex);
  }

  @Override
  public void removeFromIdIndex(Vertex vertex) {
    // make sure only this field is removed from the index.
    final Optional<Node> node = vertexToNode(vertex);
    if (node.isPresent()) {
      getIdIndex().remove(node.get(), TIM_ID);
    }
  }

  private Index<Node> getIdIndex() {
    return indexManager().forNodes(ID_INDEX);
  }


  //=====================Edge tim_id index=====================
  @Override
  public Optional<Edge> findEdgeById(UUID edgeId) {
    RelationshipIndex edgeIdIndex = getEdgeIdIndex();
    IndexHits<Relationship> hits = edgeIdIndex.query(TIM_ID, edgeId.toString());

    return hits.hasNext() ? traversal().E(hits.next().getId()).tryNext() : Optional.empty();
  }

  @Override
  public void upsertIntoEdgeIdIndex(UUID edgeId, Edge edge) {
    Optional<Edge> prevEdge = findEdgeById(edgeId);
    prevEdge.ifPresent(this::removeEdgeFromIdIndex);
    getEdgeIdIndex().add(edgeToRelationship(edge), TIM_ID, edgeId.toString());
  }

  @Override
  public void removeEdgeFromIdIndex(Edge edge) {
    getEdgeIdIndex().remove(edgeToRelationship(edge), TIM_ID);
  }

  private RelationshipIndex getEdgeIdIndex() {
    return indexManager().forRelationships("edgeIdIndex");
  }

  private Relationship edgeToRelationship(Edge edge) {
    return graphDatabase().getRelationshipById((long) edge.id());
  }
  //=====================general helper methods=====================

  private GraphTraversalSource traversal() {
    if (cachedTraversal == null) {
      cachedTraversal = tinkerPopGraphManager.getGraph().traversal();
    }
    return cachedTraversal;
  }

  private GraphDatabaseService graphDatabase() {
    return tinkerPopGraphManager.getGraphDatabase();
  }

  private IndexManager indexManager() {
    return graphDatabase().index();
  }

  // In edge cases it can occur that the actual neo4j node is already deleted, while the instance of Vertex
  // still holds an ID, causing org.neo4j.graphdb.NotFoundException.
  // LOG an error for now, keeping code stable, but maybe exception should be escalated
  private Optional<Node> vertexToNode(Vertex vertex) {
    try {
      final Node nodeById = graphDatabase().getNodeById((long) vertex.id());
      return Optional.of(nodeById);
    } catch (org.neo4j.graphdb.NotFoundException e) {
      LOG.error("Neo4j node with id {} does not exist", vertex.id());
      return Optional.empty();
    }
  }

}
