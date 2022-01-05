package nl.knaw.huygens.timbuctoo.rml.dto;

import org.immutables.value.Value;

@Value.Immutable
public interface Quad {

  RdfUri getSubject();

  RdfUri getPredicate();

  QuadPart getObject();

  static Quad create(RdfUri subject, RdfUri predicate, QuadPart object) {
    return ImmutableQuad.builder()
      .subject(subject)
      .predicate(predicate)
      .object(object)
      .build();
  }

}
