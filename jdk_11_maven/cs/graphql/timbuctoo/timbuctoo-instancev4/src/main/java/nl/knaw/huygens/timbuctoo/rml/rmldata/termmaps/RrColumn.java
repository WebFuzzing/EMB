package nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps;

import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.QuadPart;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfBlankNode;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfValue;

import java.util.Optional;

public class RrColumn implements RrTermMap {
  private final String referenceString;
  private TermType termType;
  private final String dataType;

  public RrColumn(String referenceString, TermType termType, String dataType) {
    this.referenceString = referenceString;
    this.termType = termType;
    this.dataType = dataType;
  }

  @Override
  public Optional<QuadPart> generateValue(Row input) {
    String value = input.getRawValue(referenceString);
    if (value == null) {
      return Optional.empty();
    }

    switch (termType) {
      case IRI:
        return Optional.of(new RdfUri(value));
      case BlankNode:
        return Optional.of(new RdfBlankNode(value));
      case Literal:
        return Optional.of(new RdfValue(value, dataType));
      default:
        throw new UnsupportedOperationException("Not all items in the Enumerable where handled");
    }
  }

  @Override
  public String toString() {
    return String.format("      Column: %s (%s)\n",
      this.referenceString,
      this.termType
    );
  }

}
