package nl.knaw.huygens.timbuctoo.security;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonPermissionConfiguration implements PermissionConfiguration {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private final Set<RolePermissions> rolePermissions;

  public JsonPermissionConfiguration(InputStream permissionConfig) throws IOException {
    rolePermissions = OBJECT_MAPPER.readValue(
      permissionConfig,
      new TypeReference<Set<RolePermissions>>() {
      }
    );
  }

  @Override
  public Set<Permission> getPermissionsForRoles(Collection<String> roles) {

    return rolePermissions.stream()
                          .filter(rolePerm -> roles.contains(rolePerm.roleName))
                          .flatMap(rolePerm -> rolePerm.permissions.stream())
                          .collect(Collectors.toSet());
  }

  public static class RolePermissions {
    @JsonProperty
    public String roleName;
    @JsonProperty
    public Set<Permission> permissions;

    @JsonCreator
    public RolePermissions(
      @JsonProperty("roleName") String roleName,
      @JsonProperty("permissions") Set<Permission> permissions
    ) {
      this.roleName = roleName;
      this.permissions = permissions;
    }
  }
}
