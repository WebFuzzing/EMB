package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import nl.knaw.huygens.timbuctoo.relationtypes.RelationTypeDescription;
import nl.knaw.huygens.timbuctoo.relationtypes.RelationTypeService;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.util.List;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v2.1/system/relationtypes")
@Produces(APPLICATION_JSON)
public class RelationTypes {


  private final RelationTypeService relationTypeService;

  public RelationTypes(GraphWrapper wrapper) {
    this.relationTypeService = new RelationTypeService(wrapper);
  }

  @GET
  public Response get(@QueryParam("iname") String name) {
    List<RelationTypeDescription> relationTypeDescriptions = relationTypeService.get(name);
    return Response.ok(relationTypeDescriptions).build();
  }
}
