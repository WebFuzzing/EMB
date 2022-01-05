package nl.knaw.huygens.timbuctoo.security.dataaccess.azure;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTableClient;
import nl.knaw.huygens.timbuctoo.security.dto.Login;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorization;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Optional;

import static nl.knaw.huygens.hamcrest.OptionalPresentMatcher.present;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.text.IsEmptyString.isEmptyOrNullString;

@Ignore
//You need to specify a valid accountName and accountKey and you need to provide this test with empty databases
public class AzureAccessTest {

  private CloudTableClient tableClient;

  @Before
  public void setUp() throws Exception {
    tableClient = CloudStorageAccount.parse("DefaultEndpointsProtocol=http;" +
      "AccountName=;" +
      "AccountKey=")
                                     .createCloudTableClient();
  }

  @Test
  public void testLogin() throws Exception {
    AzureLoginAccess instance = new AzureLoginAccess(tableClient);
    Login login = Login.create("userName", "userPid", new byte[]{'a'}, new byte[]{'a'}, "", "", "", "");
    instance.addLogin(login);
    Optional<Login> retrievedLogin = instance.getLogin("userName");
    assertThat(retrievedLogin, is(present()));
    assertThat(retrievedLogin.get(), is(login));
  }

  @Test
  public void testUser() throws Exception {
    AzureUserAccess instance = new AzureUserAccess(tableClient);
    User user = User.create("displayName", "persistentId");
    instance.addUser(user);
    Optional<User> userForPersistentId = instance.getUserForPid("persistentId");
    Optional<User> userForTimLocalId = instance.getUserForTimLocalId(user.getId());

    assertThat(userForPersistentId.get(), is(user));
    assertThat(userForTimLocalId.get(), is(user));
    assertThat(user.getId(), not(isEmptyOrNullString()));
  }

  @Test
  public void testNullHandling() throws Exception {
    AzureUserAccess instance = new AzureUserAccess(tableClient);
    User user = User.create(null, null);
    instance.addUser(user);
    Optional<User> userForPersistentId = instance.getUserForPid(null);
    Optional<User> userForTimLocalId = instance.getUserForTimLocalId(user.getId());

    assertThat(userForPersistentId.get(), is(user));
    assertThat(userForTimLocalId.get(), is(user));
    assertThat(user.getId(), not(isEmptyOrNullString()));
  }

  @Test
  public void testVreAuthorization() throws Exception {
    AzureVreAuthorizationAccess instance = new AzureVreAuthorizationAccess(tableClient);
    Optional<VreAuthorization> beforeCreate = instance.getAuthorization("vreId", "userId");
    VreAuthorization created = instance.getOrCreateAuthorization("vreId", "userId", "role1");
    Optional<VreAuthorization> afterCreate = instance.getAuthorization("vreId", "userId");

    assertThat(beforeCreate, is(not(present())));
    assertThat(afterCreate.get(), is(created));
  }

  @Test
  public void testDeleteAllFromPartition() throws Exception {
    AzureVreAuthorizationAccess instance = new AzureVreAuthorizationAccess(tableClient);
    instance.getOrCreateAuthorization("vreId", "userId", "role1");
    instance.getOrCreateAuthorization("otherVreId", "userId", "role1");
    instance.getOrCreateAuthorization("vreId", "otherUserId", "role1");

    instance.deleteVreAuthorizations("vreId");

    assertThat(instance.getAuthorization("vreId", "userId"), is(not(present())));
    assertThat(instance.getAuthorization("vreId", "otherUserId"), is(not(present())));
    assertThat(instance.getAuthorization("otherVreId", "userId"), is(present()));
  }


}
