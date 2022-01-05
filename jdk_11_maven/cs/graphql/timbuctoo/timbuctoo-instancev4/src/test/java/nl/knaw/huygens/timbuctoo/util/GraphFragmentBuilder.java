package nl.knaw.huygens.timbuctoo.util;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.function.Consumer;

public interface GraphFragmentBuilder {
  Tuple<Vertex, String> build(Graph graph, Consumer<RelationData> relationRequestor);
}
