package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import com.google.common.base.Charsets;
import nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter.Facet;
import org.immutables.value.Value;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Value.Immutable
public interface PaginatedList<T extends DatabaseResult> {

  Base64.Encoder ENCODER = Base64.getEncoder();

  Optional<String> getPrevCursor();

  Optional<String> getNextCursor();

  List<T> getItems();

  Optional<Long> getTotal();

  List<Facet> getFacets();

  static <U extends DatabaseResult> PaginatedList<U> create(String prevCursor, String nextCursor, List<U> items,
                                                            Optional<Long> total) {
    return ImmutablePaginatedList.<U>builder()
      .prevCursor(Optional.ofNullable(encode(prevCursor)))
      .nextCursor(Optional.ofNullable(encode(nextCursor)))
      .items(items)
      .total(total)
      .build();
  }

  static <U extends DatabaseResult> PaginatedList<U> create(String prevCursor, String nextCursor, List<U> items,
                                                            Optional<Long> total, List<Facet> facets) {
    return ImmutablePaginatedList.<U>builder()
      .prevCursor(Optional.ofNullable(encode(prevCursor)))
      .nextCursor(Optional.ofNullable(encode(nextCursor)))
      .items(items)
      .total(total)
      .facets(facets)
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
