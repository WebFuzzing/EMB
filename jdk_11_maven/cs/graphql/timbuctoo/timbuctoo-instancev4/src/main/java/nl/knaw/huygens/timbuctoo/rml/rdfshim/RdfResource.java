package nl.knaw.huygens.timbuctoo.rml.rdfshim;

import java.util.Optional;
import java.util.Set;


public interface RdfResource {
  Set<RdfResource> out(String predicateUri);

  Optional<String> asIri();

  Optional<RdfLiteral> asLiteral();
}
