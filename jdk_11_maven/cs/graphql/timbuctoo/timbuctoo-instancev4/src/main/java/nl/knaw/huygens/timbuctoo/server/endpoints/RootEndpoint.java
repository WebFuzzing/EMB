package nl.knaw.huygens.timbuctoo.server.endpoints;

import nl.knaw.huygens.timbuctoo.util.UriHelper;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.Optional;

@Path("/")
public class RootEndpoint {

  private final UriHelper uriHelper;
  private final Optional<URI> userRedirectUrl;

  public RootEndpoint(UriHelper uriHelper, Optional<URI> userRedirectUrl) {
    this.uriHelper = uriHelper;
    this.userRedirectUrl = userRedirectUrl;
  }

  @GET
  @Produces(MediaType.TEXT_HTML)
  public Response getHomepage() {
    URI url = userRedirectUrl
      .orElseGet(() -> uriHelper.fromResourceUri(URI.create("/static/upload/")));
    return Response.temporaryRedirect(url).build();
  }
}
