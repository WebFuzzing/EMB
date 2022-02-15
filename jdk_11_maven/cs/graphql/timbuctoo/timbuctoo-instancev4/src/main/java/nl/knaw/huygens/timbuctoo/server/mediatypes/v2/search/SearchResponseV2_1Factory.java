package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import nl.knaw.huygens.timbuctoo.search.EntityRef;
import nl.knaw.huygens.timbuctoo.search.SearchResult;
import nl.knaw.huygens.timbuctoo.util.UriHelper;

import java.util.List;

public class SearchResponseV2_1Factory {

  private final SearchResponseV2_1RefAdder refCreator;
  private NavigationCreator navigationCreator;

  public SearchResponseV2_1Factory(UriHelper uriHelper) {
    this.refCreator = new SearchResponseV2_1RefAdder();
    this.navigationCreator = new NavigationCreator(uriHelper);
  }

  /**
   * Make sure {@code value} is between {@code minValue} and {@code maxValue}.
   *
   * @param value    the value that has to be in the range
   * @param minValue the minimum value of the range
   * @param maxValue the maximum value of the range
   * @return {@code value} if it's in the range,
   * {@code minValue} if {@code value} is lower than the {@code minValue},
   * {@code maxValue} if {@code value} is higher than the {@code maxValue}
   */
  private static int mapToRange(int value, int minValue, int maxValue) {
    return Math.min(Math.max(value, minValue), maxValue);
  }

  public SearchResponseV2_1 createResponse(SearchResult searchResult,
                                           int rows,
                                           int start) {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    searchResponse.setFullTextSearchFields(searchResult.getFullTextSearchFields());
    searchResponse.setSortableFields(searchResult.getSortableFields());
    searchResponse.setStart(start);
    searchResponse.setFacets(searchResult.getFacets());

    int numFound = searchResult.getCount();
    searchResponse.setNumFound(numFound);
    int normalizedStart = mapToRange(start, 0, numFound);
    int normalizedRows = mapToRange(rows, 0, numFound - normalizedStart);
    List<EntityRef> refs = searchResult.getRefs(normalizedStart, normalizedRows);
    refs.forEach(ref -> refCreator.addRef(searchResponse, ref));
    navigationCreator.next(searchResponse, rows, start, numFound, searchResult.getId());
    navigationCreator.prev(searchResponse, rows, start, numFound, searchResult.getId());

    return searchResponse;
  }
}
