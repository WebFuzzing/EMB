package nl.knaw.huygens.timbuctoo.server.tasks;

import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class BackupTask extends Task {
  private static final Logger LOG = LoggerFactory.getLogger(BackupTask.class);
  private static final String BACKUP_PATH = "backupPath";
  private DataSetRepository dataSetRepository;

  public BackupTask(DataSetRepository dataSetRepository) {
    super("backup");
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    if (!parameters.containsKey(BACKUP_PATH)) {
      output.println("Please provide a \"" + BACKUP_PATH + "\"");
      output.flush();
      return;
    }
    for (DataSet dataSet : dataSetRepository.getDataSets()) {
      try {
        dataSet.backupDatabases(parameters.get(BACKUP_PATH).iterator().next());
        LOG.info("backup dataset: {}", dataSet.getMetadata().getCombinedId());
        output.println("backup dataset: " + dataSet.getMetadata().getCombinedId());
        output.flush();
      } catch (Throwable t) {
        LOG.error("sync of {} failed", dataSet.getMetadata().getCombinedId());
        output.println("sync of '" + dataSet.getMetadata().getCombinedId() + "' failed");
        output.flush();
        LOG.error("Exception thrown", t);
      }
    }
    LOG.info("backup complete");
    output.println("backup complete");
    output.flush();
  }
}
