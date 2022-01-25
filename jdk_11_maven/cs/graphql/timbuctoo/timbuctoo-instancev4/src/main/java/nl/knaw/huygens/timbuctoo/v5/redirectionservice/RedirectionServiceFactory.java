package nl.knaw.huygens.timbuctoo.v5.redirectionservice;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.kjetland.dropwizard.activemq.ActiveMQBundle;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueManager;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface RedirectionServiceFactory {
  /** We use queueing system for redirection services. This is optional for user implementations. */
  RedirectionService makeRedirectionService(QueueManager queueManager, DataSetRepository dataSetRepository);
}
