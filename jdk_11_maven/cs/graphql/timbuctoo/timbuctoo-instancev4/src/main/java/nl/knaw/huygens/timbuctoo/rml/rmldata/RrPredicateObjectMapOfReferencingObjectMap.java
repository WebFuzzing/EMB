package nl.knaw.huygens.timbuctoo.rml.rmldata;

import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;
import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps.RrRefObjectMap;
import nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps.RrTermMap;

import java.util.Optional;
import java.util.stream.Stream;

public class RrPredicateObjectMapOfReferencingObjectMap implements RrPredicateObjectMap {

  private final RrTermMap predicateMap;
  private final boolean isInverted;
  private final RrRefObjectMap objectMap;

  public RrPredicateObjectMapOfReferencingObjectMap(RrTermMap predicateMap, boolean isInverted,
                                                    RrRefObjectMap objectMap) {
    this.predicateMap = predicateMap;
    this.isInverted = isInverted;
    this.objectMap = objectMap;
  }

  @Override
  public Stream<Quad> generateValue(RdfUri subject, Row row) {
    Optional<QuadPart> predicateOpt = predicateMap.generateValue(row);
    if (predicateOpt.isPresent()) {
      RdfUri predicate = (RdfUri) predicateOpt.get();
      if (isInverted) {
        return objectMap
          .generateValue(row)
          .map(value -> Quad.create((RdfUri) value, predicate, subject));
      } else {
        return objectMap
          .generateValue(row)
          .map(value -> Quad.create(subject, predicate, value));
      }
    } else {
      return Stream.empty();
    }
  }

  @Override
  public String toString() {
    return String.format("    isInverted: %s\n    predicateMap:\n%s    objectMap:\n%s    " +
        "================================\n",
      this.isInverted,
      this.predicateMap,
      this.objectMap
    );
  }

}
