package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import com.google.common.collect.ImmutableMap;

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

import static nl.knaw.huygens.timbuctoo.server.endpoints.v2.system.vres.Metadata.redirectUrl;

@Path("/v2.1/metadata/{vre}")
@Produces(MediaType.APPLICATION_JSON)
public class Metadata {

  public static URI makeUrl(String vreName) {
    return UriBuilder.fromResource(Metadata.class)
      .buildFromMap(ImmutableMap.of(
        "vre", vreName
      ));
  }

  public Metadata() {
  }

  @GET
  public Response get(@PathParam("vre") String vreName,
                      @QueryParam("withCollectionInfo") @DefaultValue("false") boolean withCollectionInfo) {
    return Response.status(Response.Status.FOUND)
                   .location(redirectUrl(vreName, withCollectionInfo))
                   .build();
  }
}
