package nl.knaw.huygens.timbuctoo.server.endpoints.legacy;

import nl.knaw.huygens.timbuctoo.util.UriHelper;
import nl.knaw.huygens.timbuctoo.server.endpoints.v2.domain.Index;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * This class redirects from API endpoints that are no longer supported, but whose urls are still cached by google
 * or the handle server to their current counterparts.
 */
@Path("/domain/{collection}")
public class LegacyIndexRedirect {

  private final UriHelper uriHelper;

  public LegacyIndexRedirect(UriHelper uriHelper) {
    this.uriHelper = uriHelper;
  }

  @GET
  public Response index(@PathParam("collection") String collectionName) {
    return Response.status(301)
                   .location(uriHelper.fromResourceUri(Index.makeUrl(collectionName)))
                   .build();
  }
}
