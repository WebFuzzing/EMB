package nl.knaw.huygens.timbuctoo.rml.dto;

import java.util.Objects;
import java.util.Optional;

public class RdfLanguageTaggedString extends RdfValue {

  private final String language;

  public RdfLanguageTaggedString(String value, String language) {
    super(value, "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString");
    this.language = language;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    RdfLanguageTaggedString that = (RdfLanguageTaggedString) obj;
    return Objects.equals(language, that.language);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), language);
  }

  @Override
  public Optional<String> getLiteralLanguage() {
    return Optional.of(language);
  }
}
