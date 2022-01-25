package nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders;

import org.immutables.value.Value;

@Value.Immutable
public interface ProvenanceInfo {
  String getTitle();

  String getBody();

  static ProvenanceInfo provenanceInfo(String title, String body) {
    return ImmutableProvenanceInfo.builder()
      .title(title)
      .body(body)
      .build();
  }
}
