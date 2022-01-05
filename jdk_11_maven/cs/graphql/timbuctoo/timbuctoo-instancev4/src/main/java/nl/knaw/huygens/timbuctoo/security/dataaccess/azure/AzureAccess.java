package nl.knaw.huygens.timbuctoo.security.dataaccess.azure;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import com.microsoft.azure.storage.table.TableOperation;
import org.slf4j.Logger;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class AzureAccess {
  private static final Logger LOG = getLogger(AzureAccess.class);
  protected final CloudTable table;

  public AzureAccess(CloudTableClient client, String tableName) throws AzureAccessNotPossibleException {
    try {
      table = client.getTableReference(tableName);
      table.createIfNotExists();
    } catch (URISyntaxException | StorageException e) {
      LOG.error("Azure communication failed", e);
      throw new AzureAccessNotPossibleException("Azure communication failed");
    }
  }

  protected Optional<DynamicTableEntity> retrieve(String partitionKey, String rowKey) throws StorageException {
    DynamicTableEntity data = table
      .execute(TableOperation.retrieve(partitionKey, rowKey, DynamicTableEntity.class))
      .getResultAsType();
    return Optional.ofNullable(data);
  }

  protected void create(String partitionKey, String rowKey, Map<String, EntityProperty> properties)
    throws StorageException {
    table.execute(TableOperation.insert(new DynamicTableEntity(partitionKey, rowKey, new HashMap<>(properties))));
  }

  protected void delete(String partitionKey, String rowKey) throws StorageException {
    table.execute(TableOperation.delete(new DynamicTableEntity(partitionKey, rowKey)));
  }

  protected static String[] getStringArrayOrEmpty(DynamicTableEntity entity, String key) {
    String content = getStringOrNull(entity, key);
    if (content == null) {
      return new String[0];
    } else {
      return content.split(",");
    }
  }

  protected static byte[] getByteArrayOrEmpty(DynamicTableEntity entity, String key) {
    EntityProperty property = entity.getProperties().get(key);
    byte[] result = new byte[0];
    if (property != null) {
      result = property.getValueAsByteArray();
    }
    return result;
  }

  protected static String getStringOrNull(DynamicTableEntity entity, String key) {
    EntityProperty property = entity.getProperties().get(key);
    String result = null;
    if (property != null) {
      result = property.getValueAsString();
    }
    return result;
  }
}
