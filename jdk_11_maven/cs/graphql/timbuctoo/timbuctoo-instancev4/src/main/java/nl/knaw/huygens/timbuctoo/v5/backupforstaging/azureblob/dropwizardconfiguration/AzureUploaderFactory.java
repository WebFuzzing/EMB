package nl.knaw.huygens.timbuctoo.v5.backupforstaging.azureblob.dropwizardconfiguration;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.microsoft.azure.storage.CloudStorageAccount;
import nl.knaw.huygens.timbuctoo.v5.backupforstaging.BackupUploader;
import nl.knaw.huygens.timbuctoo.v5.backupforstaging.azureblob.AzureBlobBackupUploader;

public class AzureUploaderFactory implements UploaderFactory {

  @JsonProperty
  private String connectionString;

  @JsonProperty
  private String containerReference;

  @JsonProperty
  private String blobname;

  @Override
  public BackupUploader create() throws Exception {
    return new AzureBlobBackupUploader(
      CloudStorageAccount
        .parse(connectionString)
        .createCloudBlobClient()
        .getContainerReference(containerReference),
      blobname);
  }
}
