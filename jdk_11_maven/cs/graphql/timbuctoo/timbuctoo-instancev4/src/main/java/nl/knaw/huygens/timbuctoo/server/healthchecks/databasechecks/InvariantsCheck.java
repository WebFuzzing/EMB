package nl.knaw.huygens.timbuctoo.server.healthchecks.databasechecks;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.healthchecks.CompositeValidationResult;
import nl.knaw.huygens.timbuctoo.server.healthchecks.DatabaseCheck;
import nl.knaw.huygens.timbuctoo.server.healthchecks.ElementValidationResult;
import nl.knaw.huygens.timbuctoo.server.healthchecks.ValidationResult;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.model.GraphReadUtils.getEntityTypesOrDefault;

/**
 * A database health check to checks that all vertices only relate to vertices from the same VRE.
 */
public class InvariantsCheck implements DatabaseCheck {

  public static final Logger LOG = LoggerFactory.getLogger(InvariantsCheck.class);
  private final Vres vres;

  public InvariantsCheck(Vres vres) {
    this.vres = vres;
  }

  @Override
  public void init(Graph graph, GraphDatabaseService service) {

  }

  @Override
  public ValidationResult check(Vertex vertex) {
    List<ValidationResult> validationResults = Lists.newArrayList();
    String[] vertexTypes = getEntityTypesOrDefault(vertex);
    String id = vertex.value("tim_id");

    vertex.edges(Direction.BOTH).forEachRemaining(edge -> {
      if (!Objects.equals(edge.label(), "VERSION_OF")) { // ignore the VERSION_OF relations
        String[] edgeTypes = getEntityTypesOrDefault(edge);
        for (String edgeType : edgeTypes) {
          Optional<Collection> collectionOptional = vres.getCollectionForType(edgeType);
          String edgeId = edge.value("tim_id");

          if (!collectionOptional.isPresent()) {
            validationResults.add(
              new ElementValidationResult(false,
                String.format("Edge with tim_id '%s' has contains unknown variant '%s'", edgeId, edgeType)));
            continue;
          }

          Collection edgeCollection = collectionOptional.get();
          // Only check accepted edges / relations, Timbuctoo sees not accepted relations as deleted.
          if (isAccepted(edge, edgeType) && vreContainsAVariationOfTheVertex(vertexTypes, edgeCollection)) {
            addInvalidVertex(validationResults, id, vertexTypes, edgeType, edgeId);
          }
        }
      }
    });
    return new CompositeValidationResult(validationResults);
  }

  @Override
  public void finish() {

  }

  private boolean isAccepted(Edge edge, String edgeType) {
    String acceptedProp = String.format("%s_accepted", edgeType);
    Property<Boolean> property = edge.property(acceptedProp);
    return property.isPresent() && property.value();
  }

  private boolean vreContainsAVariationOfTheVertex(String[] vertexTypes, Collection collection) {
    return collection.getVre().getOwnType(vertexTypes) == null;
  }

  private void addInvalidVertex(List<ValidationResult> validationResults, String id, String[] vertexTypes,
                                String edgeType, String edgeId) {
    if (vertexTypes[0].contains("language") || vertexTypes[0].contains("location")) {
      validationResults.add(new ElementValidationResult(false,
        String
          .format(
            "Vertex with tim_id '%s' with variants '%s' should have a variant to match variant '%s' of edge with " +
              "tim_id '%s'",
            id, Lists.newArrayList(vertexTypes), edgeType, edgeId)));
    } else {
      validationResults.add(new ElementValidationResult(false,
        String
          .format(
            "Vertex with tim_id '%s' with types '%s' has no variant to match '%s' of edge with tim_id '%s'",
            id, Lists.newArrayList(vertexTypes), edgeType, edgeId)));
    }
  }
}
