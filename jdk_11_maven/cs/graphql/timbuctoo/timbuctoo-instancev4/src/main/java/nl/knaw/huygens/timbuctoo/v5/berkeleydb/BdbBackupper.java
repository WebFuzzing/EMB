package nl.knaw.huygens.timbuctoo.v5.berkeleydb;


import com.google.common.collect.Lists;
import com.sleepycat.je.Environment;
import com.sleepycat.je.util.DbBackup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.google.common.base.Charsets.UTF_8;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class BdbBackupper {
  public void backupDatabase(Environment environment, Path dbPath, Path backupPath) throws IOException {
    final Path lastBackuppedFilePath = backupPath.resolve("lastFile");
    final DbBackup backupHelper = lastBackuppedFilePath.toFile().exists() ?
        new DbBackup(environment, lastFile(lastBackuppedFilePath)) :
        new DbBackup(environment);
    environment.sync();

    try {
      backupHelper.startBackup();
      final String[] logFilesInBackupSet = backupHelper.getLogFilesInBackupSet();
      for (String logFile : logFilesInBackupSet) {
        Files.copy(dbPath.resolve(logFile), backupPath.resolve(logFile), REPLACE_EXISTING);
      }
      final long lastFileInBackupSet = backupHelper.getLastFileInBackupSet();
      Files.write(lastBackuppedFilePath, Lists.newArrayList("" + lastFileInBackupSet), UTF_8  );
    } finally {
      backupHelper.endBackup();
    }

  }

  private long lastFile(Path lastBackuppedFilePath) throws IOException {
    return Long.parseLong(Files.readString(lastBackuppedFilePath).trim());
  }
}
