package nl.knaw.huygens.timbuctoo.server.endpoints.v2.remote.rs;


import com.codahale.metrics.annotation.Timed;
import nl.knaw.huygens.timbuctoo.remote.rs.ResourceSyncService;
import nl.knaw.huygens.timbuctoo.remote.rs.view.FrameworkBase;
import nl.knaw.huygens.timbuctoo.remote.rs.view.Interpreter;
import nl.knaw.huygens.timbuctoo.remote.rs.view.SetListBase;
import nl.knaw.huygens.timbuctoo.remote.rs.view.TreeBase;
import org.hibernate.validator.constraints.NotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URISyntaxException;

@Path("/v2.1/remote/rs/discover")
@Produces(MediaType.APPLICATION_JSON)
public class Discover {

  public static final Logger LOG = LoggerFactory.getLogger(Discover.class);
  private final ResourceSyncService resourceSyncService;

  public Discover(ResourceSyncService resourceSyncService) {
    this.resourceSyncService = resourceSyncService;
  }

  @GET
  @Path("/listsets/")
  @Timed
  public Response listSets(@HeaderParam("Authorization") String authorization, @QueryParam("url") @NotEmpty String url,
                           @QueryParam("debug") @DefaultValue("false") boolean debug) {
    try {
      SetListBase setListBase = resourceSyncService.listSets(url,
        new Interpreter()
          .withStackTrace(debug), authorization);
      return Response.ok(setListBase).build();
    } catch (URISyntaxException e) {
      String errorMessage = String.format("Url '%s' is not valid.", url);
      LOG.error(errorMessage, e);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
    } catch (InterruptedException e) {
      LOG.error("Cannot list sets of url '" + url + "'.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("/framework/")
  @Timed
  public Response getFramework(@QueryParam("url") @NotEmpty String url,
                               @QueryParam("debug") @DefaultValue("false") boolean debug) {
    try {
      FrameworkBase frameworkBase = resourceSyncService.getFramework(url,
        new Interpreter()
          .withStackTrace(debug));
      return Response.ok(frameworkBase).build();
    } catch (URISyntaxException e) {
      String errorMessage = String.format("Url '%s' is not valid.", url);
      LOG.error(errorMessage, e);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
    } catch (InterruptedException e) {
      LOG.error("Cannot list sets of url '" + url + "'.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GET
  @Path("/tree/")
  @Timed
  public Response getTree(@QueryParam("url") @NotEmpty String url,
                          @QueryParam("debug") @DefaultValue("false") boolean debug) {
    try {
      TreeBase treeBase = resourceSyncService.getTree(url,
        new Interpreter()
          .withStackTrace(debug));
      return Response.ok(treeBase).build();
    } catch (URISyntaxException e) {
      String errorMessage = String.format("Url '%s' is not valid.", url);
      LOG.error(errorMessage, e);
      return Response.status(Response.Status.BAD_REQUEST).entity(errorMessage).build();
    } catch (InterruptedException e) {
      LOG.error("Cannot list sets of url '" + url + "'.", e);
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }
}
