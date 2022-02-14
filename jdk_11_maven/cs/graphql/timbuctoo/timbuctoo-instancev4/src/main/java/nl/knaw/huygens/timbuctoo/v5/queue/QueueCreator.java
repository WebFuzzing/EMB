package nl.knaw.huygens.timbuctoo.v5.queue;

import java.util.function.Consumer;

public interface QueueCreator<T> {
  public void registerReceiver(Consumer<T> consumer);

  public QueueSender createSender();
}
