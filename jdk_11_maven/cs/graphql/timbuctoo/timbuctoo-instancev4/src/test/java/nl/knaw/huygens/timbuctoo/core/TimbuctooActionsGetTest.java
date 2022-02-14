package nl.knaw.huygens.timbuctoo.core;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.dto.DataStream;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearch;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearchResult;
import nl.knaw.huygens.timbuctoo.core.dto.ReadEntity;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.CustomEntityProperties;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.CustomRelationProperties;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.model.vre.vres.VresBuilder;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionStubs.collWithCollectionName;
import static nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionStubs.keywordCollWithCollectionName;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TimbuctooActionsGetTest {

  private static final UUID ID = UUID.randomUUID();
  private static final Integer rev = 1;
  private CustomEntityProperties entityProps;
  private CustomRelationProperties relationProps;
  private TimbuctooActions instance;
  private Collection collection;
  private boolean withRelations = false;
  private DataStoreOperations dataStoreOperations;

  @Before
  public void setUp() throws Exception {
    entityProps = mock(CustomEntityProperties.class);
    relationProps = mock(CustomRelationProperties.class);
    dataStoreOperations = mock(DataStoreOperations.class);
    instance = new TimbuctooActions(null, null, null, (coll, id, rev) -> URI.create("http://example.org/persistent"),
      dataStoreOperations, null);
    collection = mock(Collection.class);
  }

  @Test(expected = NotFoundException.class)
  public void getEntityThrowsANotFoundExceptionWhenTheEntityCannotBeFound() throws Exception {
    when(dataStoreOperations.getEntity(ID, rev, collection, entityProps, relationProps))
      .thenThrow(new NotFoundException());

    instance.getEntity(collection, ID, rev, entityProps, relationProps);
  }

  @Test
  public void getEntityReturnsTheReadEntity() throws Exception {
    ReadEntity readEntity = mock(ReadEntity.class);
    when(dataStoreOperations.getEntity(ID, rev, collection, entityProps, relationProps)).thenReturn(readEntity);

    ReadEntity actualEntity = instance.getEntity(collection, ID, rev, entityProps, relationProps);

    assertThat(actualEntity, is(sameInstance(readEntity)));
  }

  @Test
  public void getCollectionLetsReturnsACollection() {
    DataStream<ReadEntity> entities = mockDataStream();
    int start = 0;
    int rows = 10;
    when(dataStoreOperations.getCollection(collection, start, rows, withRelations, entityProps, relationProps))
      .thenReturn(entities);

    DataStream<ReadEntity> result =
      instance.getCollection(collection, start, rows, withRelations, entityProps, relationProps);

    assertThat(result, is(sameInstance(entities)));
  }

  private DataStream<ReadEntity> mockDataStream() {
    return new DataStream<ReadEntity>() {
      @Override
      public <U> List<U> map(Function<ReadEntity, U> mapping) {
        throw new UnsupportedOperationException("Not implemented yet");
      }
    };
  }

  @Test
  public void doQuickSearchReturnsTheValueOfDataStoreOperations() {
    List<QuickSearchResult> entities = Lists.newArrayList();
    QuickSearch query = QuickSearch.fromQueryString("");
    Collection collection = collWithCollectionName("coll");
    int limit = 1;
    when(dataStoreOperations.doQuickSearch(collection, query, limit)).thenReturn(entities);

    List<QuickSearchResult> searchResult = instance.doQuickSearch(collection, query, null, limit);

    assertThat(searchResult, is(sameInstance(entities)));
  }

  @Test
  public void doQuickSearchCallsDoKeywordQuickSearchWhenTheCollectionIsAKeywordCollection() {
    List<QuickSearchResult> entities = Lists.newArrayList();
    QuickSearch query = QuickSearch.fromQueryString("");
    String keywordType = "";
    Collection collection = keywordCollWithCollectionName("coll");
    int limit = 1;
    when(dataStoreOperations.doKeywordQuickSearch(collection, keywordType, query, limit)).thenReturn(entities);

    List<QuickSearchResult> searchResult = instance.doQuickSearch(collection, query, keywordType, limit);

    assertThat(searchResult, is(sameInstance(entities)));
  }

  //================== Metdata ==================
  @Test
  public void loadVresDelegatesToDataStoreOperationsLoadVres() {
    Vres vres = mock(Vres.class);
    when(dataStoreOperations.loadVres()).thenReturn(vres);

    Vres actualVres = instance.loadVres();

    assertThat(actualVres, is(sameInstance(vres)));
  }

  @Test(expected = InvalidCollectionException.class)
  public void getCollectionMetadataThrowsAnInvalidCollectionExceptionWhenTheCollectionCannotBeFound() throws Exception {
    Vres vres = mock(Vres.class);
    when(dataStoreOperations.loadVres()).thenReturn(vres);

    instance.getCollectionMetadata("unknowncollections");
  }

  @Test
  public void getCollectionMetadataReturnsTheCollectionThatIsRequested() throws Exception {
    when(dataStoreOperations.loadVres()).thenReturn(vresWithCollection("knowncollections"));

    Collection knownCollection = instance.getCollectionMetadata("knowncollections");

    assertThat(knownCollection.getCollectionName(), is("knowncollections"));
  }

  private Vres vresWithCollection(String collectionName) {
    return new VresBuilder()
      .withVre("knownVre", "known", vre -> vre.withCollection(collectionName))
      .build();
  }
}
