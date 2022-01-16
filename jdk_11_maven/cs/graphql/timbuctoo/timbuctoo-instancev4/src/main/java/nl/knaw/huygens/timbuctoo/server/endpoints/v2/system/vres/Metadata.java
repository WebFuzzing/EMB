package nl.knaw.huygens.timbuctoo.server.endpoints.v2.system.vres;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.model.properties.JsonMetadata;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;

@Path("/v2.1/system/vres/{vre}/metadata")
@Produces(MediaType.APPLICATION_JSON)
public class Metadata {

  private final JsonMetadata jsonMetadata;

  public static URI makeUrl(String vreName) {
    return UriBuilder.fromResource(Metadata.class)
      .buildFromMap(ImmutableMap.of(
        "vre", vreName
      ));
  }

  public static URI redirectUrl(String vreName, boolean withCollectionInfo) {
    return UriBuilder.fromResource(Metadata.class)
                     .queryParam("withCollectionInfo", withCollectionInfo)
                     .buildFromMap(ImmutableMap.of(
                       "vre", vreName
                     ));
  }

  public Metadata(JsonMetadata jsonMetadata) {
    this.jsonMetadata = jsonMetadata;
  }

  @GET
  public Response get(@PathParam("vre") String vreName,
                      @QueryParam("withCollectionInfo") @DefaultValue("false") boolean withCollectionInfo) {
    ObjectNode result = jsonMetadata.getForVre(vreName, withCollectionInfo);
    return Response.ok(result).build();
  }
}
