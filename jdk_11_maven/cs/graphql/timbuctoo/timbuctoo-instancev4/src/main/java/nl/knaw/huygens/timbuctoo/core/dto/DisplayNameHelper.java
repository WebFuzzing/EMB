package nl.knaw.huygens.timbuctoo.core.dto;

import com.fasterxml.jackson.databind.JsonNode;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.model.properties.ReadableProperty;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.logging.Logmarkers.databaseInvariant;

public class DisplayNameHelper {
  private static final Logger LOG = LoggerFactory.getLogger(DisplayNameHelper.class);

  public static Optional<String> getDisplayname(GraphTraversalSource traversalSource, Vertex vertex,
                                                Collection targetCollection) {
    ReadableProperty displayNameProperty = targetCollection.getDisplayName();
    if (displayNameProperty != null) {
      GraphTraversal<Vertex, Try<JsonNode>> displayNameGetter = traversalSource.V(vertex.id()).union(
        targetCollection.getDisplayName().traversalJson()
      );
      if (displayNameGetter.hasNext()) {
        Try<JsonNode> traversalResult = displayNameGetter.next();
        if (!traversalResult.isSuccess()) {
          LOG.debug(databaseInvariant, "Retrieving displayname failed", traversalResult.getCause());
        } else {
          if (traversalResult.get() == null) {
            LOG.debug(databaseInvariant, "Displayname was null");
          } else {
            if (!traversalResult.get().isTextual()) {
              LOG.debug(databaseInvariant, "Displayname was not a string but " + traversalResult.get().toString());
            } else {
              return Optional.of(traversalResult.get().asText());
            }
          }
        }
      } else {
        LOG.debug(databaseInvariant, "Displayname traversal resulted in no results: " + displayNameGetter);
      }
    } else {
      LOG.debug("No displayname configured for " + targetCollection.getEntityTypeName());
      //FIXME: deze wordt gegooid tijdens de finish. da's raar
    }
    return Optional.empty();
  }
}
