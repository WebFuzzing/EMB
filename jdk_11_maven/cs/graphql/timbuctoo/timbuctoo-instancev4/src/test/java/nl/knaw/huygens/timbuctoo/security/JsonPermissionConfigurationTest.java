package nl.knaw.huygens.timbuctoo.security;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class JsonPermissionConfigurationTest {

  @Test
  public void returnsThePermissionsOfTheRoles() throws Exception {
    String rolePermissions = "[" +
      "{\n" +
      "\t\"roleName\": \"USER\",\n" +
      "\t\"permissions\": [\"REMOVE_DATASET\"]\n" +
      "},\n" +
      "{\n" +
      "\t\"roleName\": \"ADMIN\",\n" +
      "\t\"permissions\": [\"IMPORT_DATA\"]\n" +
      "},\n" +
      "{\n" +
      "\t\"roleName\": \"OTHER_ROLE\",\n" +
      "\t\"permissions\": [\"EDIT_COLLECTION_METADATA\"]\n" +
      "}" +
      "]";
    ByteArrayInputStream configuration = new ByteArrayInputStream(rolePermissions.getBytes());
    JsonPermissionConfiguration instance = new JsonPermissionConfiguration(configuration);

    Set<Permission> permissions = instance.getPermissionsForRoles(Lists.newArrayList("USER", "ADMIN"));

    assertThat(permissions, containsInAnyOrder(Permission.REMOVE_DATASET, Permission.IMPORT_DATA));
  }

  @Test
  public void returnsNothingWhenNoRolesAreConfigured() throws Exception {
    ByteArrayInputStream configuration = new ByteArrayInputStream("[]".getBytes());
    JsonPermissionConfiguration instance = new JsonPermissionConfiguration(configuration);

    Set<Permission> permissions = instance.getPermissionsForRoles(Lists.newArrayList("USER"));

    assertThat(permissions, is(empty()));
  }
}
