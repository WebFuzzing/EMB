package nl.knaw.huygens.timbuctoo.v5.graphql.security;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class UserPermissionCheck {
  private static final Logger LOG = LoggerFactory.getLogger(UserPermissionCheck.class);
  private final Optional<User> user;
  private final PermissionFetcher permissionFetcher;
  private final HashMap<String, Set<Permission>> dataSetPermissionMap = new HashMap<>();

  public UserPermissionCheck(Optional<User> user, PermissionFetcher permissionFetcher) {
    this.user = user;
    this.permissionFetcher = permissionFetcher;
  }

  public Set<Permission> getPermissions(DataSetMetaData dataSetMetaData) {

    Set<Permission> permissions = user.map(user -> {
      try {
        String dataSetId = dataSetMetaData.getCombinedId();
        if (dataSetPermissionMap.containsKey(dataSetId)) {
          return dataSetPermissionMap.get(dataSetId);
        }
        // put returns the previous value
        dataSetPermissionMap.put(dataSetId, permissionFetcher.getPermissions(user, dataSetMetaData));
        return dataSetPermissionMap.get(dataSetId);
      } catch (PermissionFetchingException e) {
        LOG.error("Could not fetch permissions for user '{}' on data set '{}'",
          user.getDisplayName(),
          dataSetMetaData.getDataSetId()
        );
        LOG.error("Error thrown", e);
        return Sets.<Permission>newHashSet();
      }
    }).orElse(Sets.newHashSet());

    if (permissions.isEmpty() && dataSetMetaData.isPublished()) {
      permissions.add(Permission.READ);
    }

    return permissions;
  }

  public boolean hasPermission(DataSetMetaData dataSet, Permission permission) {
    // do not use permissionFetcher.hasPermission,  getPermissions will cache the permissions of the users for the
    // http request and therefore makes the loading of the schema a lot faster
    return getPermissions(dataSet).contains(permission);
  }

  public boolean hasOldGraphQlPermission(DataSetMetaData dataSet, OldGraphQlPermission permission) {
    return permission.translateToPermission().stream().allMatch(perm -> hasPermission(dataSet, perm));
  }

  // This mapping has a semantic connection with the
  // nl.knaw.huygens.timbuctoo.security.dataaccess.localfile.PermissionConfigMigrator
  public enum OldGraphQlPermission {
    ADMIN,
    READ,
    WRITE;

    public Collection<Permission> translateToPermission() {
      switch (this) {
        case ADMIN:
          return Lists.newArrayList(
            Permission.IMPORT_DATA,
            Permission.REMOVE_DATASET,
            Permission.PUBLISH_DATASET,
            Permission.EDIT_COLLECTION_METADATA,
            Permission.EDIT_DATASET_METADATA,
            Permission.EXTEND_SCHEMA,
            Permission.CONFIG_INDEX,
            Permission.CONFIG_VIEW,
            Permission.CHANGE_SUMMARYPROPS,
            Permission.READ,
            Permission.WRITE,
            Permission.UPDATE_RESOURCESYNC,
            Permission.IMPORT_RESOURCESYNC,
            Permission.READ_IMPORT_STATUS
          );
        case READ:
          return Lists.newArrayList(Permission.READ);
        case WRITE:
          return Lists.newArrayList(Permission.READ, Permission.WRITE, Permission.READ_IMPORT_STATUS);
        default:
          throw new RuntimeException("This cannot happen.");
      }
    }
  }
}

