package nl.knaw.huygens.timbuctoo.server.databasemigration;

import nl.knaw.huygens.timbuctoo.relationtypes.RelationTypeService;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static nl.knaw.huygens.timbuctoo.rdf.RdfProperties.RDFINDEX_NAME;
import static nl.knaw.huygens.timbuctoo.rdf.RdfProperties.RDF_SYNONYM_PROP;
import static nl.knaw.huygens.timbuctoo.rdf.RdfProperties.RDF_URI_PROP;

public class RelationTypeRdfUriMigration implements DatabaseMigration {
  private static final Logger LOG = LoggerFactory.getLogger(RelationTypeRdfUriMigration.class);
  public static final String TIMBUCTOO_NAMESPACE = "http://timbuctoo.huygens.knaw.nl/";

  @Override
  public void execute(TinkerPopGraphManager graphWrapper) throws IOException {
    final Graph graph = graphWrapper.getGraph();
    final Transaction transaction = graph.tx();

    if (!transaction.isOpen()) {
      transaction.open();
    }

    final GraphDatabaseService graphDatabase = graphWrapper.getGraphDatabase();
    final Index<Node> rdfIndex = graphDatabase.index().forNodes(RDFINDEX_NAME);

    final String regularNameProp = "relationtype_regularName";
    final String inverseNameProp = "relationtype_inverseName";

    graph.traversal().V().has(regularNameProp).forEachRemaining(vertex -> {
      final String regularName = vertex.property(regularNameProp).isPresent() ?
        vertex.<String>property(regularNameProp).value() : "";

      final String inverseName = vertex.property(inverseNameProp).isPresent() ?
        vertex.<String>property(inverseNameProp).value() : "";

      final String rdfUri = TIMBUCTOO_NAMESPACE + regularName;
      final String[] rdfAlternatives = new String[]{ TIMBUCTOO_NAMESPACE + inverseName };

      LOG.info("setting rdfUri: \"{}\" and rdfAlternatives [{}] for relationType", rdfUri, rdfAlternatives);
      vertex.property(RDF_URI_PROP, rdfUri);
      vertex.property(RDF_SYNONYM_PROP, rdfAlternatives);

      LOG.info("indexing rdfUri: \"{}\" and rdfAlternatives [{}] for relationType", rdfUri, rdfAlternatives);
      org.neo4j.graphdb.Node neo4jNode = graphDatabase.getNodeById((Long) vertex.id());
      rdfIndex.add(neo4jNode, RelationTypeService.RELATIONTYPE_INDEX_NAME, rdfUri);
      rdfIndex.add(neo4jNode, RelationTypeService.RELATIONTYPE_INDEX_NAME, rdfAlternatives[0]);
    });

    transaction.commit();
    transaction.close();
  }
}
