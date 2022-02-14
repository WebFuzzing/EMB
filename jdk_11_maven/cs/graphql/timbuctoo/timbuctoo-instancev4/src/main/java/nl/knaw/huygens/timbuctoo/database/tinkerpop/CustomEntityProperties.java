package nl.knaw.huygens.timbuctoo.database.tinkerpop;

import nl.knaw.huygens.timbuctoo.core.dto.ReadEntityImpl;
import org.apache.tinkerpop.gremlin.structure.Vertex;

public interface CustomEntityProperties {
  void execute(ReadEntityImpl entity, Vertex entityVertex);
}
