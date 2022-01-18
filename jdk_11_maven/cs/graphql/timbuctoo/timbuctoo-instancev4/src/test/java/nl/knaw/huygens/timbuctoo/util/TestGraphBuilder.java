package nl.knaw.huygens.timbuctoo.util;

import nl.knaw.huygens.timbuctoo.server.TestableTinkerPopGraphManager;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jGraph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;
import org.neo4j.tinkerpop.api.impl.Neo4jGraphAPIImpl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.UUID.randomUUID;
import static nl.knaw.huygens.timbuctoo.util.Neo4jHelper.cleanDb;

//FIXME should become a rule
public class TestGraphBuilder {

  private static GraphDatabaseService reusableNeo4jDb = new TestGraphDatabaseFactory().newImpermanentDatabase();
  private static Transaction trans;
  private final List<GraphFragmentBuilder> graphFragmentBuilders = new LinkedList<>();
  private final GraphDatabaseService neo4jDb;

  // FIXME do use the label to map the relations
  private TestGraphBuilder(GraphDatabaseService neo4jDb) {
    this.neo4jDb = neo4jDb;
  }

  public static TestGraphBuilder newGraph() {
    return new TestGraphBuilder(reusableNeo4jDb);
  }

  public static TestGraphBuilder newSlowPrivateGraph() {
    return new TestGraphBuilder(new TestGraphDatabaseFactory().newImpermanentDatabase());
  }

  public Neo4jGraph build() {
    //When creating a new database you have to close the previous one, because
    //each database will create a new thread. This fills up the available
    //threads when running thousands of tests.
    //
    //Closing the database is very slow however (testruns go from less then a
    //second to 3 minutes on a machine with an SSD) so instead we now re-use
    //the same neo4j database that we clear when the function to create a new
    //one is called.
    if (neo4jDb == reusableNeo4jDb) {
      if (trans != null) {
        trans.close();
      }
      cleanDb(neo4jDb);
    }
    Neo4jGraphAPIImpl neo4jGraphApi = new Neo4jGraphAPIImpl(neo4jDb);
    Neo4jGraph neo4jGraph = Neo4jGraph.open(neo4jGraphApi);

    try (org.apache.tinkerpop.gremlin.structure.Transaction tx = neo4jGraph.tx()) {
      final List<RelationData> requestedRelations = new LinkedList<>();
      final Map<String, Vertex> identifiableVertices = new HashMap<>();
      //Create all identifiable vertices
      graphFragmentBuilders.forEach(
        builder -> {
          Tuple<Vertex, String> result = builder.build(neo4jGraph, requestedRelations::add);
          identifiableVertices.put(result.getRight(), result.getLeft());
        }
      );
      //then we can create the relations between them
      requestedRelations.forEach(
        relationData -> relationData.makeRelation(identifiableVertices)
      );
      tx.commit();
    }

    if (neo4jDb == reusableNeo4jDb) { //we're re-using the database
      trans = neo4jDb.beginTx(); //make sure we own the toplevel transaction so are guaranteed to close it
    }
    return neo4jGraph;
  }

  public TinkerPopGraphManager wrap() {
    final Neo4jGraph graph = build();
    return new TestableTinkerPopGraphManager(neo4jDb, graph);
  }

  public TestGraphBuilder withVertex(String id, Consumer<VertexBuilder> vertexBuilderConfig) {
    VertexBuilder vb = new VertexBuilder(id);
    vertexBuilderConfig.accept(vb);
    graphFragmentBuilders.add(vb);
    return this;
  }

  public TestGraphBuilder withVertex(Consumer<VertexBuilder> vertexBuilderConfig) {
    return withVertex(randomUUID().toString(), vertexBuilderConfig);
  }

}
