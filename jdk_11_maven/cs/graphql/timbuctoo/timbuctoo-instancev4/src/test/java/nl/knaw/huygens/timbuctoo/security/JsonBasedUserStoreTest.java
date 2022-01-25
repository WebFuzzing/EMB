package nl.knaw.huygens.timbuctoo.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.UserCreationException;
import nl.knaw.huygens.timbuctoo.util.FileHelpers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.security.JsonBasedUserStoreStubs.forFile;
import static nl.knaw.huygens.hamcrest.OptionalPresentMatcher.present;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JsonBasedUserStoreTest {

  public Path usersFile;
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private JsonBasedUserStore instance;

  @Before
  public void setUp() throws Exception {
    usersFile = FileHelpers.makeTempFilePath(false);
    User user = User.create("", "pidOfKnownUser");
    User userWithoutPid = User.create(null, null);
    User[] users = {user, userWithoutPid};
    OBJECT_MAPPER.writeValue(usersFile.toFile(), users);

    instance = forFile(usersFile);
  }

  @After
  public void tearDown() throws Exception {
    Files.delete(usersFile);
  }

  @Test
  public void userForReturnsAnEmptyOptionalForAnUnknownUser() throws AuthenticationUnavailableException {
    Optional<User> unknownUser = instance.userFor("pidOfUnknownUser");

    assertThat(unknownUser, is(not(present())));
  }

  @Test
  public void userForReturnsAnOptionalWithAUserForAKnownUser() throws AuthenticationUnavailableException {
    Optional<User> knownUser = instance.userFor("pidOfKnownUser");

    assertThat(knownUser, is(present()));
  }

  @Test
  public void ifTheDatabaseContainsOneUserWithNullAsPidThatUserIsReturnedWhenQueryingForNull()
    throws AuthenticationUnavailableException {
    Optional<User> knownUser = instance.userFor(null);

    assertThat(knownUser, is(present()));
  }

  @Test
  public void userForThrowsAnAuthenticationUnavailableExceptionWhenTheUsersFileCannotBeRead()
    throws AuthenticationUnavailableException {
    JsonBasedUserStore instance = forFile(Paths.get("nonExistingUserFile"));

    expectedException.expect(AuthenticationUnavailableException.class);

    instance.userFor("pid");
  }

  @Test
  public void createUserAddsAUserToTheUsersFile() throws Exception {
    User createdUser = instance.createUser("pidOfOtherUser", "email", "givenName", "surname", "organization");

    Optional<User> user = instance.userForId(createdUser.getId());
    assertThat(user, is(present()));
    assertThat(user.get().getPersistentId(), is("pidOfOtherUser"));
  }

  @Test
  public void createUserLetsTheUserBeCreatedOnlyOnce() throws Exception {
    User user1 = instance.createUser("pid", "email", "givenName", "surname", "organization");
    User user2 = instance.createUser("pid", "email", "givenName", "surname", "organization");

    assertThat(user1.getId(), is(user2.getId()));

  }

  @Test
  public void createUserThrowsAUserCreationExceptionWhenTheUsersFileCannotBeRead() throws Exception {
    Path nonExistingUsersFile = Paths.get("src", "test", "resources", "users1.json");
    JsonBasedUserStore instance = forFile(nonExistingUsersFile);

    expectedException.expect(UserCreationException.class);
    instance.createUser("pid", "email", "givenName", "surname", "organization");

  }

}
