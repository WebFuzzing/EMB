package nl.knaw.huygens.timbuctoo.server.tasks;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.server.security.LocalUserCreator;
import nl.knaw.huygens.timbuctoo.security.exceptions.UserCreationException;
import nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class UserCreationTaskTest {
  public static final String UNKNOWN_KEY_1 = "unknownKey1";
  public static final String UNKNOWN_KEY_2 = "unknownKey2";
  private static final String PID = "pid";
  private static final String USER_NAME = "user_name";
  private static final String PWD = "pwd";
  private static final String GIVEN_NAME = "Given";
  private static final String SURNAME = "Sur";
  private static final String EMAIL = "test@example.com";
  private static final String ORGANIZATION = "Org";
  private static final String VRE_ID = "VRE";
  private static final String VRE_ROLE = "role";
  private LocalUserCreator localUserCreator;
  private PrintWriter printWriter;
  private UserCreationTask instance;

  @Before
  public void setUp() throws Exception {
    localUserCreator = mock(LocalUserCreator.class);
    printWriter = mock(PrintWriter.class);
    instance = new UserCreationTask(localUserCreator);
  }

  @Test
  public void executeLetsTheLocalUserCreatorCreateAUser() throws Exception {
    Map<String, String> userInfo = Maps.newHashMap();
    userInfo.put(UserInfoKeys.USER_PID, PID);
    userInfo.put(UserInfoKeys.USER_NAME, USER_NAME);
    userInfo.put(UserInfoKeys.PASSWORD, PWD);
    userInfo.put(UserInfoKeys.GIVEN_NAME, GIVEN_NAME);
    userInfo.put(UserInfoKeys.SURNAME, SURNAME);
    userInfo.put(UserInfoKeys.EMAIL_ADDRESS, EMAIL);
    userInfo.put(UserInfoKeys.ORGANIZATION, ORGANIZATION);
    userInfo.put(UserInfoKeys.VRE_ID, VRE_ID);
    userInfo.put(UserInfoKeys.VRE_ROLE, VRE_ROLE);

    Map<String, List<String>> userInfoCopy = userInfo.entrySet().stream().collect(
        Collectors.toMap(Map.Entry::getKey, entry -> List.of(entry.getValue())));

    instance.execute(userInfoCopy, mock(PrintWriter.class));

    verify(localUserCreator).create(userInfo);
  }

  @Test
  public void executeFiltersAllTheUnknownKeys() throws Exception {
    Map<String, List<String>> userInfo = Maps.newHashMap();
    userInfo.put(UserInfoKeys.USER_PID, List.of(PID));
    userInfo.put(UserInfoKeys.USER_NAME, List.of(USER_NAME));
    userInfo.put(UserInfoKeys.PASSWORD, List.of(PWD));
    userInfo.put(UserInfoKeys.GIVEN_NAME, List.of(GIVEN_NAME));
    userInfo.put(UserInfoKeys.SURNAME, List.of(SURNAME));
    userInfo.put(UserInfoKeys.EMAIL_ADDRESS, List.of(EMAIL));
    userInfo.put(UserInfoKeys.ORGANIZATION, List.of(ORGANIZATION));
    userInfo.put(UserInfoKeys.VRE_ID, List.of(VRE_ID));
    userInfo.put(UserInfoKeys.VRE_ROLE, List.of(VRE_ROLE));
    userInfo.put(UNKNOWN_KEY_1, List.of("val"));
    userInfo.put(UNKNOWN_KEY_2, List.of("val2"));

    instance.execute(userInfo, mock(PrintWriter.class));

    // check the set does not has the unknown keys
    verify(localUserCreator).create(argThat(not(allOf(hasKey(UNKNOWN_KEY_1), hasKey(UNKNOWN_KEY_2)))));
  }

  @Test
  public void executeDoesNotImportWithDuplicateKeys() throws Exception {
    Map<String, List<String>> userInfo = Maps.newHashMap();
    userInfo.put(UserInfoKeys.USER_PID, List.of(PID, "otherPid"));
    userInfo.put(UserInfoKeys.USER_NAME, List.of(USER_NAME));
    userInfo.put(UserInfoKeys.PASSWORD, List.of(PWD));
    userInfo.put(UserInfoKeys.GIVEN_NAME, List.of(GIVEN_NAME));
    userInfo.put(UserInfoKeys.SURNAME, List.of(SURNAME));
    userInfo.put(UserInfoKeys.EMAIL_ADDRESS, List.of(EMAIL));
    userInfo.put(UserInfoKeys.ORGANIZATION, List.of(ORGANIZATION));
    userInfo.put(UserInfoKeys.VRE_ID, List.of(VRE_ID));
    userInfo.put(UserInfoKeys.VRE_ROLE, List.of(VRE_ROLE));

    instance.execute(userInfo, printWriter);

    verifyNoInteractions(localUserCreator);
    verify(printWriter).write(argThat(containsString(UserInfoKeys.USER_PID)));
  }

  @Test
  public void executeDoesNotImportIfKeysAreMissing() throws Exception {
    // Missing USER_PID key
    Map<String, List<String>> userInfo = Maps.newHashMap();
    userInfo.put(UserInfoKeys.USER_NAME, List.of(USER_NAME));
    userInfo.put(UserInfoKeys.PASSWORD, List.of(PWD));
    userInfo.put(UserInfoKeys.GIVEN_NAME, List.of(GIVEN_NAME));
    userInfo.put(UserInfoKeys.SURNAME, List.of(SURNAME));
    userInfo.put(UserInfoKeys.EMAIL_ADDRESS, List.of(EMAIL));
    userInfo.put(UserInfoKeys.ORGANIZATION, List.of(ORGANIZATION));
    userInfo.put(UserInfoKeys.VRE_ID, List.of(VRE_ID));
    userInfo.put(UserInfoKeys.VRE_ROLE, List.of(VRE_ROLE));

    instance.execute(userInfo, printWriter);

    verifyNoInteractions(localUserCreator);
    verify(printWriter).write(argThat(containsString(UserInfoKeys.USER_PID)));
  }

  @Test(expected = UserCreationException.class)
  public void executeRethrowsTheExceptionsOfTheLocalUserCreator() throws Exception {
    doThrow(new UserCreationException("")).when(localUserCreator).create(ArgumentMatchers.any());
    Map<String, List<String>> userInfo = Maps.newHashMap();
    userInfo.put(UserInfoKeys.USER_PID, List.of(PID));
    userInfo.put(UserInfoKeys.USER_NAME, List.of(USER_NAME));
    userInfo.put(UserInfoKeys.PASSWORD, List.of(PWD));
    userInfo.put(UserInfoKeys.GIVEN_NAME, List.of(GIVEN_NAME));
    userInfo.put(UserInfoKeys.SURNAME, List.of(SURNAME));
    userInfo.put(UserInfoKeys.EMAIL_ADDRESS, List.of(EMAIL));
    userInfo.put(UserInfoKeys.ORGANIZATION, List.of(ORGANIZATION));
    userInfo.put(UserInfoKeys.VRE_ID, List.of(VRE_ID));
    userInfo.put(UserInfoKeys.VRE_ROLE, List.of(VRE_ROLE));

    instance.execute(userInfo, mock(PrintWriter.class));
  }

}
