package nl.knaw.huygens.timbuctoo.v5.redirectionservice;

import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueManager;

public class DummyRedirectionServiceFactory implements RedirectionServiceFactory {
  @Override
  public RedirectionService makeRedirectionService(QueueManager queueManager, DataSetRepository dataSetRepository) {
    return new DummyRedirectionService(queueManager, dataSetRepository);
  }
}
