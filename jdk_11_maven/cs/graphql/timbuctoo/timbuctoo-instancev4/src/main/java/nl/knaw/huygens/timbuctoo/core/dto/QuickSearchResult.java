package nl.knaw.huygens.timbuctoo.core.dto;

import org.immutables.value.Value;

import java.util.UUID;

@Value.Immutable
public interface QuickSearchResult {
  String getIndexedValue();

  UUID getId();

  int getRev();

  static QuickSearchResult create(String indexedValue, UUID id, int rev) {
    return ImmutableQuickSearchResult.builder()
      .indexedValue(indexedValue)
      .id(id)
      .rev(rev)
      .build();
  }
}
