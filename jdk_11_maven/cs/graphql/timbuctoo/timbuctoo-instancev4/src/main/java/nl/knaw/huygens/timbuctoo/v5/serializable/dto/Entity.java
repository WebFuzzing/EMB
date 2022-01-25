package nl.knaw.huygens.timbuctoo.v5.serializable.dto;

import org.immutables.value.Value;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a data entry in the database as returned by the query. It has a URI, a type (the type as which it was
 * requested) and the contents that were requested.
 */
@Value.Immutable
@Value.Style(jdkOnly = true) //Needed to allow nulls in the collection
public interface Entity extends RdfData {
  String getUri();

  String getTypeUri();

  @AllowNulls
  Map<PredicateInfo, Serializable> getContents();

  @AllowNulls
  Map<String, Serializable> getContentsUnderSafeName();

  static Entity entity(String uri, String typeUri, Map<PredicateInfo, Serializable> contents) {
    Map<String, Serializable> contentsUnderSafeName = new HashMap<>();
    for (Map.Entry<PredicateInfo, Serializable> entry : contents.entrySet()) {
      contentsUnderSafeName.put(entry.getKey().getSafeName(), entry.getValue());
    }

    return ImmutableEntity.builder()
      .uri(uri)
      .typeUri(typeUri)
      .contents(contents)
      .contentsUnderSafeName(contentsUnderSafeName)
      .build();
  }
}
