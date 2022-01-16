package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.apache.tinkerpop.gremlin.groovy.DefaultImportCustomizerProvider;
import org.apache.tinkerpop.gremlin.groovy.jsr223.GremlinGroovyScriptEngine;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.BulkSet;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.slf4j.Logger;

import javax.script.Bindings;
import javax.script.ScriptException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import static org.apache.tinkerpop.gremlin.structure.Direction.IN;
import static org.apache.tinkerpop.gremlin.structure.Direction.OUT;

@Path("/v2.1/gremlin")
public class Gremlin {
  private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(Gremlin.class);

  private final GremlinGroovyScriptEngine engine;
  private final GraphWrapper wrapper;
  private final Bindings bindings;
  private final ObjectMapper mapper;

  public Gremlin(GraphWrapper wrapper) {
    this.wrapper = wrapper;
    this.engine = new GremlinGroovyScriptEngine();

    final DefaultImportCustomizerProvider provider = new DefaultImportCustomizerProvider();
    final Set<String> allImports = provider.getAllImports();
    allImports.removeIf(path -> path.indexOf("groovy.") > -1);
    engine.addImports(allImports);
    engine.addImports(Sets.newHashSet("import org.apache.tinkerpop.gremlin.neo4j.process.traversal.LabelP"));

    this.bindings = engine.createBindings();
    mapper = new ObjectMapper();
  }

  @POST
  @Consumes("text/plain")
  @Produces("text/plain")
  public Response post(String query, @QueryParam("timelimit") @DefaultValue("500") int timelimit) {
    return handlePlainQuery(query, timelimit);
  }

  @GET
  @Produces("text/html")
  public Response get() {
    return Response.temporaryRedirect(URI.create("/static/gremlin")).build();
  }

  private Response handlePlainQuery(String query, int timeLimit) {
    if (Strings.isNullOrEmpty(query)) {
      return Response.ok("").build();
    } else {
      bindings.put("g", wrapper.getGraph().traversal());
      bindings.put("maria", wrapper.getGraph().traversal().V().has("tim_id", "37981a95-e527-40a8-9528-7d32c5c5f360"));
      try {
        final String result = evaluateQuery(query);// + ".timeLimit(" + timeLimit + ")");
        wrapper.getGraph().tx().commit();
        return Response.ok(result).build();
      } catch (ScriptException e) {
        LOG.error(e.getMessage(), e);
        return Response.status(500).entity(e.getMessage()).build();
      }
    }
  }

  private void dumpVertex(Vertex vertex, StringBuilder result) {
    result.append(String.format("Vertex [%s]:\n", vertex.id()));
    ArrayList<VertexProperty<Object>> properties = Lists.newArrayList(vertex.properties());
    properties.sort((o1, o2) -> java.text.Collator.getInstance().compare(o1.label(), o2.label()));
    for (VertexProperty<Object> property : properties) {
      String stringified;
      try {
        stringified = mapper.writeValueAsString(property.value());
      } catch (JsonProcessingException e) {
        stringified = String.format("%s", mapper);
      }
      result.append(String.format("  %s: %s\n", property.label(), stringified));
    }
    for (Edge edge : ImmutableList.copyOf(vertex.edges(IN))) {
      result.append(String.format("  <--[%s]-- v[%s]\n", edge.label(), edge.outVertex().id()));
    }
    for (Edge edge : ImmutableList.copyOf(vertex.edges(OUT))) {
      result.append(String.format("  --[%s]--> v[%s]\n", edge.label(), edge.inVertex().id()));
    }
  }

  private void dumpEdge(Edge edge, StringBuilder result) {
    result.append(String.format("v[%s] --[%s]--> v[%s]\n",
            edge.outVertex().id(), edge.label(), edge.inVertex().id()));
    ArrayList<Property<Object>> properties = Lists.newArrayList(edge.properties());
    properties.sort((o1, o2) -> java.text.Collator.getInstance().compare(o1.key(), o2.key()));
    for (Property<Object> property : properties) {
      String stringified;
      try {
        stringified = mapper.writeValueAsString(property.value());
      } catch (JsonProcessingException e) {
        stringified = String.format("%s", mapper);
      }
      result.append(String.format("  %s: %s\n", property.key(), stringified));
    }
  }

  private void dumpItem(Object item, StringBuilder result) {
    if (item instanceof Vertex) {
      dumpVertex((Vertex) item, result);
    } else if (item instanceof Edge) {
      dumpEdge((Edge) item, result);
    } else if (item instanceof BulkSet) {
      BulkSet bulkSet = (BulkSet<Object>) item;
      bulkSet.forEach((obj, amount) -> {
        StringBuilder stringBuilder = new StringBuilder();
        dumpItem(obj, stringBuilder);
        result.append(stringBuilder.toString() + "\n");
      });

    } else if (item instanceof LinkedHashMap) {
      Map<String, String> map = new HashMap<>();

      LinkedHashMap lhm = (LinkedHashMap<String, Object>) item;
      lhm.forEach((key, obj) -> {
        StringBuilder stringBuilder = new StringBuilder();
        dumpItem(obj, stringBuilder);
        map.put((String)key, stringBuilder.toString());
      });
      try {
        result.append(mapper.writeValueAsString(map));
      } catch (JsonProcessingException e) {
        LOG.error(e.getMessage(), e);
      }
    } else {
      result.append(item);
      result.append(item).append("\n");
    }
  }

  private String evaluateQuery(String query) throws ScriptException {
    GraphTraversal traversalResult = (GraphTraversal) engine.eval(query, bindings);
    StringBuilder result = new StringBuilder();
    while (traversalResult.hasNext()) {

      dumpItem(traversalResult.next(), result);
    }
    if (result.length() > 0) {
      return result.toString();
    } else {
      return "No results...";
    }
  }

}
