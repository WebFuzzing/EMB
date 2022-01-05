package nl.knaw.huygens.timbuctoo.experimental.womenwriters;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.jersey.params.UUIDParam;
import nl.knaw.huygens.timbuctoo.crud.CrudServiceFactory;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.core.NotFoundException;
import nl.knaw.huygens.timbuctoo.core.TransactionEnforcer;
import nl.knaw.huygens.timbuctoo.core.TransactionStateAndResult;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

@Path("/v2.1/womenwritersdomain/{collection}/{id}")
@Produces(MediaType.APPLICATION_JSON)
public class WomenWritersEntityGet {

  private final CrudServiceFactory crudServiceFactory;
  private final TransactionEnforcer transactionEnforcer;

  public WomenWritersEntityGet(CrudServiceFactory crudServiceFactory, TransactionEnforcer transactionEnforcer) {
    this.crudServiceFactory = crudServiceFactory;
    this.transactionEnforcer = transactionEnforcer;
  }

  public static URI makeUrl(String collectionName, UUID id) {
    return UriBuilder.fromResource(WomenWritersEntityGet.class)
                     .buildFromMap(ImmutableMap.of(
                       "collection", collectionName,
                       "id", id
                     ));
  }

  @GET
  public Response get(@PathParam("collection") String collectionName, @PathParam("id") UUIDParam id,
                      @QueryParam("rev") Integer rev) {
    return transactionEnforcer.executeAndReturn(timbuctooActions -> {
      WomenWritersJsonCrudService womenWritersJsonCrudService = crudServiceFactory
        .newWomenWritersJsonCrudService(timbuctooActions);
      try {
        JsonNode result = womenWritersJsonCrudService.get(collectionName, id.get(), rev);
        return TransactionStateAndResult.commitAndReturn(Response.ok(result).build());
      } catch (InvalidCollectionException e) {
        return TransactionStateAndResult.rollbackAndReturn(
          Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn(e.getMessage()))).build());
      } catch (NotFoundException e) {
        return TransactionStateAndResult.rollbackAndReturn(
          Response.status(Response.Status.NOT_FOUND).entity(jsnO("message", jsn("not found"))).build());
      }
    });
  }
}
