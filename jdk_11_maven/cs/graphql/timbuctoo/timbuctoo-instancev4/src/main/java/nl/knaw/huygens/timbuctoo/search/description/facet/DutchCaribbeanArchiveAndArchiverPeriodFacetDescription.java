package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.FacetDescription;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.DatableRangeFacetGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.DateRangeFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

class DutchCaribbeanArchiveAndArchiverPeriodFacetDescription implements FacetDescription {
  private final String facetName;
  private final String beginYear;
  private final String endYear;
  private FacetGetter facetGetter;

  public DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(String facetName, String beginYear, String endYear) {
    this(facetName, beginYear, endYear, new DatableRangeFacetGetter());
  }

  DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(String facetName, String beginYear, String endYear,
                                                         FacetGetter facetGetter) {
    this.facetName = facetName;
    this.beginYear = beginYear;
    this.endYear = endYear;
    this.facetGetter = facetGetter;
  }

  @Override
  public String getName() {
    return facetName;
  }

  @Override
  public Facet getFacet(Map<String, Set<Vertex>> values) {
    return facetGetter.getFacet(facetName, values);
  }

  @Override
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    final Optional<DateRangeFacetValue> facetOrEmpty = FacetParsingHelp.getValue(facetName, facets);
    facetOrEmpty.ifPresent(facet -> {
      long begin;
      long end;
      if (facet.getUpperLimit() >= facet.getLowerLimit()) {
        begin = facet.getLowerLimit();
        end = facet.getUpperLimit();
      } else {
        end = facet.getLowerLimit();
        begin = facet.getUpperLimit();
      }
      /*
       *        Vs------------Ve
       *   Qs-------------------------Qe
       *   Qs-------Qe
       *            Qs----Qe
       *                  Qs----------Qe
       * match when: Qs < Ve && Qe > Vs
       */
      graphTraversal //FIXME: how to deal with items that have a beginYear, but no endYear
                     .has(beginYear)
                     .has(endYear)
                     .filter(filterYear(year -> {
                       //System.out.println(year  + " <= " + end + ": " + (year <= end));
                       return year <= end;
                     }, beginYear))
                     .filter(filterYear(year -> {
                       //System.out.println(year  + " >= " + end + ": " + (year >= end));
                       return year >= begin;
                     }, endYear));
    });
  }

  private Predicate<Traverser<Vertex>> filterYear(Predicate<Long> filter, String propertyName) {
    return t -> {
      try {
        String dateStr = t.get().<String>value(propertyName);
        if (dateStr.length() > 4) {
          dateStr = dateStr.substring(0, 4);
        }
        final long year = Long.parseLong(dateStr);
        return filter.test(year);
      } catch (NumberFormatException e) {
        return false;
      }
    };
  }

  @Override
  public List<String> getValues(Vertex vertex) {
    List<String> values = Lists.newArrayList();

    if (vertex.keys().contains(beginYear)) {
      values.add(vertex.value(beginYear));
    }
    if (vertex.keys().contains(endYear)) {
      values.add(vertex.value(endYear));
    }

    return values;
  }
}
