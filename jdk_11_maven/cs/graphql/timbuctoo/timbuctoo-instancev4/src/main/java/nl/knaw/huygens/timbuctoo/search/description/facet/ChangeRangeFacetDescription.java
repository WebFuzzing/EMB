package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Range;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.ChangeRangeFacetGetter;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.LocalPropertyValueGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.DateRangeFacetValue;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

class ChangeRangeFacetDescription extends AbstractFacetDescription {
  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;

  public static final Logger LOG = LoggerFactory.getLogger(ChangeRangeFacetDescription.class);
  private final ObjectMapper objectMapper;

  public ChangeRangeFacetDescription(String facetName, String propertyName) {
    super(facetName, propertyName, new ChangeRangeFacetGetter(), new LocalPropertyValueGetter());
    objectMapper = new ObjectMapper();
  }

  private LocalDate getChangeLocalDate(Object changeObjectString) throws IOException {
    Change change = objectMapper.readValue("" + changeObjectString, Change.class);
    return Instant.ofEpochMilli(change.getTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDate();
  }

  @Override
  @SuppressWarnings("unchecked")
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    Optional<FacetValue> first =
            facets.stream().filter(facetValue -> Objects.equals(facetValue.getName(), facetName)).findFirst();

    if (!first.isPresent()) {
      return;
    }
    FacetValue facetValue = first.get();

    if (!(facetValue instanceof DateRangeFacetValue)) {
      return;
    }

    long lowerLimit = ((DateRangeFacetValue) facetValue).getLowerLimit();
    LocalDate lowerLimitDate = LocalDate.parse("" + lowerLimit, FORMATTER);
    long upperLimit = ((DateRangeFacetValue) facetValue).getUpperLimit();
    LocalDate upperLimitDate = LocalDate.parse(("" + upperLimit), FORMATTER);
    // Use range because the java.time.Period has no way to determine if a date falls in that Period.
    Range<LocalDate> period = Range.closed(lowerLimitDate, upperLimitDate);

    graphTraversal.where(__.has(propertyName, P.test((o1, o2) -> {
      try {
        LocalDate localDate = getChangeLocalDate(o1);

        return ((Range<LocalDate>) o2).contains(localDate);
      } catch (IOException e) {
        LOG.error("Date {} cannot be parsed.", o1);
      }
      return false;
    }, period)));

  }
}
