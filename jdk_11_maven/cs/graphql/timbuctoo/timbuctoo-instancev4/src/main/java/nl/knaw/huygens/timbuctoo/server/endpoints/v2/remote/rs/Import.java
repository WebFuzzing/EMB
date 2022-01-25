package nl.knaw.huygens.timbuctoo.server.endpoints.v2.remote.rs;

import io.vavr.control.Either;
import nl.knaw.huygens.timbuctoo.remote.rs.download.ResourceSyncFileLoader;
import nl.knaw.huygens.timbuctoo.remote.rs.download.ResourceSyncImport;
import nl.knaw.huygens.timbuctoo.remote.rs.download.exceptions.CantRetrieveFileException;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints.auth.AuthCheck;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ResourceSyncMutationFileHelper;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.ResourceSyncReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/v2.1/remote/rs/import")
public class Import {

  public static final Logger LOG = LoggerFactory.getLogger(Import.class);
  private final ResourceSyncFileLoader resourceSyncFileLoader;
  private final AuthCheck authCheck;

  public Import(ResourceSyncFileLoader resourceSyncFileLoader, AuthCheck authCheck) {
    this.resourceSyncFileLoader = resourceSyncFileLoader;
    this.authCheck = authCheck;
  }

  @POST
  @Produces("application/json")
  public Response importData(@HeaderParam("Authorization") String authorization,
                             @QueryParam("forceCreation") boolean forceCreation,
                             @QueryParam("dataSetFile") String dataSetFile,
                             @QueryParam("async") @DefaultValue("true") final boolean async,
                             ImportData importData)
      throws DataStoreCreationException {

    final Either<Response, Response> responses = authCheck
        .getOrCreate(authorization, importData.userId, importData.dataSetId, forceCreation)
        .flatMap(userAndDs -> authCheck.allowedToImport(userAndDs.getLeft(), userAndDs.getRight()))
        .map(userAndDs -> {
          final DataSet dataSet = userAndDs.getRight();
          try {
            LOG.info("Loading files");

            ResourceSyncReport resourceSyncReport = new ResourceSyncReport();
            ResourceSyncMutationFileHelper fileHelper = new ResourceSyncMutationFileHelper(dataSet, resourceSyncReport);

            ResourceSyncImport resourceSyncImport = new ResourceSyncImport(resourceSyncFileLoader, async);
            resourceSyncImport.filterAndImport(
                importData.source.toString(), dataSetFile, null, null, fileHelper);

            return Response.ok(resourceSyncReport).build();
          } catch (CantRetrieveFileException e) {
            return Response.status(400).entity(e.getMessage()).build();
          } catch (Exception e) {
            LOG.error("Could not read files to import", e);
            return Response.serverError().entity(e).build();
          }
        });
    if (responses.isLeft()) {
      return responses.getLeft();
    } else {
      return responses.get();
    }
  }

  public static class ImportData {
    public URI source;
    public String userId;
    public String dataSetId;
  }
}
