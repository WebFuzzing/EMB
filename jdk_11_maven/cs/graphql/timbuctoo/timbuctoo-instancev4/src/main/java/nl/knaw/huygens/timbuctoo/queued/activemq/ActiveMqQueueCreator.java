package nl.knaw.huygens.timbuctoo.queued.activemq;

import com.kjetland.dropwizard.activemq.ActiveMQBundle;
import com.kjetland.dropwizard.activemq.ActiveMQSender;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueCreator;
import nl.knaw.huygens.timbuctoo.v5.queue.QueueSender;

import java.util.function.Consumer;

public class ActiveMqQueueCreator<T> implements QueueCreator<T> {
  private final Class<T> messageType;
  private final String queueName;
  private final ActiveMQBundle mq;

  public ActiveMqQueueCreator(Class<T> messageType, String queueName, ActiveMQBundle mq) {
    this.messageType = messageType;
    this.queueName = queueName;
    this.mq = mq;
  }

  public void registerReceiver(Consumer<T> consumer) {
    mq.registerReceiver(
      "queue:" + queueName,
      consumer::accept,
      messageType,
      false
    );
  }

  public QueueSender createSender() {
    ActiveMQSender sender = mq.createSender("queue:" + queueName, true);
    return sender::send;
  }

}
