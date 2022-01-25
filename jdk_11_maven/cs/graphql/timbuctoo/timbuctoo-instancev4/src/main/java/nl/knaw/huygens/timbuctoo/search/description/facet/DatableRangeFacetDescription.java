package nl.knaw.huygens.timbuctoo.search.description.facet;

import com.google.common.collect.Range;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.search.FacetValue;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.DatableRangeFacetGetter;
import nl.knaw.huygens.timbuctoo.search.description.facet.helpers.LocalPropertyValueGetter;
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
import java.util.Optional;

public class DatableRangeFacetDescription extends AbstractFacetDescription {
  public static final SimpleDateFormat FILTER_FORMAT = new SimpleDateFormat("yyyy");
  public static final Logger LOG = LoggerFactory.getLogger(DatableRangeFacetDescription.class);

  public DatableRangeFacetDescription(String facetName, String propertyName) {
    super(facetName, propertyName,new DatableRangeFacetGetter(), new LocalPropertyValueGetter());
  }

  private Datable getDatable(String datableAsString) {
    String value = StringUtils.strip(datableAsString, "\"");
    return new Datable(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public void filter(GraphTraversal<Vertex, Vertex> graphTraversal, List<FacetValue> facets) {
    final Optional<DateRangeFacetValue> facet = FacetParsingHelp.getValue(facetName, facets);
    if (!facet.isPresent()) {
      return;
    }

    String lowerLimitString = facet.get().getLowerLimit() + "";
    String upperLimitString = facet.get().getUpperLimit() + "";

    try {
      Range<Date> range = Range.closed(FILTER_FORMAT.parse(lowerLimitString), FILTER_FORMAT.parse(upperLimitString));

      graphTraversal.where(__.has(propertyName, P.test((o1, o2) -> {
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
