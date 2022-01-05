package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface PermissionConfiguration {
  Set<Permission> getPermissionsForRoles(Collection<String> roles);
}
