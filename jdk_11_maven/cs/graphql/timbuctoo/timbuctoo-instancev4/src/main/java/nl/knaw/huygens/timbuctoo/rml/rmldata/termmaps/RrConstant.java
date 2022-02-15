package nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps;

import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfValue;

import java.util.Optional;

public class RrConstant implements RrTermMap {
  private final QuadPart value;

  public RrConstant(String value) {
    this.value = new RdfUri(value);
  }

  public RrConstant(String value, TermType termType, String dataType) {
    if (termType == TermType.Literal) {
      this.value = new RdfValue(value, dataType);
    } else {
      this.value = new RdfUri(value);
    }
  }

  @Override
  public Optional<QuadPart> generateValue(Row input) {
    return Optional.of(value);
  }

  @Override
  public String toString() {
    return String.format("      Constant: %s\n",
      this.value
    );
  }

}
