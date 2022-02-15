package nl.knaw.huygens.timbuctoo.search;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static nl.knaw.huygens.timbuctoo.search.SearchDescription.ID_DB_PROP;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;

public class MockVertexBuilder {

  private final Vertex vertex;
  private final ObjectMapper objectMapper;
  private final Set<String> keys;
  private final Map<String, List<Vertex>> outGoingRelationMap;
  private final List<VertexProperty> properties;
  private final HashMap<String, List<Vertex>> incomingRelationMap;

  private MockVertexBuilder() {
    objectMapper = new ObjectMapper();
    vertex = mock(Vertex.class);
    keys = Sets.newHashSet();
    incomingRelationMap = Maps.newHashMap();
    outGoingRelationMap = incomingRelationMap;
    when(vertex.property(anyString())).thenReturn(mock(VertexProperty.class));
    properties = Lists.newArrayList();
  }

  public static MockVertexBuilder vertex() {
    return new MockVertexBuilder();
  }

  public static MockVertexBuilder vertexWithId(String id) {
    return vertex().withId(id);
  }

  public MockVertexBuilder withProperty(String key, String value) {
    keys.add(key);
    when(vertex.value(key)).thenReturn(value);

    VertexProperty property = createVertexProperty(key, value);
    when(vertex.property(key)).thenReturn(property);
    properties.add(property);

    return this;
  }

  public MockVertexBuilder withProperty(String key, Object value) {
    try {
      return withProperty(key, objectMapper.writeValueAsString(value));
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  private VertexProperty createVertexProperty(String key, String value) {
    VertexProperty property = mock(VertexProperty.class);
    when(property.key()).thenReturn(key);
    when(property.isPresent()).thenReturn(true);
    when(property.value()).thenReturn(value);
    return property;
  }


  public MockVertexBuilder withId(String id) {
    return withProperty(ID_DB_PROP, id);
  }

  public Vertex build() {
    when(vertex.keys()).thenReturn(keys);
    doReturn(properties.iterator()).when(vertex).properties();

    when(vertex.vertices(any(Direction.class), anyString())).thenReturn(Lists.<Vertex>newArrayList().iterator());
    buildEdges(Direction.OUT, outGoingRelationMap);
    buildEdges(Direction.IN, incomingRelationMap);


    return vertex;
  }

  private void buildEdges(Direction direction, Map<String, List<Vertex>> relationMap) {
    // Problem: with thenReturn iterator() is invoked at creation time,
    // fixed by using thenAnswer() because iterator() is now invoked on vertices
    // Problem2: vertices accepts a vararg, to support the vararg the vertices of all the relations should be combined
    when(vertex.vertices(argThat(is(direction)), any())).thenAnswer(invocationOnMock -> {
      List<Object> vertices = Lists.newArrayList();
      boolean isFirst = true;

      for (Object argument : invocationOnMock.getArguments()) {
        if (isFirst) {
          isFirst = false;
          continue;
        }
        if (relationMap.containsKey(argument)) {
          vertices.addAll(relationMap.get(argument));
        }
      }

      return vertices.iterator();
    });
    when(vertex.edges(argThat(is(direction)), any())).thenAnswer(invocationOnMock -> {
      List<Object> edges = Lists.newArrayList();
      boolean isFirst = true;

      for (Object argument : invocationOnMock.getArguments()) {
        if (isFirst) {
          isFirst = false;
          continue;
        }
        if (relationMap.containsKey(argument)) {

          if (direction == Direction.IN) {
            for (Vertex vertex1 : relationMap.get(argument)) {
              Edge edge = mock(Edge.class);
              when(edge.outVertex()).thenReturn(vertex1);
            }
          } else {
            for (Vertex vertex1 : relationMap.get(argument)) {
              Edge edge = mock(Edge.class);
              when(edge.inVertex()).thenReturn(vertex1);
            }
          }
          edges.addAll(relationMap.get(argument));
        }
      }

      return edges.iterator();
    });

  }

  public MockVertexBuilder withOutgoingRelation(String relationName, Vertex otherVertex) {
    withEdge(outGoingRelationMap, relationName, otherVertex);

    return this;
  }

  public MockVertexBuilder withIncomingRelation(String relationName, Vertex otherVertex) {
    withEdge(incomingRelationMap, relationName, otherVertex);

    return this;
  }

  private void withEdge(Map<String, List<Vertex>> relationMap, String relationName, Vertex otherVertex) {
    if (!relationMap.containsKey(relationName)) {
      relationMap.put(relationName, Lists.newArrayList());
    }

    relationMap.get(relationName).add(otherVertex);
  }
}
