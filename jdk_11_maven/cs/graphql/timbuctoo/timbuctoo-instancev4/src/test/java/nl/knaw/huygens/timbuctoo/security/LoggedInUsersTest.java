package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.security.client.MockAuthenticationHandler;
import nl.knaw.huygens.security.client.UnauthorizedException;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;
import nl.knaw.huygens.timbuctoo.util.Timeout;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Optional;

import static java.util.concurrent.TimeUnit.SECONDS;
import static nl.knaw.huygens.hamcrest.OptionalPresentMatcher.present;
import static nl.knaw.huygens.timbuctoo.security.UserStoreMockBuilder.userStore;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class LoggedInUsersTest {

  public static final Timeout ONE_SECOND_TIMEOUT = new Timeout(1, SECONDS);
  @Rule
  public ExpectedException expectedException = ExpectedException.none();
  private LoggedInUsers userStoreWithUserA;
  private LoggedInUsers userStoreWithUserAAndB;

  @Before
  public void setUp() throws Exception {
    Authenticator authenticator = AuthenticatorMockBuilder.authenticator().withPidFor("a", "b", "pid").build();
    UserStore userStore = userStore().withUserFor("pid").build();

    MockAuthenticationHandler authHandler = new MockAuthenticationHandler();

    userStoreWithUserA = new LoggedInUsers(
      authenticator,
      userStore,
      ONE_SECOND_TIMEOUT,
      x -> {
        throw new UnauthorizedException();
      }
    );

    Authenticator authenticator1 = AuthenticatorMockBuilder.authenticator()
      .withPidFor("a", "b", "pid")
      .withPidFor("c", "d", "otherPid")
      .build();
    UserStore userStore1 = userStore().withUserFor("pid").withUserFor("otherPid").build();

    userStoreWithUserAAndB = new LoggedInUsers(
      authenticator1,
      userStore1,
      ONE_SECOND_TIMEOUT,
      x -> {
        throw new UnauthorizedException();
      }
    );

  }

  @Test
  public void canStoreAUserAndReturnsAToken() throws Exception {
    LoggedInUsers instance = userStoreWithUserA;

    Optional<String> token = instance.userTokenFor("a", "b");

    assertThat(token, is(present()));
    assertThat(token.get(), not(isEmptyString()));
  }

  @Test
  public void canRetrieveAStoredUser() throws Exception {
    LoggedInUsers instance = userStoreWithUserA;
    String token = instance.userTokenFor("a", "b").get();

    Optional<User> user = instance.userFor(token);

    assertThat(user, is(present()));
  }

  //FIXME: same token same user?

  @Test
  public void willReturnAUniqueTokenForEachUser()
    throws Exception {
    LoggedInUsers instance = userStoreWithUserAAndB;

    String tokenA = instance.userTokenFor("a", "b").get();
    String tokenB = instance.userTokenFor("c", "d").get();

    assertThat(tokenA, is(not(tokenB)));
  }

  @Test
  public void willReturnTheSameUserForATokenEachTime()
    throws Exception {
    LoggedInUsers instance = userStoreWithUserA;
    String token = instance.userTokenFor("a", "b").get();

    User user = instance.userFor(token).get();
    User sameUser = instance.userFor(token).get();

    assertThat(user, is(sameUser));
  }

  @Test
  public void willReturnTheUserBelongingToTheToken()
    throws Exception {
    LoggedInUsers instance = userStoreWithUserAAndB;
    String tokenA = instance.userTokenFor("a", "b").get();
    String tokenB = instance.userTokenFor("c", "d").get();

    User userA = instance.userFor(tokenA).get();
    User userB = instance.userFor(tokenB).get();

    assertThat(userA, is(not(userB)));
  }

  @Test
  public void returnsAnEmptyOptionalForABogusToken() {
    LoggedInUsers instance = userStoreWithUserA;
    String bogusToken = "bogus";

    Optional<User> user = instance.userFor(bogusToken);

    assertThat(user, is(not(present())));
  }

  @Test
  public void returnsNoTokenIfTheUserIsUnknown()
    throws Exception {
    LoggedInUsers instance = userStoreWithUserA;

    Optional<String> token = instance.userTokenFor("unknownUser", "");

    assertThat(token, is(not(present())));
  }

  @Test
  public void returnsAnEmptyOptionalIfTheUserIsRetrievedAfterATimeout() throws Exception {
    LoggedInUsers instance = this.userStoreWithUserA;
    String token = instance.userTokenFor("a", "b").get();

    Thread.sleep(ONE_SECOND_TIMEOUT.toMilliseconds() + 100);

    Optional<User> user = instance.userFor(token);

    assertThat(user, is(not(present())));
  }

  @Test
  public void willGenerateADifferentTokenEachSession() throws Exception {
    LoggedInUsers instance = this.userStoreWithUserA;
    String token = instance.userTokenFor("a", "b").get();

    Thread.sleep(ONE_SECOND_TIMEOUT.toMilliseconds() + 1);

    String otherToken = instance.userTokenFor("a", "b").get();

    assertThat(token, is(not(otherToken)));
  }

  @Test
  public void throwsLocalLoginUnavailableExceptionWhenTheUserCouldNotBeAuthenticatedLocallyDueToASystemError()
    throws Exception {
    Authenticator authenticator = mock(JsonBasedAuthenticator.class);
    given(authenticator.authenticate(anyString(), anyString())).willThrow(new LocalLoginUnavailableException(""));
    LoggedInUsers instance = new LoggedInUsers(authenticator, null, ONE_SECOND_TIMEOUT, null);


    expectedException.expect(LocalLoginUnavailableException.class);

    instance.userTokenFor("", "");
  }

  @Test
  public void throwsAnAuthenticationUnavailableExceptionWhenTheUserCouldNotBeRetrievedDueToASystemError()
    throws Exception {
    UserStore userStore = mock(JsonBasedUserStore.class);
    given(userStore.userFor(anyString())).willThrow(new AuthenticationUnavailableException(""));
    Authenticator authenticator = AuthenticatorMockBuilder.authenticator().withPidFor("a", "b", "pid").build();
    LoggedInUsers instance = new LoggedInUsers(authenticator, userStore, ONE_SECOND_TIMEOUT, null);


    expectedException.expect(AuthenticationUnavailableException.class);

    instance.userTokenFor("a", "b");

  }

}
