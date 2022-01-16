package nl.knaw.huygens.timbuctoo.v5.backupforstaging.azureblob.dropwizardconfiguration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.timbuctoo.v5.backupforstaging.BackupUploader;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface UploaderFactory {
  BackupUploader create() throws Exception;
}
