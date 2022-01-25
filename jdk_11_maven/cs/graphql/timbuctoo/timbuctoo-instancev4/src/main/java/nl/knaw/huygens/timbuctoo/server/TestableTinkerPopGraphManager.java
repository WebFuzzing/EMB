package nl.knaw.huygens.timbuctoo.server;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopConfig;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.io.IoCore;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;

import java.util.LinkedHashMap;

import static org.slf4j.LoggerFactory.getLogger;

public class TestableTinkerPopGraphManager extends TinkerPopGraphManager {
  private static final Logger LOG = getLogger(TestableTinkerPopGraphManager.class);

  public TestableTinkerPopGraphManager(GraphDatabaseService graphDatabaseService, Neo4jGraph graph) {
    super(new TinkerPopConfig(), new LinkedHashMap<>());
    this.graphDatabase = graphDatabaseService;
    this.graph = graph;
  }

  @Override
  public void start() throws Exception {
    callWaiters();
  }

  @Override
  protected Result check() throws Exception {
    return Result.healthy();
  }

  @Override
  protected void initGraphDatabaseService() {
  }

  public String writeGraph() {
    java.io.OutputStream output = new java.io.OutputStream() {
      private StringBuilder stringBuilder = new StringBuilder();
      @Override
      public void write(int chr) throws java.io.IOException {
        stringBuilder.append((char) chr );
      }

      @Override
      public String toString() {
        return stringBuilder.toString();
      }
    };
    try {
      graph.io(IoCore.graphson()).writer().create().writeGraph(output, graph);
    } catch (java.io.IOException e) {
      e.printStackTrace();
    }

    String wellformedJson = "[" + String.join(",\n", (CharSequence[]) output.toString().split("\n")) + "]";
    com.google.gson.Gson gson = new GsonBuilder().setPrettyPrinting().create();
    com.google.gson.JsonParser jp = new JsonParser();
    com.google.gson.JsonElement je = jp.parse(wellformedJson);
    return gson.toJson(je);
  }

}
