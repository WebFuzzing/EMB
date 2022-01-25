package nl.knaw.huygens.timbuctoo.databaselog;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface LogOutput {
  void prepareToWrite();

  void newVertex(Vertex vertex);

  void updateVertex(Vertex vertex);

  void newEdge(Edge edge);

  void updateEdge(Edge edge);

  void newProperty(Property property);

  void updateProperty(Property property);

  void deleteProperty(String propertyName);

  void finishWriting();
}
