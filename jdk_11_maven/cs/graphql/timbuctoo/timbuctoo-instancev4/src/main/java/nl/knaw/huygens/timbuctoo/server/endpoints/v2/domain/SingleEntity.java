package nl.knaw.huygens.timbuctoo.server.endpoints.v2.domain;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.jersey.params.UUIDParam;
import nl.knaw.huygens.timbuctoo.core.AlreadyUpdatedException;
import nl.knaw.huygens.timbuctoo.crud.CrudServiceFactory;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.crud.JsonCrudService;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.PermissionFetchingException;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.core.TransactionStateAndResult.commitAndReturn;
import static nl.knaw.huygens.timbuctoo.core.TransactionStateAndResult.rollbackAndReturn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

@Path("/v2.1/domain/{collection}/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class SingleEntity {

  private final UserValidator userValidator;
  private final CrudServiceFactory crudServiceFactory;
  private final TransactionEnforcer transactionEnforcer;

  public SingleEntity(UserValidator userValidator, CrudServiceFactory crudServiceFactory,
                      TransactionEnforcer transactionEnforcer) {
    this.userValidator = userValidator;
    this.crudServiceFactory = crudServiceFactory;
    this.transactionEnforcer = transactionEnforcer;
  }

  public static URI makeUrl(String collectionName, UUID id) {
    return UriBuilder.fromResource(SingleEntity.class)
                     .buildFromMap(ImmutableMap.of(
                       "collection", collectionName,
                       "id", id
                     ));
  }

  public static URI makeUrl(String collectionName, UUID id, Integer rev) {
    if (rev == null) {
      return makeUrl(collectionName, id);
    } else {
      return UriBuilder.fromResource(SingleEntity.class)
                       .queryParam("rev", rev)
                       .buildFromMap(ImmutableMap.of(
                         "collection", collectionName,
                         "id", id
                       ));
    }
  }

  @GET
  public Response get(@PathParam("collection") String collectionName, @PathParam("id") UUIDParam id,
                      @QueryParam("rev") Integer rev) {

    return transactionEnforcer.executeAndReturn(timbuctooActions -> {
      JsonCrudService crudService = crudServiceFactory.newJsonCrudService(timbuctooActions);
      try {
        JsonNode result = crudService.get(collectionName, id.get(), rev);
        return commitAndReturn(Response.ok(result).build());
      } catch (InvalidCollectionException e) {
        return rollbackAndReturn(
          Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn(e.getMessage()))).build());
      } catch (NotFoundException e) {
        return rollbackAndReturn(
          Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn("not found"))).build());
      }
    });
  }

  @PUT
  public Response put(@PathParam("collection") String collectionName, @HeaderParam("Authorization") String authHeader,
                      @PathParam("id") UUIDParam id, ObjectNode body) {
    Optional<User> user;
    try {
      user = userValidator.getUserFromAccessToken(authHeader);
    } catch (UserValidationException e) {
      user = Optional.empty();
    }

    Optional<User> newUser = user;

    if (!newUser.isPresent()) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } else {
      UpdateMessage updateMessage = transactionEnforcer.executeAndReturn(timbuctooActions -> {
        JsonCrudService crudService = crudServiceFactory.newJsonCrudService(timbuctooActions);
        try {
          crudService.replace(collectionName, id.get(), body, newUser.get());
          return commitAndReturn(UpdateMessage.success());
        } catch (InvalidCollectionException e) {
          return rollbackAndReturn(
            UpdateMessage.failure(e.getMessage(), Response.Status.NOT_FOUND)
          );
        } catch (NotFoundException e) {
          return rollbackAndReturn(
            UpdateMessage.failure("not found", Response.Status.NOT_FOUND)
          );
        } catch (IOException e) {
          return rollbackAndReturn(
            UpdateMessage.failure(e.getMessage(), Response.Status.BAD_REQUEST)
          );
        } catch (AlreadyUpdatedException e) {
          return rollbackAndReturn(
            UpdateMessage.failure("Entry was already updated", Response.Status.EXPECTATION_FAILED)
          );
        } catch (PermissionFetchingException e) {
          return rollbackAndReturn(
            UpdateMessage.failure(e.getMessage(), Response.Status.FORBIDDEN)
          );
        }
      });

      // we perform the get in a separate transaction so that the client receives data that is guaranteed to be
      // committed in the database
      if (updateMessage.isSuccess()) {
        return transactionEnforcer.executeAndReturn(timbuctooActions -> {
          JsonCrudService crudService = crudServiceFactory.newJsonCrudService(timbuctooActions);
          try {
            JsonNode jsonNode = crudService.get(collectionName, id.get());
            return commitAndReturn(Response.ok(jsonNode).build());
          } catch (InvalidCollectionException e) {
            return rollbackAndReturn(
              Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn(
                "Collection '" + collectionName + "' was available a moment ago, but not anymore: " + e.getMessage()
              ))).build()
            );
          } catch (NotFoundException e) {
            return rollbackAndReturn(
              Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn("not found"))).build()
            );
          }
        });
      } else {
        return Response.status(updateMessage.getResponseStatus())
                       .entity(jsnO("message", jsn(updateMessage.getException().get())))
                       .build();
      }

    }
  }

  @DELETE
  public Response delete(@PathParam("collection") String collectionName,
                         @HeaderParam("Authorization") String authHeader,
                         @PathParam("id") UUIDParam id) {
    Optional<User> user;
    try {
      user = userValidator.getUserFromAccessToken(authHeader);
    } catch (UserValidationException e) {
      user = Optional.empty();
    }

    Optional<User> newUser = user;
    if (!newUser.isPresent()) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    } else {

      return transactionEnforcer.executeAndReturn(timbuctooActions -> {
        JsonCrudService jsonCrudService = crudServiceFactory.newJsonCrudService(timbuctooActions);
        try {
          jsonCrudService.delete(collectionName, id.get(), newUser.get());
          return commitAndReturn(Response.noContent().build());
        } catch (InvalidCollectionException e) {
          return rollbackAndReturn(
            Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn(e.getMessage()))).build()
          );
        } catch (NotFoundException e) {
          return rollbackAndReturn(
            Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn("not found"))).build()
          );
        } catch (PermissionFetchingException e) {
          return rollbackAndReturn(
            Response.status(Response.Status.FORBIDDEN).entity(jsnO("message", jsn(e.getMessage()))).build()
          );
        } catch (IOException e) {
          return rollbackAndReturn(
            Response.status(Response.Status.BAD_REQUEST).entity(jsnO("message", jsn(e.getMessage()))).build()
          );
        }
      });
    }
  }

  private static class UpdateMessage {
    private final boolean success;
    private final String exception;
    private final Response.Status responseStatus;

    private UpdateMessage(boolean success, String exceptionMessage, Response.Status responseStatus) {
      this.success = success;
      this.exception = exceptionMessage;
      this.responseStatus = responseStatus;
    }

    public static UpdateMessage success() {
      return new UpdateMessage(true, null, Response.Status.OK);
    }

    public static UpdateMessage failure(String exception, Response.Status responseStatus) {
      return new UpdateMessage(false, exception, responseStatus);
    }

    public boolean isSuccess() {
      return success;
    }

    public Response.Status getResponseStatus() {
      return responseStatus;
    }

    public Optional<String> getException() {
      return Optional.ofNullable(exception);
    }

  }

}
