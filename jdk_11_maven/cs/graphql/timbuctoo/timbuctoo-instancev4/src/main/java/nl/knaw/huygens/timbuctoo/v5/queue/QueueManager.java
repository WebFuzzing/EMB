package nl.knaw.huygens.timbuctoo.v5.queue;


public interface QueueManager {
  <T> QueueCreator<T> createQueue(Class<T> messageType, String queueName);
}
