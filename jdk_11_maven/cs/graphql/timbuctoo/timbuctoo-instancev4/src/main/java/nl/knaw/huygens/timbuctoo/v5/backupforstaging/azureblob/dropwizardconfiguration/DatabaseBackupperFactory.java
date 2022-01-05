package nl.knaw.huygens.timbuctoo.v5.backupforstaging.azureblob.dropwizardconfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.backupforstaging.DatabaseBackupper;

public class DatabaseBackupperFactory {

  @JsonProperty
  private UploaderFactory uploaderFactory;

  public DatabaseBackupper create(String neo4jPath, String bdbPath) throws Exception {
    return new DatabaseBackupper(
      neo4jPath,
      bdbPath,
      uploaderFactory.create()
    );
  }
}
