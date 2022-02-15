package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import com.google.common.base.Charsets;
import org.immutables.value.Value;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Value.Immutable
public interface PaginatedDynamicList {

  Base64.Encoder ENCODER = Base64.getEncoder();

  Optional<String> getPrevCursor();

  Optional<String> getNextCursor();

  List<SubjectReference> getEntities();

  List<TypedValue> getValues();

  static PaginatedDynamicList create(Optional<String> prevCursor, Optional<String> nextCursor,
                                     List<DatabaseResult> items) {
    List<SubjectReference> entities = new ArrayList<>();
    List<TypedValue> values = new ArrayList<>();

    for (DatabaseResult item : items) {
      if (item instanceof TypedValue) {
        values.add((TypedValue) item);
      } else if (item instanceof SubjectReference) {
        entities.add((SubjectReference) item);
      } else {
        throw new IllegalStateException("Item should be either a SubjectReference or a TypedValue");
      }
    }

    return ImmutablePaginatedDynamicList.builder()
      .prevCursor(prevCursor.map(PaginatedDynamicList::encode))
      .nextCursor(nextCursor.map(PaginatedDynamicList::encode))
      .entities(entities)
      .values(values)
      .build();
  }

  static String encode(String prevCursor) {
    if (prevCursor == null) {
      return null;
    } else {
      return ENCODER.encodeToString(prevCursor.getBytes(Charsets.UTF_8));
    }
  }
}
