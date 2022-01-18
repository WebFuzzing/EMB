package nl.knaw.huygens.timbuctoo.search.description.facet.helpers;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.search.description.facet.PropertyValueGetter;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public class LocalPropertyValueGetter implements PropertyValueGetter {

  @Override
  public List<String> getValues(Vertex vertex, String propertyName) {
    if (vertex.property(propertyName).isPresent()) {
      return Lists.newArrayList((String) vertex.property(propertyName).value());
    }
    return null;
  }
}
