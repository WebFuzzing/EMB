package nl.knaw.huygens.timbuctoo.rml.dto;

import java.util.Objects;
import java.util.Optional;

public class RdfUri implements QuadPart {
  private final String uri;

  public RdfUri(String uri) {
    this.uri = uri;
  }

  @Override
  public String getContent() {
    return uri;
  }

  @Override
  public Optional<String> getUri() {
    return Optional.of(uri);
  }

  @Override
  public Optional<String> getLiteral() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getLiteralType() {
    return Optional.empty();
  }

  @Override
  public Optional<String> getLiteralLanguage() {
    return Optional.empty();
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    RdfUri rdfUri = (RdfUri) obj;
    return Objects.equals(uri, rdfUri.uri);
  }

  @Override
  public int hashCode() {
    return Objects.hash(uri);
  }
}
