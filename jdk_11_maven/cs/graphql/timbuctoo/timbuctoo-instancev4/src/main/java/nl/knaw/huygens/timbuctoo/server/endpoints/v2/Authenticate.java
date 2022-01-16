package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import nl.knaw.huygens.timbuctoo.security.exceptions.AuthenticationUnavailableException;
import nl.knaw.huygens.timbuctoo.security.exceptions.LocalLoginUnavailableException;
import nl.knaw.huygens.timbuctoo.security.LoggedInUsers;
import nl.knaw.huygens.timbuctoo.server.security.BasicAuthorizationHeaderParser;
import nl.knaw.huygens.timbuctoo.server.security.InvalidAuthorizationHeaderException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

@Path("/v2.1/authenticate")
@Produces(MediaType.APPLICATION_JSON)
public class Authenticate {

  public static final Logger LOG = LoggerFactory.getLogger(Authenticate.class);
  private final LoggedInUsers loggedInUsers;

  public Authenticate(LoggedInUsers loggedInUsers) {
    this.loggedInUsers = loggedInUsers;
  }

  @POST
  public Response authenticate(@HeaderParam(HttpHeaders.AUTHORIZATION) String encodedAuthString) {
    try {
      if (encodedAuthString == null) {
        return unauthorizedResponse();
      }
      BasicAuthorizationHeaderParser.Credentials credentials = BasicAuthorizationHeaderParser
        .parse(encodedAuthString);

      Optional<String> token = loggedInUsers.userTokenFor(credentials.getUsername(), credentials.getPassword());
      if (token.isPresent()) {
        return Response.noContent().header("X_AUTH_TOKEN", token.get()).build();
      } else {
        return unauthorizedResponse();
      }
    } catch (InvalidAuthorizationHeaderException e) {
      LOG.info(e.getMessage());
      return unauthorizedResponse();
    } catch (LocalLoginUnavailableException | AuthenticationUnavailableException e) {
      return Response.serverError().entity(e.getMessage()).build();
    }
  }

  private Response unauthorizedResponse() {
    return Response
      .status(Response.Status.UNAUTHORIZED)
      .header(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=\"timbuctoo\"")
      .entity(jsnO("message", jsn("unauthorized")))
      .build();
  }
}
