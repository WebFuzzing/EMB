package nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter;

import org.immutables.value.Value;

@Value.Immutable
public interface FacetOption {
  String getName();

  int getCount();

  static FacetOption facetOption(String name, int count) {
    return ImmutableFacetOption.builder()
      .count(count)
      .name(name)
      .build();
  }
}
