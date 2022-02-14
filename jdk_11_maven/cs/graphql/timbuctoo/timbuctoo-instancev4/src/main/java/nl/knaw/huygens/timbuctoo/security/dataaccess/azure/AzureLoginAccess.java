package nl.knaw.huygens.timbuctoo.security.dataaccess.azure;

import com.google.common.collect.ImmutableMap;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.DynamicTableEntity;
import com.microsoft.azure.storage.table.EntityProperty;
import nl.knaw.huygens.timbuctoo.security.dataaccess.LoginAccess;
import nl.knaw.huygens.timbuctoo.security.dto.Login;
import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LoginCreationException;
import org.slf4j.Logger;

import java.util.Map;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

public class AzureLoginAccess extends AzureAccess implements LoginAccess {

  private static final Logger LOG = getLogger(AzureLoginAccess.class);

  public AzureLoginAccess(CloudTableClient client) throws AzureAccessNotPossibleException {
    super(client, "logins");
  }

  private Map<String, EntityProperty> objectToProps(Login source) {
    ImmutableMap.Builder<String, EntityProperty> builder = ImmutableMap.<String, EntityProperty>builder()
      .put("username", new EntityProperty(source.getUsername()))
      .put("userPid", new EntityProperty(source.getUserPid()))
      .put("password", new EntityProperty(source.getPassword()))
      .put("salt", new EntityProperty(source.getSalt()));
    if (source.getGivenName() != null) {
      builder.put("givenName", new EntityProperty(source.getGivenName()));
    }
    if (source.getSurName() != null) {
      builder.put("surName", new EntityProperty(source.getSurName()));
    }
    if (source.getEmailAddress() != null) {
      builder.put("emailAddress", new EntityProperty(source.getEmailAddress()));
    }
    if (source.getOrganization() != null) {
      builder.put("organization", new EntityProperty(source.getOrganization()));
    }
    return builder.build();
  }

  private Login makeLogin(DynamicTableEntity entity) {
    return Login.create(
      getStringOrNull(entity, "username"),
      getStringOrNull(entity, "userPid"),
      getByteArrayOrEmpty(entity, "password"),
      getByteArrayOrEmpty(entity, "salt"),
      getStringOrNull(entity, "givenName"),
      getStringOrNull(entity, "surName"),
      getStringOrNull(entity, "emailAddress"),
      getStringOrNull(entity, "organization")
    );
  }

  @Override
  public Optional<Login> getLogin(String username) throws LocalLoginUnavailableException {
    try {
      return retrieve("logins", username).map(this::makeLogin);
    } catch (StorageException e) {
      LOG.error("getLogin failed", e);
      throw new LocalLoginUnavailableException("Could not get login");
    }
  }

  @Override
  public void addLogin(Login login) throws LoginCreationException {
    try {
      super.create("logins", login.getUsername(), objectToProps(login));
    } catch (StorageException e) {
      LOG.error("addLogin failed", e);
      throw new LoginCreationException("Could not add login");
    }

  }
}
