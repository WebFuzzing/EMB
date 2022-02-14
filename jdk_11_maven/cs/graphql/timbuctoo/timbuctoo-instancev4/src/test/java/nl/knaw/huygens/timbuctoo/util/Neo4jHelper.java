package nl.knaw.huygens.timbuctoo.util;


import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.PropertyContainer;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.index.RelationshipIndex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Neo4jHelper {

  private static final Logger log = LoggerFactory.getLogger(Neo4jHelper.class);

  public static void dumpDb(GraphDatabaseService gds) {
    for (Node node : gds.getAllNodes()) {
      System.out.println(dumpNode(node));
    }
    for (Relationship rel : gds.getAllRelationships()) {
      System.out.println(dumpEdge(rel));
    }
  }

  private static String dumpNode(Node node) {
    try {
      ArrayList<String> labels = new ArrayList<>();
      for (Label label : node.getLabels()) {
        if (!"vertex".equals(label.name())) {
          labels.add(label.name());
        }
      }
      return String.format("(%d) [%s] %s ", node.getId(), String.join(", ", labels), dumpProps(node));
    } catch (Exception e) {
      return "(" + node.getId() + ") " + e.getMessage();
    }
  }

  private static String dumpEdge(Relationship edge) {
    try {
      return String.format("(%s) -[%s %s]-> (%s)",
        edge.getStartNode().getId(),
        edge.getType().name(),
        dumpProps(edge),
        edge.getEndNode().getId()
      );
    } catch (Exception e) {
      return "//!!!" + edge.getId() + ": " + e.getMessage() + "!!!";
    }
  }

  private static String dumpProps(PropertyContainer pc) {
    try {
      Map<String, Object> props = new HashMap<>();
      for (String prop : pc.getPropertyKeys()) {
        props.put(prop, pc.getProperty(prop));
      }
      return String.format("%s", props);
    } catch (Exception e) {
      return "{!!!" + e.getMessage() + "!!!}";
    }
  }

  public static void cleanDb(GraphDatabaseService graphDatabaseService) {
    cleanDb(graphDatabaseService, false);
  }

  public static void cleanDb( GraphDatabaseService graphDatabaseService, boolean includeReferenceNode ) {
    Transaction tx = graphDatabaseService.beginTx();
    try {
      clearIndex(graphDatabaseService);
      removeNodes(graphDatabaseService, includeReferenceNode);
      tx.success();
    } catch (Throwable t) {
      tx.failure();
      throw new RuntimeException("Error cleaning database ",t);
    } finally {
      tx.close();
    }
  }

  private static void removeNodes(GraphDatabaseService graphDatabaseService, boolean includeReferenceNode) {
    for (Node node : graphDatabaseService.getAllNodes()) {
      for (Relationship rel : node.getRelationships(Direction.OUTGOING)) {
        try {
          rel.delete();
        } catch (IllegalStateException ise) {
          if (!ise.getMessage().contains("since it has already been deleted")) {
            throw ise;
          }
        }

      }
      for (Label label: node.getLabels()) {
        node.removeLabel(label);
      }
    }
    for (Node node : graphDatabaseService.getAllNodes()) {
      try {
        node.delete();
      } catch (IllegalStateException ise) {
        if (!ise.getMessage().contains("since it has already been deleted")) {
          throw ise;
        }
      }
    }
  }

  private static void clearIndex(GraphDatabaseService gds) {
    IndexManager indexManager = gds.index();
    for (String ix : indexManager.nodeIndexNames()) {
      try {
        Index<Node> nodeIndex = indexManager.forNodes(ix);
        if (nodeIndex.isWriteable()) {
          nodeIndex.delete();
        }
      } catch (Exception e) {
        log.warn("Cannot delete node index " + ix + " " + e.getMessage());
      }
    }
    for (String ix : indexManager.relationshipIndexNames()) {
      try {
        RelationshipIndex relationshipIndex = indexManager.forRelationships(ix);
        if (relationshipIndex.isWriteable()) {
          relationshipIndex.delete();
        }
      } catch (Exception e) {
        log.warn("Cannot delete relationship index " + ix + " " + e.getMessage());
      }
    }
  }
}
