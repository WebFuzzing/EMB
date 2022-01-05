package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetGetter;
import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

public class DatableRangeFacetGetter implements FacetGetter {
  private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyyMMdd");

  @Override
  public Facet getFacet(String facetName, Map<String, Set<Vertex>> values) {
    long lowerLimit = 0;
    long upperLimit = 0;

    for (String key : values.keySet()) {
      Datable datable = getDatable(key);
      if (datable.isValid()) {
        long fromDate = Long.valueOf(FORMAT.format(datable.getFromDate()));
        long toDate = Long.valueOf(FORMAT.format(datable.getToDate()));
        if (toDate > upperLimit) {
          upperLimit = toDate;
        }
        if (lowerLimit == 0 || fromDate < lowerLimit) {
          lowerLimit = fromDate;
        }
      }
    }

    return new Facet(facetName, Lists.newArrayList(new Facet.RangeOption(lowerLimit, upperLimit)), "RANGE");
  }

  private Datable getDatable(String datableAsString) {
    String value = StringUtils.strip(datableAsString, "\"");

    return new Datable(value);
  }

}
