package nl.knaw.huygens.timbuctoo.v5.dropwizard.tasks;

import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.v5.backupforstaging.DatabaseBackupper;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class StagingBackup extends Task {
  private final DatabaseBackupper backupper;

  public StagingBackup(DatabaseBackupper backupper) {
    super("staging-backup");
    this.backupper = backupper;
  }

  @Override
  public void execute(Map<String, List<String>> immutableMultimap, PrintWriter printWriter) throws Exception {
    backupper.makeBackup();
    printWriter.append("{status: \"success\"}");
    printWriter.close();
  }
}
