package nl.knaw.huygens.timbuctoo.search.description.facet;


import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.List;

public interface PropertyValueGetter {

  List<String> getValues(Vertex vertex, String propertyName);
}
