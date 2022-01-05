package nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps;

import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;

import java.util.Optional;

public enum TermType {
  IRI,
  BlankNode,
  Literal;

  public static Optional<TermType> forNode(RdfResource node) {
    return node.asIri().flatMap(resource -> {
      switch (resource) {
        case "http://www.w3.org/ns/r2rml#IRI":
          return Optional.of(IRI);
        case "http://www.w3.org/ns/r2rml#BlankNode":
          return Optional.of(BlankNode);
        case "http://www.w3.org/ns/r2rml#Literal":
          return Optional.of(Literal);
        default:
          return Optional.empty();
      }
    });
  }
}
