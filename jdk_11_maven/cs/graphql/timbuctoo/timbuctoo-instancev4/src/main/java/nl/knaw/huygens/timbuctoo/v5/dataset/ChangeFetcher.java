package nl.knaw.huygens.timbuctoo.v5.dataset;

import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;

import java.util.stream.Stream;

public interface ChangeFetcher {

  Stream<CursorQuad> getPredicates(String subject, boolean getRetracted, boolean getUnchanged, boolean getAsserted);

  Stream<CursorQuad> getPredicates(String subject, String predicate, Direction direction, boolean getRetracted,
                                   boolean getUnchanged, boolean getAsserted);
}
