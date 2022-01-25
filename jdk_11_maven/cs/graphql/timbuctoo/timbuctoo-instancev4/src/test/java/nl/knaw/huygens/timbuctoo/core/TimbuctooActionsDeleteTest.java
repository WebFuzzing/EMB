package nl.knaw.huygens.timbuctoo.core;

import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.core.dto.ImmutableEntityLookup;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.security.dto.UserStubs.userWithId;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class TimbuctooActionsDeleteTest {

  public static final String USER_ID = "userId";
  public static final UUID ID = UUID.randomUUID();
  public static final int REV = 1;
  public static final String COLLECTION_NAME = "collectionName";
  private Clock clock;
  private RedirectionService redirectionService;
  private Collection collection;
  private Instant instant;
  private Change change;
  private DataStoreOperations dataStoreOperations;
  private AfterSuccessTaskExecutor afterSuccessTaskExecutor;

  @Before
  public void setUp() throws Exception {
    clock = mock(Clock.class);
    instant = Instant.now();
    when(clock.instant()).thenReturn(instant);
    redirectionService = mock(RedirectionService.class);
    collection = mock(Collection.class);
    when(collection.getCollectionName()).thenReturn(COLLECTION_NAME);
    change = new Change();
    change.setUserId(USER_ID);
    change.setTimeStamp(instant.toEpochMilli());
    dataStoreOperations = mock(DataStoreOperations.class);
    afterSuccessTaskExecutor = mock(AfterSuccessTaskExecutor.class);
  }

  @Test
  public void deleteEntityLetsDataAccessDeleteTheEntity() throws Exception {
    TimbuctooActions instance = createInstance(true);

    instance.deleteEntity(collection, ID, userWithId(USER_ID));

    verify(dataStoreOperations).deleteEntity(collection, ID, change);
  }

  @Test(expected = PermissionFetchingException.class)
  public void deleteEntityThrowsAnUnAuthrozedExceptionIfTheUserIsNotAllowedToWriteTheCollection() throws Exception {
    TimbuctooActions instance = createInstance(false);

    try {
      instance.deleteEntity(collection, ID, userWithId(USER_ID));
    } finally {
      verifyNoInteractions(dataStoreOperations);
    }
  }

  @Test(expected = NotFoundException.class)
  public void deleteEntityThrowsANotFoundExceptionWhenTheEntityCannotBeFound() throws Exception {
    doThrow(new NotFoundException()).when(dataStoreOperations).deleteEntity(collection, ID, change);
    TimbuctooActions instance = createInstance(true);

    instance.deleteEntity(collection, ID, userWithId(USER_ID));
  }

  @Test
  public void deleteEntityAddsAPidAfterTheEntityIsDeleted() throws Exception {
    when(dataStoreOperations.deleteEntity(collection, ID, change)).thenReturn(REV);
    TimbuctooActions instance = createInstance(true);

    instance.deleteEntity(collection, ID, userWithId(USER_ID));

    verify(afterSuccessTaskExecutor).addTask(
      new TimbuctooActions.AddPersistentUrlTask(
        redirectionService,
        URI.create("http://example.org/persistent"),
        ImmutableEntityLookup.builder().collection(COLLECTION_NAME).timId(ID).rev(REV).build()
      )
    );
  }

  private TimbuctooActions createInstance(boolean allowedToWrite) throws PermissionFetchingException {
    PermissionFetcher permissionFetcher = mock(PermissionFetcher.class);
    if (allowedToWrite) {
      given(permissionFetcher.getOldPermissions(any(), any())).willReturn(
        Sets.newHashSet(Permission.WRITE, Permission.READ));
    } else {
      given(permissionFetcher.getOldPermissions(any(),any())).willReturn(
        Sets.newHashSet(Permission.READ));
    }
    return new TimbuctooActions(permissionFetcher, clock, redirectionService,
      (coll, id, rev) -> URI.create("http://example.org/persistent"), dataStoreOperations, afterSuccessTaskExecutor);
  }


}
