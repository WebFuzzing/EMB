package nl.knaw.huygens.timbuctoo.core;

import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.core.dto.CreateRelation;
import nl.knaw.huygens.timbuctoo.core.dto.UpdateRelation;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.security.dto.UserStubs.userWithId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class TimbuctooActionsRelationTest {

  private static final String USER_ID = "userId";
  private Clock clock;
  private CreateRelation createRelation;
  private Collection collection;
  private Instant instant;
  private DataStoreOperations dataStoreOperations;

  @Before
  public void setUp() throws Exception {
    clock = mock(Clock.class);
    instant = Instant.now();
    when(clock.instant()).thenReturn(instant);
    createRelation = new CreateRelation(null, null, null);
    collection = mock(Collection.class);
    dataStoreOperations = mock(DataStoreOperations.class);
  }

  @Test
  public void createRelationCreatesANewRelation() throws Exception {
    TimbuctooActions instance = createInstance(true);

    instance.createRelation(collection, createRelation, userWithId(USER_ID));

    verify(dataStoreOperations).acceptRelation(argThat(is(collection)), argThat(allOf(
      hasProperty("created", allOf(
        hasProperty("userId", is(USER_ID)),
        hasProperty("timeStamp", is(instant.toEpochMilli()))
      ))
    )));

  }

  @Test
  public void createRelationReturnsTheIdOfTheNewLyCreatedRelation() throws Exception {
    when(dataStoreOperations.acceptRelation(collection, createRelation))
      .thenReturn(UUID.randomUUID());
    TimbuctooActions instance = createInstance(true);

    UUID id = instance.createRelation(collection, createRelation, userWithId(USER_ID));

    assertThat(id, is(notNullValue(UUID.class)));
  }

  @Test(expected = PermissionFetchingException.class)
  public void createRelationThrowsAnUnauthorizedExceptionWhenTheUserIsNotAllowedToWrite() throws Exception {
    TimbuctooActions instance = createInstance(false);

    try {
      instance.createRelation(collection, createRelation, userWithId(USER_ID));
    } finally {
      verifyNoInteractions(dataStoreOperations);
    }
  }

  @Test(expected = IOException.class)
  public void createRelationsThrowsAnIoExceptionWithTheMessageOfTheReturnValueIfTheRelationsCouldNotBeCreated()
    throws Exception {
    when(dataStoreOperations.acceptRelation(collection, createRelation))
      .thenThrow(new RelationNotPossibleException(""));
    TimbuctooActions instance = createInstance(true);

    instance.createRelation(collection, createRelation, userWithId(USER_ID));
  }

  @Test(expected = PermissionFetchingException.class)
  public void replaceRelationThrowsAnAuthorizationExceptionWhenTheUsersIsNotAllowedToWrite() throws Exception {
    TimbuctooActions instance = createInstance(false);

    try {
      instance.replaceRelation(collection, new UpdateRelation(UUID.randomUUID(), 1, false),
        userWithId(USER_ID));
    } finally {
      verifyNoInteractions(dataStoreOperations);
    }
  }

  @Test
  public void replaceRelationUpdatesARelation() throws Exception {
    UUID id = UUID.randomUUID();
    UpdateRelation updateRelation = new UpdateRelation(id, 1, false);
    TimbuctooActions instance = createInstance(true);

    instance.replaceRelation(collection, updateRelation, userWithId(USER_ID));

    verify(dataStoreOperations).replaceRelation(argThat(is(collection)), argThat(allOf(
      hasProperty("id", is(id)),
      hasProperty("modified", allOf(
        hasProperty("userId", is(USER_ID)),
        hasProperty("timeStamp", is(instant.toEpochMilli()))
      ))
    )));
  }

  @Test(expected = NotFoundException.class)
  public void replaceRelationThrowsANotFoundExceptionWhenTheRelationCannotBeFound() throws Exception {
    UpdateRelation updateRelation = new UpdateRelation(null, 1, false);
    doThrow(new NotFoundException()).when(dataStoreOperations).replaceRelation(collection, updateRelation);
    TimbuctooActions instance = createInstance(true);

    instance.replaceRelation(collection, updateRelation, userWithId(USER_ID));
  }

  private TimbuctooActions createInstance(boolean allowedToWrite) throws PermissionFetchingException {
    PermissionFetcher permissionFetcher = mock(PermissionFetcher.class);
    if (allowedToWrite) {
      given(permissionFetcher.getOldPermissions(any(),any())).willReturn(
        Sets.newHashSet(Permission.WRITE, Permission.READ));
    } else {
      given(permissionFetcher.getOldPermissions(any(), any())).willReturn(
        Sets.newHashSet(Permission.READ));
    }
    return new TimbuctooActions(permissionFetcher, clock,
      null, (coll, id, rev) -> URI.create("http://example.org/persistent"),
      dataStoreOperations, null);
  }

}
