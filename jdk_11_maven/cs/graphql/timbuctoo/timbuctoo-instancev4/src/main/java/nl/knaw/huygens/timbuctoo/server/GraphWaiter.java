package nl.knaw.huygens.timbuctoo.server;

import org.apache.tinkerpop.gremlin.structure.Graph;

import java.util.function.Consumer;

public interface GraphWaiter {
  void onGraph(Consumer<Graph> graphConsumer);
}
