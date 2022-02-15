package nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto;

import org.immutables.value.Value;

import java.util.Optional;

@Value.Immutable
public interface CursorQuad {
  String getSubject();

  String getPredicate();

  String getObject();

  Optional<String> getValuetype();

  Optional<String> getLanguage();

  Direction getDirection();

  @Value.Auxiliary
  ChangeType getChangeType();

  @Value.Auxiliary
  String getCursor();

  static CursorQuad create(String subject, String predicate, Direction direction, String object, String valueType,
                           String language, String cursor) {
    return create(subject, predicate, direction, ChangeType.UNCHANGED, object, valueType, language, cursor);
  }

  static CursorQuad create(String subject, String predicate, Direction direction, ChangeType changeType, String object,
                           String valueType, String language, String cursor) {
    return ImmutableCursorQuad.builder()
      .subject(subject)
      .predicate(predicate)
      .object(object)
      .valuetype(Optional.ofNullable(valueType))
      .language(Optional.ofNullable(language))
      .cursor(cursor)
      .direction(direction)
      .changeType(changeType)
      .build();
  }

}
