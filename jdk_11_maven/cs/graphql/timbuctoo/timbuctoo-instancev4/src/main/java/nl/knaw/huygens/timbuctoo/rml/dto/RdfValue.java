package nl.knaw.huygens.timbuctoo.rml.dto;

import java.util.Objects;
import java.util.Optional;

public class RdfValue implements QuadPart {

  protected final String value;
  private final String type;

  public RdfValue(String value, String type) {
    this.value = value;
    this.type = type;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RdfValue rdfValue = (RdfValue) obj;
    return Objects.equals(value, rdfValue.value) &&
      Objects.equals(type, rdfValue.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value, type);
  }

  @Override
  public String getContent() {
    return value;
  }

  @Override
  public Optional<String> getUri() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getLiteral() {
    return Optional.of(value);
  }

  @Override
  public Optional<String> getLiteralType() {
    return Optional.of(type);
  }

  @Override
  public Optional<String> getLiteralLanguage() {
    return Optional.empty();
  }
}
