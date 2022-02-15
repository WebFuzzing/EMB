package nl.knaw.huygens.timbuctoo.core;

import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.core.dto.ImmutableEntityLookup;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateEntity;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;

import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.security.dto.UserStubs.userWithId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class TimbuctooActionsReplaceTest {

  public static final int NEW_REV = 1;
  public static final String COLLECTION_NAME = "collection";
  public static final UUID ID = UUID.randomUUID();
  private static final String USER_ID = "userId";
  private DataStoreOperations dataStoreOperations;
  private Clock clock;
  private RedirectionService redirectionService;
  private UpdateEntity updateEntity;
  private Collection collection;
  private Instant instant;
  private AfterSuccessTaskExecutor afterSuccessTaskExecutor;

  @Before
  public void setUp() throws Exception {
    clock = mock(Clock.class);
    instant = Instant.now();
    when(clock.instant()).thenReturn(instant);
    redirectionService = mock(RedirectionService.class);
    updateEntity = mock(UpdateEntity.class);
    when(updateEntity.getId()).thenReturn(ID);
    collection = mock(Collection.class);
    when(collection.getCollectionName()).thenReturn(COLLECTION_NAME);
    afterSuccessTaskExecutor = mock(AfterSuccessTaskExecutor.class);
    dataStoreOperations = mock(DataStoreOperations.class);
  }

  @Test(expected = PermissionFetchingException.class)
  public void replaceEntityThrowsAnUnauthorizedExceptionWhenTheUserIsNotAllowedToWrite() throws Exception {
    TimbuctooActions instance = createInstance(false);

    try {
      instance.replaceEntity(collection, updateEntity, userWithId(USER_ID));
    } finally {
      verifyNoInteractions(dataStoreOperations);
    }
  }

  @Test
  public void replaceEntityAddsAHandleAfterASuccessfulUpdate() throws Exception {
    when(dataStoreOperations.replaceEntity(collection, updateEntity)).thenReturn(NEW_REV);
    TimbuctooActions instance = createInstance(true);

    instance.replaceEntity(collection, updateEntity, userWithId(USER_ID));

    InOrder inOrder = inOrder(dataStoreOperations, redirectionService, afterSuccessTaskExecutor);
    inOrder.verify(dataStoreOperations).replaceEntity(collection, updateEntity);
    inOrder.verify(afterSuccessTaskExecutor).addTask(
      new TimbuctooActions.AddPersistentUrlTask(
        redirectionService,
        URI.create("http://example.org/persistent"),
        ImmutableEntityLookup.builder().collection(COLLECTION_NAME).timId(ID).rev(NEW_REV).build()
      )
    );

  }

  @Test
  public void replaceEntityAddsTheModifiedPropertyToUpdateEntityBeforeExecutingTheUpdate() throws Exception {
    TimbuctooActions instance = createInstance(true);

    instance.replaceEntity(collection, updateEntity, userWithId(USER_ID));

    InOrder inOrder = inOrder(dataStoreOperations, updateEntity);
    inOrder.verify(updateEntity).setModified(argThat(allOf(
      hasProperty("timeStamp", is(instant.toEpochMilli())),
      hasProperty("userId", is(USER_ID))
    )));
    inOrder.verify(dataStoreOperations).replaceEntity(collection, updateEntity);
  }

  @Test(expected = NotFoundException.class)
  public void replaceEntityThrowsANotFoundExceptionWhenExecuteAndReturnReturnsAnUpdateStatusNotFound()
    throws Exception {
    when(dataStoreOperations.replaceEntity(collection, updateEntity)).thenThrow(new NotFoundException());
    TimbuctooActions instance = createInstance(true);

    instance.replaceEntity(collection, updateEntity, userWithId(USER_ID));
  }

  @Test(expected = AlreadyUpdatedException.class)
  public void replaceEntityThrowsAnAlreadyUpdatedExceptionWhenExecuteAndReturnReturnsAnUpdateStatusAlreadyUpdated()
    throws Exception {
    when(dataStoreOperations.replaceEntity(collection, updateEntity)).thenThrow(new AlreadyUpdatedException());
    TimbuctooActions instance = createInstance(true);

    instance.replaceEntity(collection, updateEntity, userWithId(USER_ID));
  }

  private TimbuctooActions createInstance(boolean allowedToWrite) throws PermissionFetchingException {
    PermissionFetcher permissionFetcher = mock(PermissionFetcher.class);
    if (allowedToWrite) {
      given(permissionFetcher.getOldPermissions(any(), any())).willReturn(
        Sets.newHashSet(Permission.WRITE, Permission.READ));
    } else {
      given(permissionFetcher.getOldPermissions(any(), any())).willReturn(
        Sets.newHashSet(Permission.READ));
    }
    return new TimbuctooActions(permissionFetcher, clock, redirectionService,
      (coll, id, rev) -> URI.create("http://example.org/persistent"), dataStoreOperations, afterSuccessTaskExecutor);
  }

}
