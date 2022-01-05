package nl.knaw.huygens.timbuctoo.rml.rmldata;


import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps.RrTermMap;

import java.util.Optional;

public class RrSubjectMap {
  private final RrTermMap termMap;

  public RrSubjectMap(RrTermMap termMap) {
    this.termMap = termMap;
  }

  public Optional<RdfUri> generateValue(Row row) {
    return termMap.generateValue(row).map(x -> (RdfUri) x);
  }

  @Override
  public String toString() {
    return String.format("    SubjectMap: \n%s",
      this.termMap
    );
  }

}
