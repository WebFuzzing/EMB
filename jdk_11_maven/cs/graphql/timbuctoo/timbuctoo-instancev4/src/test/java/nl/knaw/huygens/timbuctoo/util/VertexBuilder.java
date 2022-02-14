package nl.knaw.huygens.timbuctoo.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.tinkerpop.gremlin.neo4j.structure.Neo4jVertex;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import static nl.knaw.huygens.timbuctoo.util.RelationData.RelationDataBuilder.makeRelationData;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;

public class VertexBuilder implements GraphFragmentBuilder {

  private final HashMap<String, Object> properties;
  private final List<RelationData.RelationDataBuilder> relationList;
  private String type;
  private List<String> vres;
  private String id;
  private final ObjectMapper objectMapper;
  private boolean isLatest;
  private List<String> labels;
  private String timId;

  VertexBuilder(String id) {
    this.id = id;
    objectMapper = new ObjectMapper();
    properties = Maps.newHashMap();
    relationList = Lists.newArrayList();
    labels = Lists.newArrayList();
  }

  @Override
  public Tuple<Vertex, String> build(Graph graph, Consumer<RelationData> relationRequestor) {
    Vertex vertex;
    if (labels.size() == 1) {
      // If there is exactly one label, it is still a valid tinkerpop vertex and needs to be passed to the
      // addVertex method. Otherwise the vertex also gets the label "vertex" and hasLabel will stop working
      vertex = graph.addVertex(labels.get(0));
    } else {
      vertex = graph.addVertex();
    }

    try {
      if (vres == null) {
        vres = Lists.newArrayList("");
      }
      if (type == null) {
        type = "<type>";
      }
      vertex.property("types", objectMapper.writeValueAsString(
        Lists.newArrayList(vres.stream().map(vre -> vre + type).iterator()))
      );
      if (timId != null) {
        vertex.property("tim_id", timId);
      }
      vertex.property("isLatest", isLatest);

      for (Map.Entry<String, Object> entry : properties.entrySet()) {
        vertex.property(entry.getKey(), entry.getValue());
      }

      if (labels.size() > 1) {
        for (String label : labels.subList(1, labels.size())) {
          ((Neo4jVertex) vertex).addLabel(label);
        }
      }
      relationList.forEach(relationDataBuilder -> {
        //can't be done earlier because vres might not be complete
        RelationData relationData = relationDataBuilder.build(vres);
        relationRequestor.accept(relationData);
      });

      return tuple(vertex, id);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public VertexBuilder withType(String type) {
    this.type = type;
    return this;
  }

  public VertexBuilder withVre(String vre) {
    if (vres == null) {
      vres = Lists.newArrayList();
    }
    this.vres.add(vre);
    return this;
  }

  public VertexBuilder withTimId(UUID id) {
    this.timId = id.toString();
    return this;
  }

  public VertexBuilder withTimId(String id) {
    this.timId = id;
    return this;
  }

  public VertexBuilder isLatest(boolean isLatest) {
    this.isLatest = isLatest;
    return this;
  }

  public VertexBuilder withProperty(String name, boolean value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, byte value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, short value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, int value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, long value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, float value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, double value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, char value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, String value) {
    properties.put(name, value);
    return this;
  }


  public VertexBuilder withProperty(String name, boolean[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, byte[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, short[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, int[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, long[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, float[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, double[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, char[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withProperty(String name, String[] value) {
    properties.put(name, value);
    return this;
  }

  public VertexBuilder withOutgoingRelation(String relationName, String otherVertex) {
    relationList.add(makeRelationData(relationName, id, otherVertex));
    return this;
  }

  public VertexBuilder withOutgoingRelation(String relationName, String otherVertex,
                                            Function<RelationData.RelationDataBuilder,
                                              RelationData.RelationDataBuilder> relationBuilder) {
    relationList.add(relationBuilder.apply(makeRelationData(relationName, id, otherVertex)));
    return this;
  }

  public VertexBuilder withIncomingRelation(String relationName, String otherVertex) {
    relationList.add(makeRelationData(relationName, otherVertex, id));
    return this;
  }

  public VertexBuilder withIncomingRelation(String relationName, String otherVertex,
                                            Function<RelationData.RelationDataBuilder,
                                              RelationData.RelationDataBuilder> relationBuilder) {
    relationList.add(relationBuilder.apply(makeRelationData(relationName, otherVertex, id)));
    return this;
  }

  public VertexBuilder withLabel(String label) {
    labels.add(label);

    return this;
  }

  public List<String> getLabels() {
    return labels;
  }


}
