package nl.knaw.huygens.timbuctoo.rml.rmldata;

import nl.knaw.huygens.timbuctoo.rml.ErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.MappingDocumentBuilder;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class RmlMappingDocument {

  private final List<RrTriplesMap> triplesMaps;
  private List<String> errors;

  public RmlMappingDocument(List<RrTriplesMap> triplesMaps, List<String> errors) {
    this.triplesMaps = triplesMaps;
    this.errors = errors;
  }

  public Stream<Quad> execute(ErrorHandler defaultErrorHandler) {
    if (errors.size() > 0) {
      throw new RuntimeException("Mapping contains errors");
    }
    return triplesMaps.stream().flatMap(map -> map.getItems(defaultErrorHandler));
  }

  public static MappingDocumentBuilder rmlMappingDocument() {
    return new MappingDocumentBuilder();
  }

  public List<String> getErrors() {
    return errors;
  }

  @Override
  public String toString() {
    String errorDump;
    if (errors.size() > 0) {
      errorDump = "\nErrors:\n" + String.join("\n", errors);
    } else {
      errorDump = "";
    }
    return "MappingDocument: \n" + errorDump + "\n\n" +
      String.join("", this.triplesMaps.stream().map(x -> String.format("%s", x)).collect(toList()));
  }
}
