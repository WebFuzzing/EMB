package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.base.Strings;
import com.google.common.collect.Range;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.DatableRangeFacetGetter;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.RelatedPropertyValueGetter;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.DateRangeFacetValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class RelatedDatableRangeFacetDescription extends AbstractFacetDescription {
  public static final SimpleDateFormat FILTER_FORMAT = new SimpleDateFormat("yyyy");
  public static final Logger LOG = LoggerFactory.getLogger(DatableRangeFacetDescription.class);
  private final String[] relations;


  public RelatedDatableRangeFacetDescription(String facetName, String propertyName, String... relations) {
    super(facetName, propertyName, new DatableRangeFacetGetter(), new RelatedPropertyValueGetter(relations));
    this.relations = relations;
  }

  private Datable getDatable(String datableAsString) {
    String value = StringUtils.strip(datableAsString, "\"");

    return new Datable(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    Optional<FacetValue> first = facets.stream()
            .filter(facetValue -> Objects.equals(facetValue.getName(), facetName))
            .findFirst();

    if (!first.isPresent()) {
      return;
    }
    FacetValue facetValue = first.get();

    if (!(facetValue instanceof DateRangeFacetValue)) {
      return;
    }

    // pad the strings to make them parsable
    String lowerLimitString =
            Strings.padStart("" + ((DateRangeFacetValue) facetValue).getLowerLimit(), 8, '0').substring(0, 4);
    String upperLimitString =
            Strings.padStart("" + ((DateRangeFacetValue) facetValue).getUpperLimit(), 8, '0').substring(0, 4);

    try {
      Range<Date> range = Range.closed(FILTER_FORMAT.parse(lowerLimitString), FILTER_FORMAT.parse(upperLimitString));

      graphTraversal.where(__.bothE(relations).otherV().has(propertyName, P.test((o1, o2) -> {
        Datable datable = getDatable("" + o1);
        if (!datable.isValid()) {
          return false;
        }

        Range<Date> range1 = (Range<Date>) o2;
        return range1.contains(datable.getFromDate()) || range1.contains(datable.getToDate());
      }, range)));
    } catch (ParseException e) {
      LOG.error("Cannot parse date", e);
    }

  }
}
