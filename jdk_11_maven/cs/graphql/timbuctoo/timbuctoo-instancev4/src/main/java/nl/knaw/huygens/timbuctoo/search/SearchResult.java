package nl.knaw.huygens.timbuctoo.search;

import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class SearchResult {

  // TODO add id used to store the result

  private List<Vertex> searchResults;
  private SearchDescription searchDescription;
  private List<Facet> facets;
  private UUID id;

  public SearchResult(List<Vertex> searchResults,
                      SearchDescription description,
                      List<Facet> facets) {

    this.searchResults = searchResults;
    this.searchDescription = description;
    this.facets = facets;
  }

  public List<EntityRef> getRefs() {
    return getRefs(0, 50);
  }

  public List<EntityRef> getRefs(long offset, int limit) {
    return searchResults.stream()
            .skip(offset).limit(limit)
            .map(result -> searchDescription.createRef(result))
            .collect(toList());
  }

  public List<String> getFullTextSearchFields() {
    return searchDescription.getFullTextSearchFields();
  }

  public List<String> getSortableFields() {
    return searchDescription.getSortableFields();
  }

  public List<Facet> getFacets() {
    return facets;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public int getCount() {
    return searchResults.size();
  }

  public SearchDescription getSearchDescription() {
    return searchDescription;
  }

  public List<Vertex> getSearchResult() {
    return searchResults;
  }
}
