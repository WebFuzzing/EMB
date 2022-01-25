package nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class Facet {

  private static final Logger LOG = LoggerFactory.getLogger(Facet.class);

  final LinkedHashMap<String, FacetOption> options = new LinkedHashMap<>();
  final String caption;

  public Facet(String caption) {
    this.caption = caption;
  }

  public void incOption(String key, int value) {
    if (options.containsKey(key)) {
      LOG.warn("The aggregation '" + caption + "' resulted the same bucket (" + key + ") being mentioned twice. " +
        "We did not expect that to be possible");
      options.put(key, FacetOption.facetOption(key, options.get(key).getCount() + value));
    } else {
      options.put(key, FacetOption.facetOption(key, value));
    }
  }

  public String getCaption() {
    return caption;
  }

  public List<FacetOption> getOptions() {
    return new ArrayList<>(options.values());
  }
}
