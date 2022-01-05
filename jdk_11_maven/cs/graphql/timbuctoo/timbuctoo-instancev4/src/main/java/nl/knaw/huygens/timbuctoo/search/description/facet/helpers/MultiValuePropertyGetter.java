package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.description.facet.PropertyValueGetter;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class MultiValuePropertyGetter implements PropertyValueGetter {
  public static final Logger LOG = LoggerFactory.getLogger(MultiValuePropertyGetter.class);
  private static final ObjectMapper mapper = new ObjectMapper();


  @Override
  public List<String> getValues(Vertex vertex, String propertyName) {
    if (vertex.property(propertyName).isPresent()) {
      final String value = (String) vertex.property(propertyName).value();
      try {
        return (List<String>) mapper.readValue(value, List.class);
      } catch (IOException e) {
        LOG.error("'{}' is not a valid multi valued field", value);
      }
    }
    return Lists.newArrayList();
  }
}
