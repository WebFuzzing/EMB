package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.search.description.facet.Facet;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetGetter;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;

public class ChangeRangeFacetGetter implements FacetGetter {
  private final ObjectMapper objectMapper = new ObjectMapper();
  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.BASIC_ISO_DATE;
  public static final Logger LOG = LoggerFactory.getLogger(ChangeRangeFacetGetter.class);

  private LocalDate getChangeLocalDate(Object changeObjectString) throws IOException {
    Change change = objectMapper.readValue("" + changeObjectString, Change.class);
    return Instant.ofEpochMilli(change.getTimeStamp()).atZone(ZoneId.systemDefault()).toLocalDate();
  }

  @Override
  public Facet getFacet(String facetName, Map<String, Set<Vertex>> values) {
    long lowerLimit = 0;
    long upperLimit = 0;

    for (String key : values.keySet()) {
      try {
        LocalDate localDate = getChangeLocalDate(key);
        long dateStamp = Long.valueOf(FORMATTER.format(localDate));
        if (dateStamp > upperLimit) {
          upperLimit = dateStamp;
        }
        if (lowerLimit == 0 || dateStamp < lowerLimit) {
          lowerLimit = dateStamp;
        }
      } catch (IOException e) {
        LOG.error("'{}' is not a valid change.", key);
      }
    }

    return new Facet(facetName, Lists.newArrayList(new Facet.RangeOption(lowerLimit, upperLimit)), "RANGE");
  }
}
