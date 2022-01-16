package nl.knaw.huygens.timbuctoo.rml.rmldata.builders;

import nl.knaw.huygens.timbuctoo.rml.rmldata.RrTriplesMap;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

class PromisedTriplesMap {
  private RrTriplesMap triplesMap;
  private Set<Consumer<RrTriplesMap>> consumers = new HashSet<>();

  void setTriplesMap(RrTriplesMap triplesMap) {
    this.triplesMap = triplesMap;
    for (Consumer<RrTriplesMap> consumer : this.consumers) {
      consumer.accept(triplesMap);
    }
  }

  void onTriplesMapReceived(Consumer<RrTriplesMap> consumer) {
    if (triplesMap == null) {
      this.consumers.add(consumer);
    } else {
      throw new RuntimeException("");
    }
  }
}
