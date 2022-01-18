package nl.knaw.huygens.timbuctoo.server.endpoints.v2.domain;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.crud.CrudServiceFactory;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.crud.JsonCrudService;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.core.TransactionStateAndResult.commitAndReturn;
import static nl.knaw.huygens.timbuctoo.core.TransactionStateAndResult.rollbackAndReturn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

@Path("/v2.1/domain/{collection}")
@Produces(MediaType.APPLICATION_JSON)
public class Index {

  private final UserValidator userValidator;
  private final CrudServiceFactory crudServiceFactory;
  private final TransactionEnforcer transactionEnforcer;

  public Index(UserValidator userValidator, CrudServiceFactory crudServiceFactory,
               TransactionEnforcer transactionEnforcer) {
    this.userValidator = userValidator;
    this.crudServiceFactory = crudServiceFactory;
    this.transactionEnforcer = transactionEnforcer;
  }

  public static URI makeUrl(String collectionName) {
    return UriBuilder.fromResource(Index.class)
      .buildFromMap(ImmutableMap.of(
        "collection", collectionName
      ));
  }

  @POST
  public Response createNew(
    @PathParam("collection") String collectionName,
    @HeaderParam("Authorization") String authHeader,
    ObjectNode body
  ) throws URISyntaxException {
    Optional<User> user;
    try {
      user = userValidator.getUserFromAccessToken(authHeader);
    } catch (UserValidationException e) {
      user = Optional.empty();
    }

    Optional<User> newUser = user;

    if (!user.isPresent()) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } else {
      return transactionEnforcer.executeAndReturn(timbuctooActions -> {
        JsonCrudService crudService = crudServiceFactory.newJsonCrudService(timbuctooActions);
        try {
          UUID id = crudService.create(collectionName, body, newUser.get());
          return commitAndReturn(
            Response.created(SingleEntity.makeUrl(collectionName, id)).build()
          );
        } catch (InvalidCollectionException e) {
          return rollbackAndReturn(
            Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn(e.getMessage()))).build()
          );
        } catch (IOException e) {
          return rollbackAndReturn(
            Response.status(Response.Status.BAD_REQUEST).entity(jsnO("message", jsn(e.getMessage()))).build()
          );
        } catch (PermissionFetchingException e) {
          return rollbackAndReturn(
            Response.status(Response.Status.FORBIDDEN).entity(jsnO("message", jsn(e.getMessage()))).build()
          );
        }
      });

    }
  }

  @GET
  public Response list(@PathParam("collection") String collectionName,
                       @QueryParam("rows") @DefaultValue("200") int rows,
                       @QueryParam("start") @DefaultValue("0") int start,
                       @QueryParam("withRelations") @DefaultValue("false") boolean withRelations) {

    return transactionEnforcer.executeAndReturn(timbuctooActions -> {
      JsonCrudService crudService = crudServiceFactory.newJsonCrudService(timbuctooActions);
      try {
        List<ObjectNode> jsonNodes = crudService.getCollection(collectionName, rows, start, withRelations);
        return commitAndReturn(Response.ok(jsonNodes).build());
      } catch (InvalidCollectionException e) {
        return rollbackAndReturn(
          Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn(e.getMessage()))).build()
        );
      } catch (IllegalArgumentException e) {
        String message =
          String.format("Could not process parameters rows=%d start=%d", rows, start);
        return rollbackAndReturn(
          Response.status(Response.Status.BAD_REQUEST).entity(jsnO("message", jsn(message))).build()
        );
      }
    });
  }
}
