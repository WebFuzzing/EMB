package nl.knaw.huygens.timbuctoo.v5.datastores.quadstore;

import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;

import java.util.stream.Stream;

public interface QuadStore {
  Stream<CursorQuad> getQuads(String subject, String predicate, Direction direction, String cursor);

  Stream<CursorQuad> getQuads(String subject);

  Stream<CursorQuad> getAllQuads();

  void close();

  void commit();

  boolean isClean();
}
