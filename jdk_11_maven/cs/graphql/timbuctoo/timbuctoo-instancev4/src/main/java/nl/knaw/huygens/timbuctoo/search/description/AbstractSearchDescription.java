package nl.knaw.huygens.timbuctoo.search.description;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.search.EntityRef;
import nl.knaw.huygens.timbuctoo.search.SearchDescription;
import nl.knaw.huygens.timbuctoo.search.SearchResult;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortDescription;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.SearchRequestV2_1;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public abstract class AbstractSearchDescription implements SearchDescription {

  public static final SortDescription NO_OP_SORT_DESCRIPTION = new SortDescription(Lists.newArrayList());

  public EntityRef createRef(Vertex vertex) {
    String id = getIdDescriptor().get(vertex);

    EntityRef ref = new EntityRef(getType(), id);

    String displayName = getDisplayNameDescriptor().get(vertex);
    ref.setDisplayName(displayName);

    Map<String, Object> data = Maps.newHashMap();

    getDataPropertyDescriptors().entrySet().forEach(entry -> data.put(entry.getKey(), entry.getValue().get(vertex)));

    ref.setData(data);

    return ref;
  }

  protected GraphTraversal<Vertex, Vertex> initializeVertices(GraphWrapper graphWrapper) {
    return graphWrapper.getCurrentEntitiesFor(getType());
  }

  @Override
  public SearchResult execute(GraphWrapper graphWrapper, SearchRequestV2_1 searchRequest) {
    GraphTraversal<Vertex, Vertex> vertices = initializeVertices(graphWrapper);
    // filter by facets
    getFacetDescriptions().forEach(desc -> desc.filter(vertices, searchRequest.getFacetValues()));
    // filter by full text search
    searchRequest.getFullTextSearchParameters().forEach(param -> {
      Optional<FullTextSearchDescription> first = getFullTextSearchDescriptions()
        .stream()
        .filter(desc -> Objects.equals(param.getName(), desc.getName()))
        .findFirst();
      if (first.isPresent()) {
        first.get().filter(vertices, param);
      }
    });
    // order / sort
    getSortDescription().sort(vertices, searchRequest.getSortParameters());
    List<FacetCounter> facetCounters = getFacetDescriptions()
      .stream()
      .filter(desc -> !getOnlyFilterFacetList().contains(desc.getName()))
      .map(FacetCounter::new)
      .collect(toList());

    List<Vertex> searchResult = vertices.map(vertexTraverser -> {
      facetCounters.forEach(counter -> counter.addSearchHits(vertexTraverser.get()));
      return vertexTraverser.get();
    }).toList();

    List<Facet> facets = facetCounters.stream().map(FacetCounter::createFacet).collect(toList());

    return new SearchResult(searchResult, this, facets);
  }

  protected List<String> getOnlyFilterFacetList() {
    return Lists.newArrayList();
  }

  // Hooks

  protected abstract List<FacetDescription> getFacetDescriptions();

  protected abstract Map<String, PropertyDescriptor> getDataPropertyDescriptors();

  protected abstract PropertyDescriptor getDisplayNameDescriptor();

  protected abstract PropertyDescriptor getIdDescriptor();

  public abstract String getType();

  protected abstract List<FullTextSearchDescription> getFullTextSearchDescriptions();

  protected SortDescription getSortDescription() {
    return NO_OP_SORT_DESCRIPTION;
  }

  private static class FacetCounter {
    private FacetDescription facetDescription;
    private Map<String, Set<Vertex>> counts;

    public FacetCounter(FacetDescription facetDescription) {
      this.facetDescription = facetDescription;
      counts = Maps.newHashMap();
    }

    public String getName() {
      return facetDescription.getName();
    }

    public void addSearchHitForValue(String value, Vertex vertex) {
      if (!counts.containsKey(value)) {
        counts.put(value, Sets.newHashSet());
      }
      counts.get(value).add(vertex);
    }

    public Facet createFacet() {
      return facetDescription.getFacet(counts);
    }

    public void addSearchHits(Vertex vertex) {
      final List<String> facetValues = facetDescription.getValues(vertex);
      if (facetValues != null) {
        facetValues.stream().forEach(facetValue -> {
          this.addSearchHitForValue(facetValue, vertex);
        });
      }
    }
  }
}


