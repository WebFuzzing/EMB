package nl.knaw.huygens.timbuctoo.security.dataaccess.azure;

import com.google.common.collect.Sets;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import nl.knaw.huygens.timbuctoo.security.PermissionConfiguration;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public class AzurePermissionConfiguration extends AzureAccess implements PermissionConfiguration {

  private static final Logger LOG = LoggerFactory.getLogger(AzurePermissionConfiguration.class);
  private static final String TABLE = "permissions";

  public AzurePermissionConfiguration(CloudTableClient client)
    throws AzureAccessNotPossibleException {
    super(client, TABLE);
  }

  @Override
  public Set<Permission> getPermissionsForRoles(Collection<String> roles) {
    Set<Permission> permissions = Sets.newHashSet();

    try {
      for (String role : roles) {
        Optional<DynamicTableEntity> rolePermissions = retrieve(TABLE, role);
        if (rolePermissions.isPresent()) {
          String[] permissionStrings = getStringArrayOrEmpty(rolePermissions.get(), "permissions");
          for (String permissionString : permissionStrings) {
            permissions.add(Permission.valueOf(permissionString));
          }

        }
      }
    } catch (StorageException e) {
      LOG.error("Permission could not be retrieved", e);
    }

    return permissions;
  }
}
