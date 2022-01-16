package nl.knaw.huygens.timbuctoo.v5.serializable.serializations;

import nl.knaw.huygens.timbuctoo.v5.serializable.SerializableResult;
import nl.knaw.huygens.timbuctoo.v5.serializable.serializations.base.CollectionsOfEntitiesSerialization;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class GraphVizSerialization extends CollectionsOfEntitiesSerialization {

  protected final PrintWriter writer;

  public GraphVizSerialization(OutputStream outputStream) {
    writer = new PrintWriter(outputStream);
  }

  @Override
  public void serialize(SerializableResult serializableResult) throws IOException {
    super.serialize(serializableResult);
    Map<String, Integer> entityIds = new HashMap<>();
    AtomicInteger nodeCounter = new AtomicInteger();
    writer.println("digraph G {");
    for (Map<String, AggregatedEntity> entities : allEntities.values()) {
      for (Map.Entry<String, AggregatedEntity> entity : entities.entrySet()) {
        Integer nodeId = entityIds.computeIfAbsent(entity.getKey(), k -> nodeCounter.incrementAndGet());
        writer.println("node" + nodeId + " [label=\"" + entity.getKey() + "\"]");
        for (Map.Entry<String, Set<String>> relation : entity.getValue().relations.entrySet()) {
          for (String otherNodeUri : relation.getValue()) {
            Integer otherNodeId = entityIds.computeIfAbsent(otherNodeUri, k -> nodeCounter.incrementAndGet());
            writer.println("node" + nodeId + " -> node" + otherNodeId + "[label=\"" + relation.getKey() + "\"]");
          }
        }
      }
    }
    writer.println("}");
    writer.flush();
    writer.close();
  }
}
