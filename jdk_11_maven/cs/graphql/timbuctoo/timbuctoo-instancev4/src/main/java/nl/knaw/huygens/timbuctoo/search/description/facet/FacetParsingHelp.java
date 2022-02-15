package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.DateRangeFacetValue;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class FacetParsingHelp {
  static Optional<DateRangeFacetValue> getValue(String facetName, List<FacetValue> facets) {
    Optional<FacetValue> first = facets.stream()
      .filter(facetValue -> Objects.equals(facetValue.getName(), facetName))
      .findFirst();
    if (!first.isPresent()) {
      return Optional.empty();
    }
    FacetValue facetValue = first.get();

    if (!(facetValue instanceof DateRangeFacetValue)) {
      return Optional.empty();
    }

    DateRangeFacetValue casted = (DateRangeFacetValue) facetValue;

    //normalize input, make sure its always YYYY
    //the input from the client is YNNNN YYNNNN YYYNNNN etc. NNNN goes from 0000 to 9999 and means 1/10th milliYear
    // (1/10,000th of a year)
    casted.setLowerLimit(casted.getLowerLimit() / 10000);
    casted.setUpperLimit(casted.getUpperLimit() / 10000);

    return Optional.of(casted);
  }
}
