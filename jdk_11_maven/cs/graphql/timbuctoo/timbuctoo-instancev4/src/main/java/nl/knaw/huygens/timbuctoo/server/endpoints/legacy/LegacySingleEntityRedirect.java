package nl.knaw.huygens.timbuctoo.server.endpoints.legacy;

import io.dropwizard.jersey.params.UUIDParam;
import nl.knaw.huygens.timbuctoo.util.UriHelper;
import nl.knaw.huygens.timbuctoo.server.endpoints.v2.domain.SingleEntity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * This class redirects from API endpoints that are no longer supported, but whose urls are still cached by google
 * or the handle server to their current counterparts.
 */
@Path("/domain/{collection}/{id}")
public class LegacySingleEntityRedirect {

  private final UriHelper uriHelper;

  public LegacySingleEntityRedirect(UriHelper uriHelper) {
    this.uriHelper = uriHelper;
  }

  @GET
  public Response singleEntity(@PathParam("collection") String collectionName, @PathParam("id") UUIDParam id,
                               @QueryParam("rev") Integer rev) {
    return Response.status(301)
                   .location(uriHelper.fromResourceUri(SingleEntity.makeUrl(collectionName, id.get(), rev)))
                   .build();
  }
}
