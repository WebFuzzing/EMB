package nl.knaw.huygens.timbuctoo.v5.dropwizard;

import io.dropwizard.lifecycle.Managed;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;

public class DataSetRepositoryManager implements Managed {
  private final DataSetRepository dataSetRepository;

  public DataSetRepositoryManager(DataSetRepository dataSetRepository) {
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public void start() throws Exception {
    dataSetRepository.start();
  }

  @Override
  public void stop() throws Exception {
    dataSetRepository.stop();
  }
}
