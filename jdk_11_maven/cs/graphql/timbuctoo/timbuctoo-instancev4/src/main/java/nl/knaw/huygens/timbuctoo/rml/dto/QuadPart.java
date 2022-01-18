package nl.knaw.huygens.timbuctoo.rml.dto;

import java.util.Optional;

public interface QuadPart {
  String getContent();

  Optional<String> getUri();

  Optional<String> getLiteral();

  Optional<String> getLiteralType();

  Optional<String> getLiteralLanguage();
}
