package nl.knaw.huygens.timbuctoo.server.healthchecks.databasechecks;

import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.model.LocationNames;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.model.TempName;
import nl.knaw.huygens.timbuctoo.search.description.PropertyDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.search.description.property.WwDocumentDisplayNameDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import nl.knaw.huygens.timbuctoo.server.healthchecks.DatabaseCheck;
import nl.knaw.huygens.timbuctoo.server.healthchecks.ElementValidationResult;
import nl.knaw.huygens.timbuctoo.server.healthchecks.ValidationResult;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;

import java.util.HashMap;

import static nl.knaw.huygens.timbuctoo.model.GraphReadUtils.getEntityTypesOrDefault;

public class FullTextIndexCheck implements DatabaseCheck {

  private final HashMap<String, PropertyDescriptor> displayNameDescriptors;
  protected GraphDatabaseService databaseService;
  protected Graph graph;

  public FullTextIndexCheck() {
    final PropertyDescriptorFactory propertyDescriptorFactory = new PropertyDescriptorFactory(
            new PropertyParserFactory());

    displayNameDescriptors = Maps.newHashMap();
    displayNameDescriptors.put("wwdocuments", new WwDocumentDisplayNameDescriptor());
    displayNameDescriptors.put("wwpersons", propertyDescriptorFactory.getComposite(
            propertyDescriptorFactory.getLocal("wwperson_names", PersonNames.class),
            propertyDescriptorFactory.getLocal("wwperson_tempName", TempName.class)));
    displayNameDescriptors.put("wwkeywords", propertyDescriptorFactory.getLocal("wwkeyword_value", String.class));
    displayNameDescriptors.put("wwlanguages", propertyDescriptorFactory.getLocal("wwlanguage_name", String.class));
    displayNameDescriptors.put("wwlocations", propertyDescriptorFactory.getLocal("names", LocationNames.class));
    displayNameDescriptors.put("wwcollectives", propertyDescriptorFactory.getLocal("wwcollective_name", String.class));
  }

  @Override
  public void init(Graph graph, GraphDatabaseService service) {
    this.graph = graph;
    this.databaseService = service;
  }

  @Override
  public ValidationResult check(Vertex vertex) {
    Boolean isLatest = vertex.property("isLatest").isPresent() ?
            (Boolean) vertex.property("isLatest").value() :
            false;

    if (isLatest) {
      final IndexManager indexManager = databaseService.index();
      String[] types = getEntityTypesOrDefault(vertex);
      Transaction transaction = graph.tx();

      // Apparently a lucene search needs a transaction to be open:
      // http://stackoverflow.com/questions/19428017
      if (!transaction.isOpen()) {
        transaction.open();
      }

      for (String type : types) {
        if (indexManager.existsForNodes(type + "s")) {
          final String timId = vertex.property("tim_id").isPresent() ?
                  (String) vertex.property("tim_id").value() :
                  null;
          if (timId == null) {
            transaction.close();
            return new ElementValidationResult(false, String.format("Vertex %s misses tim_id", vertex));
          } else {
            final Index<Node> index = indexManager.forNodes(type + "s");
            IndexHits<Node> hits = index.get("tim_id", timId);
            if (hits.size() < 1) {
              String message = String.format("Vertex with tim_id %s not found in index %ss", timId, type);
              transaction.close();
              return new ElementValidationResult(false, message);
            }
            if (hits.size() > 1) {
              String message = String.format("Vertex with tim_id %s has duplicate entries in index %ss", timId, type);
              transaction.close();
              return new ElementValidationResult(false, message);
            }

            GraphTraversal<Vertex, Vertex> found = graph.traversal().V(hits.next().getId());
            if (!found.hasNext()) {
              String message = String.format("Vertex from index with tim_id %s is not present in graphdb", timId);
              transaction.close();
              return new ElementValidationResult(false, message);
            }

            Vertex foundVertex = found.next();
            final PropertyDescriptor descriptor = displayNameDescriptors.get(type + "s");
            if (descriptor.get(vertex) != null && descriptor.get(foundVertex) != null &&
                    !descriptor.get(vertex).equals(descriptor.get(foundVertex))) {
              String message = String.format(
                      "Displayname of vertex from index does not match latest vertex with tim_id %s", timId);
              transaction.close();
              return new ElementValidationResult(false, message);
            }

          }
        }
      }
      transaction.close();
    }
    return new ElementValidationResult(true, String.format("Vertex %s is valid", vertex));
  }

  @Override
  public void finish() {
    this.graph = null;
    this.databaseService = null;
  }

}
