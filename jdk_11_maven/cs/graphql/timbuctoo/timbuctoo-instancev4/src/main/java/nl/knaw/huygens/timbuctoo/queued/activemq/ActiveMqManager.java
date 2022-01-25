package nl.knaw.huygens.timbuctoo.queued.activemq;

import com.kjetland.dropwizard.activemq.ActiveMQBundle;
import nl.knaw.huygens.timbuctoo.queued.activemq.ActiveMqQueueCreator;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueCreator;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueManager;

public class ActiveMqManager implements QueueManager {
  private final ActiveMQBundle activeMqBundle;

  public ActiveMqManager(ActiveMQBundle activeMqBundle) {
    this.activeMqBundle = activeMqBundle;
  }

  public <T> QueueCreator<T> createQueue(Class<T> messageType, String queueName) {
    return new ActiveMqQueueCreator<>(
      messageType,
      queueName,
      activeMqBundle
    );
  }
}
