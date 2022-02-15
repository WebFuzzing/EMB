package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class EdgeManipulator {

  public static Edge duplicateEdge(Edge edgeToDuplicate) {
    Edge duplicate = createDuplicate(edgeToDuplicate);

    addProperties(edgeToDuplicate, duplicate);

    changeLatest(duplicate, edgeToDuplicate);
    return duplicate;
  }

  private static void changeLatest(Edge duplicate, Edge edgeToDuplicate) {
    duplicate.property("isLatest", true);
    edgeToDuplicate.property("isLatest", false);
  }

  private static Edge createDuplicate(Edge edgeToDuplicate) {
    Vertex sourceOfEdge = edgeToDuplicate.outVertex();
    Vertex targetOfEdge = edgeToDuplicate.inVertex();

    return sourceOfEdge.addEdge(edgeToDuplicate.label(), targetOfEdge);
  }

  private static void addProperties(Edge edgeToDuplicate, Edge duplicate) {
    edgeToDuplicate.properties()
      .forEachRemaining(prop -> duplicate.property(prop.key(), prop.value()));
  }
}
