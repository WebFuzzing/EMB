package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import io.dropwizard.jersey.params.UUIDParam;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.graph.D3Graph;
import nl.knaw.huygens.timbuctoo.graph.D3GraphGeneratorService;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

@Path("/v2.1/graph/{collection}s/{id}")
@Produces(APPLICATION_JSON)
public class Graph {
  public static final Logger LOG = LoggerFactory.getLogger(Graph.class);
  private final D3GraphGeneratorService d3GraphGeneratorService;


  public Graph(GraphWrapper wrapper, Vres mappings) {
    this.d3GraphGeneratorService = new D3GraphGeneratorService(wrapper, mappings);
  }

  @GET
  public Response get(@PathParam("collection") String collectionName, @PathParam("id") UUIDParam id,
                      @QueryParam("depth") int depth, @QueryParam("types") List<String> relationNames) {

    try {
      D3Graph result = d3GraphGeneratorService.get(collectionName, id.get(), relationNames, depth);
      return Response.ok(result).build();
    } catch (NotFoundException e) {
      return Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn("not found"))).build();
    }
  }


}
