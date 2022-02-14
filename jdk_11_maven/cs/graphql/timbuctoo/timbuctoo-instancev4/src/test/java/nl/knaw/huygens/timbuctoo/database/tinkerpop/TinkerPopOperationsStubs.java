package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener.ChangeListener;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;

import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.database.tinkerpop.VertexDuplicator.VERSION_OF;
import static nl.knaw.huygens.timbuctoo.util.TestGraphBuilder.newGraph;
import static org.mockito.Mockito.mock;

public class TinkerPopOperationsStubs {
  public static TinkerPopOperations forGraphWrapper(TinkerPopGraphManager graphManager) {

    return new TinkerPopOperations(graphManager, mock(ChangeListener.class), new GremlinEntityFetcher(), null,
      createIndexHandler(graphManager));
  }

  public static TinkerPopOperations forChangeListenerMock(ChangeListener changeListener) {
    TinkerPopGraphManager graphManager = newGraph().wrap();
    return new TinkerPopOperations(graphManager, changeListener, new GremlinEntityFetcher(), null,
      createIndexHandler(graphManager));
  }

  public static TinkerPopOperations forReplaceCall(ChangeListener changeListener, UUID id, int rev) {
    TinkerPopGraphManager graphManager = newGraph()
      .withVertex("old", vertexBuilder -> vertexBuilder
        .withTimId(id.toString())
        .withProperty("rev", rev)
        .withProperty("isLatest", false)
      )
      .withVertex(vertexBuilder -> vertexBuilder
        .withTimId(id.toString())
        .withProperty("rev", rev)
        .withProperty("isLatest", true)
        .withIncomingRelation(VERSION_OF, "old")
      )
      .wrap();
    return new TinkerPopOperations(graphManager, changeListener, new GremlinEntityFetcher(), null, createIndexHandler(
      graphManager));
  }

  public static TinkerPopOperations forDeleteCall(ChangeListener changeListener, UUID id, int rev,
                                                  String entityTypeName) {
    TinkerPopGraphManager graphManager = newGraph()
      .withVertex("old", vertexBuilder -> vertexBuilder
        .withTimId(id.toString())
        .withProperty("types", "[\"" + entityTypeName + "\"]")
        .withProperty("rev", rev)
        .withProperty("isLatest", false)
      )
      .withVertex(vertexBuilder -> vertexBuilder
        .withTimId(id.toString())
        .withProperty("types", "[\"" + entityTypeName + "\"]")
        .withProperty("rev", rev)
        .withProperty("isLatest", true)
        .withIncomingRelation(VERSION_OF, "old")
      )
      .wrap();
    return new TinkerPopOperations(graphManager, changeListener, new GremlinEntityFetcher(), null, createIndexHandler(
      graphManager));
  }

  public static TinkerPopOperations forGraphWrapperAndMappings(TinkerPopGraphManager graphManager, Vres mappings) {
    return new TinkerPopOperations(graphManager, mock(ChangeListener.class), new GremlinEntityFetcher(), mappings,
      createIndexHandler(graphManager));
  }

  public static TinkerPopOperations newInstance() {
    return forGraphWrapper(newGraph().wrap());
  }

  private static IndexHandler createIndexHandler(TinkerPopGraphManager graphManager) {
    return new Neo4jIndexHandler(graphManager);
  }

  public static TinkerPopOperations forGraphMappingsAndIndex(TinkerPopGraphManager graphManager,
                                                             Vres vres,
                                                             IndexHandler indexHandler) {
    return new TinkerPopOperations(graphManager, mock(ChangeListener.class), new GremlinEntityFetcher(), vres,
      indexHandler);
  }

  static TinkerPopOperations forGraphMappingsAndChangeListener(TinkerPopGraphManager graphManager,
                                                               Vres vres,
                                                               ChangeListener changeListener) {
    return new TinkerPopOperations(graphManager, changeListener, new GremlinEntityFetcher(), vres,
      mock(IndexHandler.class));
  }

  public static TinkerPopOperations forGraphMappingsListenerAndIndex(TinkerPopGraphManager graphManager,
                                                                     Vres vres,
                                                                     ChangeListener changeListener,
                                                                     IndexHandler indexHandler) {
    return new TinkerPopOperations(graphManager, changeListener, new GremlinEntityFetcher(), vres, indexHandler);
  }
}
