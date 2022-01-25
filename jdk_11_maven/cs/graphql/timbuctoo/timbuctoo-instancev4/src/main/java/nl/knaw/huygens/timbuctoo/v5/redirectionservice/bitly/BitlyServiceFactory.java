package nl.knaw.huygens.timbuctoo.v5.redirectionservice.bitly;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueManager;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionService;
import nl.knaw.huygens.timbuctoo.v5.redirectionservice.RedirectionServiceFactory;

public class BitlyServiceFactory implements RedirectionServiceFactory {

  @JsonProperty("accessToken")
  private final String accessToken;

  @JsonCreator
  public BitlyServiceFactory(@JsonProperty("accessToken") String accessToken) {
    this.accessToken = accessToken;
  }

  @Override
  public RedirectionService makeRedirectionService(QueueManager queueManager, DataSetRepository dataSetRepository) {
    return new BitlyService(queueManager, dataSetRepository, accessToken);
  }
}
