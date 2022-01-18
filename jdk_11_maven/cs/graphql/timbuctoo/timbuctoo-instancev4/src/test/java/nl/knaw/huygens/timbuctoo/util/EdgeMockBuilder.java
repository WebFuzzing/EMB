package nl.knaw.huygens.timbuctoo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.io.graphml.GraphMLWriter;

import java.util.List;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.search.MockVertexBuilder.vertex;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EdgeMockBuilder {

  private final Map<String, Object> properties;
  private final ObjectMapper objectMapper;
  private final Vertex inVertex;
  private final Vertex outVertex;

  private EdgeMockBuilder() {
    properties = Maps.newHashMap();
    objectMapper = new ObjectMapper();
    inVertex = vertex().build();
    outVertex = vertex().build();
  }

  public static EdgeMockBuilder edge() {
    return new EdgeMockBuilder();
  }

  public EdgeMockBuilder withProperty(String key, long value) {
    this.properties.put(key, value);
    return this;
  }

  public EdgeMockBuilder withProperty(String key, int value) {
    this.properties.put(key, value);
    return this;
  }

  public EdgeMockBuilder withProperty(String key, double value) {
    this.properties.put(key, value);
    return this;
  }

  public EdgeMockBuilder withProperty(String key, String value) {
    this.properties.put(key, value);
    return this;
  }

  public EdgeMockBuilder withProperty(String key, Object value) {
    try {
      this.properties.put(key, objectMapper.writeValueAsString(value));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
    return this;
  }

  public EdgeMockBuilder withId(String id) {
    this.withProperty("tim_id", id);
    return this;
  }

  public Edge build() {
    Edge edge = mock(Edge.class);
    when(edge.inVertex()).thenReturn(inVertex);
    when(edge.outVertex()).thenReturn(outVertex);

    List<Property> allProps = Lists.newArrayList();
    given(edge.keys()).willReturn(properties.keySet());
    properties.forEach((key, value) -> addEdgeProperty(edge, key, value, allProps));

    doReturn(allProps.iterator()).when(edge).properties();

    return edge;
  }

  private void addEdgeProperty(Edge edge, String key, Object value, List<Property> allProps) {
    Property property = mock(Property.class);
    when(property.key()).thenReturn(key);
    when(property.isPresent()).thenReturn(true);
    when(property.value()).thenReturn(value);
    when(edge.property(key)).thenReturn(property);
    when(edge.value(key)).thenReturn(value);
    allProps.add(property);
  }



}
