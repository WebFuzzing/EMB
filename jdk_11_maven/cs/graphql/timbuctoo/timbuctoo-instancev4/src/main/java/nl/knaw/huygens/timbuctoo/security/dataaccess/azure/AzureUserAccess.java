package nl.knaw.huygens.timbuctoo.security.dataaccess.azure;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableQuery;
import nl.knaw.huygens.timbuctoo.security.dataaccess.UserAccess;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class AzureUserAccess extends AzureAccess implements UserAccess {

  private static final Logger LOG = getLogger(AzureUserAccess.class);

  public AzureUserAccess(CloudTableClient client) throws AzureAccessNotPossibleException {
    super(client, "users");
  }

  public User propsToObject(DynamicTableEntity entity) {
    return User.create(
      getStringOrNull(entity, "displayName"),
      getStringOrNull(entity, "persistentId"),
      getStringOrNull(entity, "id")
    );
  }

  public Map<String, EntityProperty> objectToProps(User source) {
    Map<String, EntityProperty> result = new HashMap<>();
    if (source.getPersistentId() != null) {
      result.put("persistentId", new EntityProperty(source.getPersistentId()));
    }
    if (source.getDisplayName() != null) {
      result.put("displayName", new EntityProperty(source.getDisplayName()));
    }
    result.put("id", new EntityProperty(source.getId()));
    return result;
  }

  @Override
  public void addUser(User user) throws AuthenticationUnavailableException {
    String rowKey = user.getPersistentId() == null ? "null" : user.getPersistentId();
    try {
      create("users", rowKey, objectToProps(user));
    } catch (StorageException e) {
      LOG.error("addUser failed", e);
      throw new AuthenticationUnavailableException("Could not add user");
    }
  }

  @Override
  public Optional<User> getUserForPid(String pid) throws AuthenticationUnavailableException {
    try {
      return retrieve("users", pid == null ? "null" : pid).map(this::propsToObject);
    } catch (StorageException e) {
      LOG.error("getUserForPid failed", e);
      throw new AuthenticationUnavailableException("Could not get user");
    }
  }

  @Override
  public Optional<User> getUserForTimLocalId(String userId) throws AuthenticationUnavailableException {
    String query = "(PartitionKey eq 'users') and (id eq '" + userId + "')";
    Iterator<DynamicTableEntity> users = table.execute(TableQuery.from(DynamicTableEntity.class)
      .where(query)
      .take(2)).iterator();
    if (users.hasNext()) {
      Optional<User> result = Optional.of(propsToObject(users.next()));
      if (users.hasNext()) {
        LOG.error("Multiple items found for query " + query);
      }
      return result;
    } else {
      return Optional.empty();
    }
  }
}
