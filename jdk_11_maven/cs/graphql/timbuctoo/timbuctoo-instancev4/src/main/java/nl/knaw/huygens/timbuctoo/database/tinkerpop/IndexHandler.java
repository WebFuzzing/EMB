package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.core.dto.QuickSearch;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Optional;
import java.util.UUID;

public interface IndexHandler {
  //=====================quick search index=====================
  boolean hasQuickSearchIndexFor(Collection collection);

  GraphTraversal<Vertex, Vertex> findByQuickSearch(Collection collection, QuickSearch quickSearch);

  GraphTraversal<Vertex, Vertex> findKeywordsByQuickSearch(Collection collection, QuickSearch quickSearch,
                                                           String keywordType);

  /**
   * This method adds or update a index entry and makes shure the index have only one entry for each vertex.
   */
  void upsertIntoQuickSearchIndex(Collection collection, String quickSearchValue, Vertex vertex, Vertex oldVertex);

  void removeFromQuickSearchIndex(Collection collection, Vertex vertex);

  void deleteQuickSearchIndex(Collection collection);

  //=====================tim_id index=====================
  Optional<Vertex> findById(UUID timId);

  /**
   * This method does not prevent multiple entries for a vertex.
   * Use {@link #upsertIntoIdIndex(UUID, Vertex)}
   */
  @Deprecated
  void insertIntoIdIndex(UUID timId, Vertex vertex);

  /**
   * This method adds or update a index entry and makes sure the index have only one entry for each vertex.
   */
  void upsertIntoIdIndex(UUID timId, Vertex vertex);

  void removeFromIdIndex(Vertex vertex);

  //=====================Edge tim_id index=====================
  Optional<Edge> findEdgeById(UUID edgeId);

  /**
   * This method adds or update a index entry and makes sure the index have only one entry for each edge.
   */
  void upsertIntoEdgeIdIndex(UUID edgeId, Edge edge);

  void removeEdgeFromIdIndex(Edge edge);
}
