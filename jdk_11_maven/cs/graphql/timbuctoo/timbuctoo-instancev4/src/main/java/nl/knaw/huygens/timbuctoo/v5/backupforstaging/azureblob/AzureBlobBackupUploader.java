package nl.knaw.huygens.timbuctoo.v5.backupforstaging.azureblob;

import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import nl.knaw.huygens.timbuctoo.v5.backupforstaging.exceptions.BackupUploadException;
import nl.knaw.huygens.timbuctoo.v5.backupforstaging.BackupUploader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;

public class AzureBlobBackupUploader implements BackupUploader {
  private final CloudBlobContainer container;
  private final String blobname;

  public AzureBlobBackupUploader(CloudBlobContainer container, String blobname) throws StorageException {
    this.container = container;
    this.blobname = blobname;
    container.createIfNotExists();
  }

  @Override
  public void storeBackup(File zipfile) throws BackupUploadException {
    try {
      final CloudBlockBlob blob = container.getBlockBlobReference(blobname);
      blob.upload(new FileInputStream(zipfile), zipfile.length());
    } catch (URISyntaxException | StorageException | IOException e) {
      throw new BackupUploadException(e);
    }
  }
}
