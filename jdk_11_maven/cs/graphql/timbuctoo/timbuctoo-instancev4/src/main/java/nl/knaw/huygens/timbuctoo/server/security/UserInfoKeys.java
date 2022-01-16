package nl.knaw.huygens.timbuctoo.server.security;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class UserInfoKeys {
  public static final String USER_PID = "userPid";
  public static final String USER_NAME = "userName";
  public static final String PASSWORD = "password";
  public static final String GIVEN_NAME = "givenName";
  public static final String SURNAME = "surname";
  public static final String EMAIL_ADDRESS = "emailAddress";
  public static final String ORGANIZATION = "organization";
  public static final String VRE_ID = "vreId";
  public static final String VRE_ROLE = "vreRole";
  public static final Set<String> all =
    newHashSet(USER_PID, USER_NAME, PASSWORD, GIVEN_NAME, SURNAME, EMAIL_ADDRESS, ORGANIZATION, VRE_ID, VRE_ROLE);

  private UserInfoKeys() {
    throw new RuntimeException("Class should not be instantiated.");
  }

  public static boolean contains(String key) {
    return all.contains(key);
  }
}
