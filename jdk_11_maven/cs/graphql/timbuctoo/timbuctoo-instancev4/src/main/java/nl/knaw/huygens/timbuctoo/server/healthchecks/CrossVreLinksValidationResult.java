package nl.knaw.huygens.timbuctoo.server.healthchecks;

import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Map;

public class CrossVreLinksValidationResult implements ValidationResult {
  private final Map<Vre, List<Vertex>> verticesLinkedAcrossVres;

  public CrossVreLinksValidationResult(Map<Vre, List<Vertex>> verticesLinkedAcrossVres) {
    this.verticesLinkedAcrossVres = verticesLinkedAcrossVres;
  }

  @Override
  public boolean isValid() {
    return verticesLinkedAcrossVres.isEmpty();
  }

  @Override
  public String getMessage() {
    throw new UnsupportedOperationException("Not implemented yet");
  }
}
