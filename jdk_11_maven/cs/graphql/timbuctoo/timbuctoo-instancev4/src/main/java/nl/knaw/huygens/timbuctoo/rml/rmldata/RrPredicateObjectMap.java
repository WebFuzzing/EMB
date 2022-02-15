package nl.knaw.huygens.timbuctoo.rml.rmldata;

import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;

import java.util.stream.Stream;

public interface RrPredicateObjectMap {
  Stream<Quad> generateValue(RdfUri subject, Row row);
}
