package nl.knaw.huygens.timbuctoo.database.tinkerpop.changelistener;

import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.core.DataStoreOperations;
import nl.knaw.huygens.timbuctoo.core.dto.CreateEntityStubs;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateEntity;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperations;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperationsStubs;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperationsStubs.forDeleteCall;
import static nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperationsStubs.forReplaceCall;
import static nl.knaw.huygens.timbuctoo.database.tinkerpop.VertexDuplicator.IS_LATEST;
import static nl.knaw.huygens.timbuctoo.database.tinkerpop.VertexDuplicator.VERSION_OF;
import static nl.knaw.huygens.timbuctoo.util.StreamIterator.stream;
import static nl.knaw.huygens.timbuctoo.util.VertexMatcher.likeVertex;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ChangeListenerTest {
  @Test
  public void callsOnCreateAndOnAddToCollectionOnCreateEntity() throws Exception {
    ChangeListener changeListener = new ChangeListenerImpl(vertex -> {
      assertThat(vertex, likeVertex()
        .withProperty(IS_LATEST, true)
        .withProperty("rev", 1)
      );

      Long prevVersions = stream(vertex.vertices(Direction.BOTH, VERSION_OF)).collect(Collectors.counting());
      assertThat(prevVersions, is(1L));
    });
    ChangeListener spy = spy(changeListener);
    DataStoreOperations instance = TinkerPopOperationsStubs.forChangeListenerMock(spy);

    Collection collectionMock = mock(Collection.class);
    Collection baseCollectionMock = mock(Collection.class);
    instance.createEntity(collectionMock, Optional.of(baseCollectionMock), CreateEntityStubs.dummy());

    verify(spy).onCreate(same(collectionMock), any());
    verify(spy).onAddToCollection(same(collectionMock), eq(Optional.empty()), any());
    verify(spy).onAddToCollection(same(baseCollectionMock), eq(Optional.empty()), any());
  }

  @Test
  public void callsOnPropertyUpdateOnReplaceEntity() throws Exception {
    ChangeListener changeListener = new ChangeListenerImpl(vertex -> {
      assertThat(vertex, likeVertex()
        .withProperty("isLatest", true)
        .withProperty("rev", 2)
      );

      Long prevVersions = stream(vertex.vertices(Direction.BOTH, VERSION_OF)).collect(Collectors.counting());
      assertThat(prevVersions, is(1L));
    });
    UUID id = UUID.randomUUID();
    ChangeListener spy = spy(changeListener);
    DataStoreOperations instance = forReplaceCall(spy, id, 1);

    UpdateEntity updateEntity = new UpdateEntity(id, newArrayList(), 1);
    updateEntity.setModified(new Change());

    Collection collectionMock = mock(Collection.class);
    instance.replaceEntity(collectionMock, updateEntity);

    verify(spy).onPropertyUpdate(same(collectionMock), any(), any());
  }

  @Test
  public void callsOnAddToCollectionOnReplaceEntityWithNewCollection() throws Exception {
    ChangeListener changeListener = new ChangeListenerImpl(vertex -> {
      assertThat(vertex, likeVertex()
        .withProperty("isLatest", true)
        .withProperty("rev", 2)
      );

      Long prevVersions = stream(vertex.vertices(Direction.BOTH, VERSION_OF)).collect(Collectors.counting());
      assertThat(prevVersions, is(1L));
    });
    UUID id = UUID.randomUUID();
    ChangeListener spy = spy(changeListener);
    DataStoreOperations instance = forReplaceCall(spy, id, 1);

    UpdateEntity updateEntity = new UpdateEntity(id, newArrayList(), 1);
    updateEntity.setModified(new Change());

    Collection collectionMock = mock(Collection.class);
    given(collectionMock.getEntityTypeName()).willReturn("something");
    instance.replaceEntity(collectionMock, updateEntity);

    verify(spy).onAddToCollection(same(collectionMock), any(), any());
  }

  @Test
  public void callsOnRemoveFromCollectionOnDeleteEntity() throws Exception {
    ChangeListener changeListener = new ChangeListenerImpl(vertex -> {
      assertThat(vertex, likeVertex()
        .withProperty("isLatest", true)
        .withProperty("rev", 2)
      );

      Long prevVersions = stream(vertex.vertices(Direction.BOTH, VERSION_OF)).collect(Collectors.counting());
      assertThat(prevVersions, is(1L));
    });

    UUID id = UUID.randomUUID();

    ChangeListener spy = spy(changeListener);
    DataStoreOperations instance = forDeleteCall(spy, id, 1, "someName");

    Collection collection = mock(Collection.class);
    when(collection.getEntityTypeName()).thenReturn("someName");
    when(collection.getVre()).thenReturn(mock(Vre.class));
    instance.deleteEntity(collection, id, new Change());

    verify(spy).onRemoveFromCollection(same(collection), any(), any());
  }

  @Test
  public void isCalledForAllMethods() {
    Set<String> knownMethods = Sets.newHashSet(
      //these should have implementations
      "createEntity",
      "replaceEntity",
      "deleteEntity",
      "finishEntities",

      //these are ignored
      "addPredicateValueTypeVertexToVre",
      "removeCollectionsAndEntities",
      "replaceRelation",
      "acceptRelation",
      "doQuickSearch",
      "doKeywordQuickSearch",
      "clearMappingErrors",
      "hasMappingErrors",
      "saveRmlMappingState",
      "setAdminCollection",
      "getMappingErrors",
      "setVrePublishState",
      "setVreMetadata",
      "setVreImage",
      "getVreImageBlob",
      "deleteVre",

      "close",
      "success",
      "rollback",
      "getEntity",
      "getCollection",
      "loadVres",
      "databaseIsEmptyExceptForMigrations",
      "initDb",
      "saveRelationTypes",
      "saveVre",
      "ensureVreExists",
      "addPid",
      "getEntityByRdfUri",
      "getRelationTypes",
      "addCollectionToVre",
      "getPredicatesFor",
      "retractProperty",
      "assertProperty",
      "addPropertiesToCollection",
      "getEntitiesWithUnknownType",
      "retrieveProperty",
      "addTypeToEntity",
      "moveEdges"
    );
    Method[] allMethods = TinkerPopOperations.class.getDeclaredMethods();
    for (Method method : allMethods) {
      if (Modifier.isPublic(method.getModifiers())) {
        if (!knownMethods.contains(method.getName())) {
          throw new IllegalStateException("This test is not implemented for #" + method.getName());
        }
      }
    }
  }

  private class ChangeListenerImpl implements ChangeListener {

    private final Consumer<Vertex> validateVertex;

    private ChangeListenerImpl(Consumer<Vertex> validateVertex) {
      this.validateVertex = validateVertex;
    }

    @Override
    public void onCreate(Collection collection, Vertex vertex) {
      validateVertex.accept(vertex);
    }

    @Override
    public void onPropertyUpdate(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
      validateVertex.accept(newVertex);
    }

    @Override
    public void onRemoveFromCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
      validateVertex.accept(newVertex);
    }

    @Override
    public void onAddToCollection(Collection collection, Optional<Vertex> oldVertex, Vertex newVertex) {
      validateVertex.accept(newVertex);
    }

    @Override
    public void onCreateEdge(Collection collection, Edge edge) {

    }

    @Override
    public void onEdgeUpdate(Collection collection, Edge oldEdge, Edge newEdge) {

    }
  }
}
