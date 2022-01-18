package nl.knaw.huygens.timbuctoo.rml.rmldata;

import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;
import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps.RrTermMap;

import java.util.Optional;
import java.util.stream.Stream;


public class RrPredicateObjectMapOfTermMap implements RrPredicateObjectMap {

  private final RrTermMap objectMap;
  private final RrTermMap predicateMap;

  public RrPredicateObjectMapOfTermMap(RrTermMap predicateMap, RrTermMap objectMap) {
    this.predicateMap = predicateMap;
    this.objectMap = objectMap;
  }

  @Override
  public Stream<Quad> generateValue(RdfUri subject, Row row) {
    Optional<QuadPart> predicate = predicateMap.generateValue(row);
    Optional<QuadPart> object = objectMap.generateValue(row);
    if (predicate.isPresent() && object.isPresent()) {
      return Stream.of(Quad.create(subject, (RdfUri) predicate.get(), object.get()));
    } else {
      return Stream.empty();
    }
  }

  @Override
  public String toString() {
    return String.format("    predicateMap:\n%s    objectMap:\n%s    ================================\n",
      this.predicateMap,
      this.objectMap
    );
  }

}
