package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.T;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public class VreIniter {
  private final GraphWrapper wrapper;
  private final Vres vres;

  public VreIniter(GraphWrapper wrapper, Vres vres) {
    this.wrapper = wrapper;
    this.vres = vres;
  }

  public Vertex upsertVre(String vreName, String vreLabel, String fileName) {
    final Vertex result;
    try (Transaction tx = wrapper.getGraph().tx()) {
      final GraphTraversal<Vertex, Vertex> vre = getVreTraversal(vreName);
      if (vre.hasNext()) {
        result = vre.next();
        if (result.property(TinkerpopSaver.SAVED_MAPPING_STATE).isPresent()) {
          result.property(TinkerpopSaver.SAVED_MAPPING_STATE).remove();
        }
        wrapper.getGraph().traversal().V(result.id())
          .out(TinkerpopSaver.RAW_COLLECTION_EDGE_NAME)
          .union(
            __.out(TinkerpopSaver.RAW_ITEM_EDGE_NAME),
            __.out(TinkerpopSaver.RAW_PROPERTY_EDGE_NAME),
            __.identity() //the collection
          )
          .drop()
          .toList();//force traversal and thus side-effects
      } else {
        result = wrapper.getGraph()
          .addVertex(T.label, Vre.DATABASE_LABEL, Vre.VRE_NAME_PROPERTY_NAME, vreName);
      }
      result.property(Vre.VRE_LABEL_PROPERTY_NAME, vreLabel);
      result.property(Vre.UPLOADED_FILE_NAME, fileName);
      result.property(Vre.PUBLISH_STATE_PROPERTY_NAME, Vre.PublishState.UPLOADING.toString());
      tx.commit();
    }

    vres.reload();
    return result;
  }

  public GraphTraversal<Vertex, Vertex> getVreTraversal(String vreName) {
    return wrapper.getGraph().traversal().V()
      .hasLabel(Vre.DATABASE_LABEL)
      .has(Vre.VRE_NAME_PROPERTY_NAME, vreName);
  }
}
