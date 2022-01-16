package nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints;

import nl.knaw.huygens.timbuctoo.v5.datastores.rssource.RsDocumentBuilder;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

@Path(".well-known")
public class WellKnown {

  @Path("resourcesync")
  @GET
  public Response resourceSync() {
    // Permanent redirect
    return Response.seeOther(UriBuilder.fromResource(RsEndpoint.class)
                                       .path(RsDocumentBuilder.SOURCE_DESCRIPTION_PATH)
                                       .build()
    ).build();
  }
}
