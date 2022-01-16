package nl.knaw.huygens.timbuctoo.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.BasicDataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportInfo;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorization;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationCreationException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.AuthorizationUnavailableException;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.security.dto.UserStubs.userWithId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class BasicPermissionFetcherTest {

  private VreAuthorizationCrud vreAuthorizationCrud;
  private PermissionFetcher permissionFetcher;
  private UserValidator userValidator;
  private User testUser;
  private DataSetMetaData publishedDataSetMetaData;
  private PermissionConfiguration permissionConfig;

  @Before
  public void setUp() throws Exception {
    vreAuthorizationCrud = mock(VreAuthorizationCrud.class);
    userValidator = mock(UserValidator.class);
    testUser = mock(User.class);
    given(testUser.getId()).willReturn("testownerid");
    given(userValidator.getUserFromUserId("testownerid")).willReturn(Optional.of(testUser));
    permissionConfig = mock(PermissionConfiguration.class);
    permissionFetcher = new BasicPermissionFetcher(vreAuthorizationCrud, permissionConfig);
    publishedDataSetMetaData = mock(BasicDataSetMetaData.class);
    given(publishedDataSetMetaData.getDataSetId()).willReturn("testdatasetid");
    given(publishedDataSetMetaData.getOwnerId()).willReturn("testownerid");
    given(publishedDataSetMetaData.isPublished()).willReturn(true);
  }

  @Test
  public void getPermissionsReturnsPermissionsForGivenUserAndDataSet() throws Exception {
    VreAuthorization vreAuthorization = mock(VreAuthorization.class);
    List<String> userRoles = Lists.newArrayList("USER");
    given(vreAuthorization.getRoles()).willReturn(userRoles);
    given(permissionConfig.getPermissionsForRoles(userRoles)).willReturn(Sets.newHashSet(
      Permission.WRITE,
      Permission.READ
    ));
    given(vreAuthorizationCrud.getAuthorization(
      anyString(),
      any(User.class))
    ).willReturn(Optional.of(vreAuthorization));

    Set<Permission> permissions = permissionFetcher.getPermissions(mock(User.class), publishedDataSetMetaData);

    assertThat(permissions, containsInAnyOrder(Permission.WRITE, Permission.READ));
  }

  @Test
  public void getPermissionsDoesNotReturnWritePermissionsForRsImportedDataSet() throws Exception {
    VreAuthorization vreAuthorization = mock(VreAuthorization.class);
    List<String> userRoles = Lists.newArrayList("USER");
    given(vreAuthorization.getRoles()).willReturn(userRoles);
    given(permissionConfig.getPermissionsForRoles(userRoles)).willReturn(Sets.newHashSet(
      Permission.WRITE,
      Permission.READ
    ));
    given(vreAuthorizationCrud.getAuthorization(
      anyString(),
      any(User.class))
    ).willReturn(Optional.of(vreAuthorization));
    ImportInfo importInfo = new ImportInfo("http://example.com/resourcesync/user/dataset/capabilitylist.xml",null);
    given(publishedDataSetMetaData.getImportInfo())
      .willReturn(Lists.newArrayList(importInfo));

    Set<Permission> permissions = permissionFetcher.getPermissions(mock(User.class), publishedDataSetMetaData);

    assertThat(permissions, contains(Permission.READ));
  }

  @Test
  public void getPermissionsReturnsReadPermissionOnlyUserWithoutWritePermissionInDataSet() throws Exception {
    VreAuthorization vreAuthorization = mock(VreAuthorization.class);
    given(vreAuthorization.isAllowedToWrite()).willReturn(false);
    given(vreAuthorizationCrud.getAuthorization(anyString(), any(User.class)))
      .willReturn(Optional.of(vreAuthorization));

    Set<Permission> permissions = permissionFetcher.getPermissions(mock(User.class), publishedDataSetMetaData);

    assertThat(permissions, contains(Permission.READ));
  }

  @Test
  public void getPermissionsReturnsReadPermissionWhenAuthorizationUnavailableExceptionTriggered() throws Exception {
    given(vreAuthorizationCrud.getAuthorization(anyString(), any(User.class))).willThrow(
      AuthorizationUnavailableException.class
    );

    Set<Permission> permissions = permissionFetcher.getPermissions(mock(User.class), publishedDataSetMetaData);

    assertThat(permissions, contains(Permission.READ));
  }

  @Test
  public void getPermissionsReturnsReadPermissionWhenUserNotPresent() throws Exception {
    given(vreAuthorizationCrud.getAuthorization(anyString(), any(User.class))).willReturn(Optional.empty());

    Set<Permission> permissions = permissionFetcher.getPermissions(mock(User.class), publishedDataSetMetaData);

    assertThat(permissions, contains(Permission.READ));
  }

  @Test
  public void getPermissionsDoesNotReturnReadPermissionForUnauthorizedUserInPrivateDataset() throws Exception {
    DataSetMetaData privateDataSetMetaData = mock(BasicDataSetMetaData.class);
    given(privateDataSetMetaData.getDataSetId()).willReturn("testdatasetid");
    given(privateDataSetMetaData.getOwnerId()).willReturn("testownerid");
    given(privateDataSetMetaData.isPublished()).willReturn(false);

    Set<Permission> permissions = permissionFetcher.getPermissions(mock(User.class), privateDataSetMetaData);

    assertThat(permissions, is(empty()));
  }

  @Test
  public void getPermissionsReturnsPermissionsForUserWithWriteAccessInPrivateDataset() throws Exception {
    VreAuthorization vreAuthorization = mock(VreAuthorization.class);
    List<String> userRoles = Lists.newArrayList("USER");
    given(vreAuthorization.getRoles()).willReturn(userRoles);
    given(permissionConfig.getPermissionsForRoles(userRoles)).willReturn(Sets.newHashSet(
      Permission.WRITE,
      Permission.READ
    ));
    given(vreAuthorizationCrud.getAuthorization(anyString(), any(User.class)))
      .willReturn(Optional.of(vreAuthorization));

    DataSetMetaData dataSetMetaData2 = mock(BasicDataSetMetaData.class);
    given(dataSetMetaData2.getDataSetId()).willReturn("testdatasetid");
    given(dataSetMetaData2.getOwnerId()).willReturn("testownerid");
    given(dataSetMetaData2.isPublished()).willReturn(false);

    Set<Permission> permissions = permissionFetcher.getPermissions(userWithId("testWriterId"), dataSetMetaData2);

    assertThat(permissions, containsInAnyOrder(Permission.READ, Permission.WRITE));
  }

  @Test
  public void initializeOwnerAuthorizationCreatesAdminAuthorization() throws Exception {
    User user = userWithId("testuserid");
    permissionFetcher.initializeOwnerAuthorization(user,"testownerid", "testdatasetid");

    verify(vreAuthorizationCrud).createAuthorization("testownerid__testdatasetid", user, "ADMIN");
  }

  @Test(expected = AuthorizationCreationException.class)
  public void initializeOwnerAuthorizationThrowsExceptionWhenVreAuthorizationCrudFails() throws Exception {
    User user = userWithId("testuserid");
    doThrow(AuthorizationCreationException.class).when(vreAuthorizationCrud).createAuthorization(
      "testownerid__testdatasetid",
      user,
      "ADMIN"
    );

    permissionFetcher.initializeOwnerAuthorization(user,"testownerid", "testdatasetid");
  }

  @Test
  public void removeAuthorizationsRemovesAdminAuthorization() throws Exception {
    given(userValidator.getUserFromPersistentId("testownerid")).willReturn(Optional.of(testUser));

    permissionFetcher.removeAuthorizations("testownerid__testdatasetid");

    verify(vreAuthorizationCrud).deleteVreAuthorizations("testownerid__testdatasetid");
  }

}
