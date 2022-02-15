package nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints;

import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportManager;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.rml.RmlRdfCreator;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints.ErrorResponseHelper.handleImportManagerResult;

@Path("/v5/{userId}/{dataSetId}/rml")
public class Rml {
  private static final Logger LOG = LoggerFactory.getLogger(Rml.class);
  private final DataSetRepository dataSetRepository;
  private final ErrorResponseHelper errorResponseHelper;
  private final UserValidator userValidator;

  public Rml(DataSetRepository dataSetRepository, ErrorResponseHelper errorResponseHelper,
             UserValidator userValidator) {
    this.dataSetRepository = dataSetRepository;
    this.errorResponseHelper = errorResponseHelper;
    this.userValidator = userValidator;
  }

  @POST
  public Response upload(final String rdfData,
                         @PathParam("userId") final String ownerId,
                         @PathParam("dataSetId") final String dataSetId,
                         @HeaderParam("authorization") String authHeader)
    throws DataStoreCreationException, LogStorageFailedException, ExecutionException, InterruptedException {
    Optional<User> user;
    try {
      user = userValidator.getUserFromAccessToken(authHeader);
    } catch (UserValidationException e) {
      LOG.error("Exception validating user", e);
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    if (!user.isPresent()) {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
    final Optional<DataSet> dataSet = dataSetRepository.getDataSet(user.get(),ownerId, dataSetId);
    if (dataSet.isPresent()) {
      ImportManager importManager = dataSet.get().getImportManager();

      final String baseUri = dataSet.get().getMetadata().getBaseUri();
      final String graph = dataSet.get().getMetadata().getGraph();
      Future<ImportStatus> promise = importManager.generateLog(
        baseUri,
        graph,
        new RmlRdfCreator(baseUri, rdfData)
      );
      return handleImportManagerResult(promise);
    } else {
      return errorResponseHelper.dataSetNotFound(ownerId, dataSetId);
    }
  }

}
