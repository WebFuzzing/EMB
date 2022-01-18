package nl.knaw.huygens.timbuctoo.server.tasks;

import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

public class MigrateTruePatchStoresTask extends Task {
  private final DataSetRepository dataSetRepository;

  public MigrateTruePatchStoresTask(DataSetRepository dataSetRepository) {
    super("migrateTruePatchStores");
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public void execute(Map<String, List<String>> parameters, PrintWriter output) throws Exception {
    for (DataSet dataSet : dataSetRepository.getDataSets()) {
      output.println("Migrate dataset: " + dataSet.getMetadata().getCombinedId());
      output.flush();

      dataSet.getTruePatchStore().migrate();

      output.println("Finished migration of dataset: " + dataSet.getMetadata().getCombinedId());
      output.flush();
    }
  }
}
