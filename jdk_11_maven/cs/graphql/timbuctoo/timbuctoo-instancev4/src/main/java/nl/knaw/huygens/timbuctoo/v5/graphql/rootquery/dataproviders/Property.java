package nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders;

import org.immutables.value.Value;

@Value.Immutable
public interface Property {
  String getName();

  long getDensity();

  boolean getIsList();

  StringList getReferencedCollections();

  default StringList getReferencedCollections(int count, String cursor) {
    return getReferencedCollections();
  }

  boolean getIsValueType();

  boolean getIsInverse();

  String getUri();

  String getShortenedUri();

}
