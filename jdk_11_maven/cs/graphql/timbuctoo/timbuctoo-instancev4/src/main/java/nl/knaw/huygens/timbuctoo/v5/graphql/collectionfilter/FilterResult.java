package nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter;

import java.util.List;

public interface FilterResult {
  List<String> getUriList();

  String getNextToken();

  int getTotal();

  List<Facet> getFacets();

  String getPrevToken();
}
