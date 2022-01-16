package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.dto.VreAuthorization;
import org.junit.Test;

import static nl.knaw.huygens.timbuctoo.security.dto.UserRoles.ADMIN_ROLE;
import static nl.knaw.huygens.timbuctoo.security.dto.UserRoles.UNVERIFIED_USER_ROLE;
import static nl.knaw.huygens.timbuctoo.security.dto.UserRoles.USER_ROLE;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class VreAuthorizationTest {
  @Test
  public void isAllowedToWriteReturnsTrueIfTheRolesContainUser() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", USER_ROLE);

    boolean allowedToWrite = instance.isAllowedToWrite();

    assertThat(allowedToWrite, is(true));
  }

  @Test
  public void isAllowedToWriteReturnsTrueIfTheRolesContainAdmin() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", ADMIN_ROLE);

    boolean allowedToWrite = instance.isAllowedToWrite();

    assertThat(allowedToWrite, is(true));
  }

  @Test
  public void isAllowedToWriteReturnsFalseIfTheRolesOnlyContainUnverifiedUser() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", UNVERIFIED_USER_ROLE);

    boolean allowedToWrite = instance.isAllowedToWrite();

    assertThat(allowedToWrite, is(false));
  }

  @Test
  public void isAllowedToWriteReturnsTrueIfTheRolesContainUnverifiedUserAndUser() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", UNVERIFIED_USER_ROLE, USER_ROLE);

    boolean allowedToWrite = instance.isAllowedToWrite();

    assertThat(allowedToWrite, is(true));
  }

  @Test
  public void isAllowedToWriteReturnsTrueIfTheRolesContainUnverifiedUserAndAdmin() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", UNVERIFIED_USER_ROLE, ADMIN_ROLE);

    boolean allowedToWrite = instance.isAllowedToWrite();

    assertThat(allowedToWrite, is(true));
  }

  @Test
  public void isAllowedToWriteReturnsFalseIfTheRolesAreEmpty() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "");

    boolean allowedToWrite = instance.isAllowedToWrite();

    assertThat(allowedToWrite, is(false));
  }

  // admin access
  @Test
  public void hasAdminAccessReturnsTrueIfTheRolesContainAdmin() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", ADMIN_ROLE);

    boolean allowedToWrite = instance.hasAdminAccess();

    assertThat(allowedToWrite, is(true));
  }

  @Test
  public void hasAdminAccessReturnsFalseIfTheRolesOnlyContainUnverifiedUser() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", UNVERIFIED_USER_ROLE);

    boolean allowedToWrite = instance.hasAdminAccess();

    assertThat(allowedToWrite, is(false));
  }

  @Test
  public void hasAdminAccessReturnsFalseIfTheRolesContainUnverifiedUserAndAdmin() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", UNVERIFIED_USER_ROLE, USER_ROLE);

    boolean allowedToWrite = instance.hasAdminAccess();

    assertThat(allowedToWrite, is(false));
  }

  @Test
  public void hasAdminAccessReturnsTrueIfTheRolesContainUnverifiedUserAndAdmin() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "", UNVERIFIED_USER_ROLE, ADMIN_ROLE);

    boolean allowedToWrite = instance.hasAdminAccess();

    assertThat(allowedToWrite, is(true));
  }

  @Test
  public void hasAdminAccessReturnsFalseIfTheRolesAreEmpty() throws Exception {
    VreAuthorization instance = VreAuthorization.create("", "");

    boolean allowedToWrite = instance.hasAdminAccess();

    assertThat(allowedToWrite, is(false));
  }

}
