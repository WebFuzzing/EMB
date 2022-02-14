package nl.knaw.huygens.timbuctoo.v5.backupforstaging;

import nl.knaw.huygens.timbuctoo.v5.backupforstaging.exceptions.BackupUploadException;

import java.io.File;

public interface BackupUploader {
  void storeBackup(File zipfile) throws BackupUploadException;
}
