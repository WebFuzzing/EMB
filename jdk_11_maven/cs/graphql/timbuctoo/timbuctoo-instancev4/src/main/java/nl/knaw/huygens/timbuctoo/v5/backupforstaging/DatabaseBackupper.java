package nl.knaw.huygens.timbuctoo.v5.backupforstaging;

import nl.knaw.huygens.timbuctoo.v5.backupforstaging.exceptions.BackupUploadException;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class DatabaseBackupper {
  private final String neo4jPath;
  private final String dataSetPath;
  private final BackupUploader uploader;

  public DatabaseBackupper(String neo4jPath, String dataSetPath, BackupUploader uploader) {
    this.neo4jPath = neo4jPath;
    this.dataSetPath = dataSetPath;
    this.uploader = uploader;
  }

  public void makeBackup() throws BackupUploadException {
    try {
      final File tempFile = File.createTempFile("stagingBackup", "zip");
      try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(tempFile.getAbsolutePath()))) {
        backupFolder(neo4jPath, zos);
        backupFolder(dataSetPath, zos);
      }
      uploader.storeBackup(tempFile);
    } catch (IOException e) {
      throw new BackupUploadException(e);
    }
  }

  private void backupFolder(String folder, ZipOutputStream zos) throws IOException {
    try (Stream<Path> fileStream = Files.walk(Paths.get(folder))) {

      final Iterator<Path> pathStream = fileStream.filter(Files::isRegularFile).iterator();
      while (pathStream.hasNext()) {
        String file = pathStream.next().toFile().getAbsolutePath();
        zos.putNextEntry(new ZipEntry(file));
        try (FileInputStream in = new FileInputStream(file)) {
          IOUtils.copy(in, zos);
        }
        zos.closeEntry();
      }
    }
  }
}
