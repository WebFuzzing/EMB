package nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints;

import io.vavr.control.Either;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportManager;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.rssource.ChangesQuadGenerator;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints.auth.AuthCheck;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.util.ByteArrayStreamInputStream;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints.ErrorResponseHelper.handleImportManagerResult;

@Path("/v5/{userId}/{dataSet}/upload/rdf")
public class RdfUpload {

  private final AuthCheck authCheck;


  public RdfUpload(AuthCheck authCheck) {
    this.authCheck = authCheck;
  }

  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @POST
  public Response upload(@FormDataParam("file") final InputStream rdfInputStream,
                         @FormDataParam("file") final FormDataBodyPart body,
                         @FormDataParam("fileMimeTypeOverride") final MediaType mimeTypeOverride,
                         @FormDataParam("encoding") final String encoding,
                         @FormDataParam("baseUri") final URI baseUri,
                         @FormDataParam("defaultGraph") final URI defaultGraph,
                         @HeaderParam("authorization") final String authHeader,
                         @PathParam("userId") final String userId,
                         @PathParam("dataSet") final String dataSetId,
                         @QueryParam("forceCreation") boolean forceCreation,
                         @QueryParam("async") final boolean async,
                         @QueryParam("replace") final boolean replaceData)
    throws ExecutionException, InterruptedException, LogStorageFailedException, DataStoreCreationException {

    final Either<Response, Response> result = authCheck
      .getOrCreate( authHeader, userId, dataSetId, forceCreation)
      .flatMap(userAndDs -> authCheck.allowedToImport(userAndDs.getLeft(), userAndDs.getRight()))
      .map((Tuple<User, DataSet> userDataSetTuple) -> {
        final MediaType mediaType = mimeTypeOverride == null ? body.getMediaType() : mimeTypeOverride;

        final DataSet dataSet = userDataSetTuple.getRight();

        ImportManager importManager = dataSet.getImportManager();

        if (mediaType == null || !importManager.isRdfTypeSupported(mediaType)) {
          return Response
            .status(Response.Status.BAD_REQUEST)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity("{\"error\": \"We do not support the mediatype '" + mediaType + "'. Make sure to add the correct " +
              "mediatype to the file parameter. In curl you'd use `-F \"file=@<filename>;type=<mediatype>\"`. In a " +
              "webbrowser you probably have no way of setting the correct mimetype. So you can use a special " +
              "parameter " +
              "to override it: `formData.append(\"fileMimeTypeOverride\", \"<mimetype>\");`\"}")
            .build();
        }

        if (StringUtils.isBlank(encoding)) {
          return Response.status(Response.Status.BAD_REQUEST)
                  .entity("Please provide an 'encoding' parameter")
                  .build();
        }

        if (StringUtils.isBlank(body.getContentDisposition().getFileName())) {
          return Response.status(400).entity("filename cannot be empty.").build();
        }



        Future<ImportStatus> promise = null;
        try {
          if (replaceData) {
            deleteCurrentData(dataSet, importManager);
          }
          promise = importManager.addLog(
            baseUri == null ? dataSet.getMetadata().getBaseUri() : baseUri.toString(),
            defaultGraph == null ? dataSet.getMetadata().getGraph() : defaultGraph.toString(),
            body.getContentDisposition().getFileName(),
            rdfInputStream,
            Optional.of(Charset.forName(encoding)),
            mediaType
          );
        } catch (LogStorageFailedException e) {
          return Response.serverError().build();
        }
        if (!async) {
          return handleImportManagerResult(promise);
        }
        return Response.accepted().build();
      });
    if (result.isLeft()) {
      return result.getLeft();
    } else {
      return result.get();
    }
  }

  private void deleteCurrentData(DataSet dataSet, ImportManager importManager) throws LogStorageFailedException {
    final String dataSetBaseUri = dataSet.getMetadata().getBaseUri();
    final String graph = dataSet.getMetadata().getGraph();
    final ChangesQuadGenerator nqUdGenerator = new ChangesQuadGenerator(graph);
    try (Stream<CursorQuad> quads = dataSet.getQuadStore().getAllQuads()) {
      Stream<byte[]> deleteCurrentData = quads.filter(quad -> quad.getDirection() == Direction.OUT)
                                              .map(quad -> {
                                                if (quad.getValuetype().isPresent()) {
                                                  final String valueType = quad.getValuetype().get();
                                                  if (quad.getLanguage().isPresent() &&
                                                      valueType.equals(RdfConstants.LANGSTRING)) {
                                                    return nqUdGenerator.delLanguageTaggedString(
                                                        quad.getSubject(),
                                                        quad.getPredicate(),
                                                        quad.getObject(),
                                                        quad.getLanguage().get(),
                                                        graph
                                                    );
                                                  } else {
                                                    return nqUdGenerator.delValue(
                                                        quad.getSubject(),
                                                        quad.getPredicate(),
                                                        quad.getObject(),
                                                        valueType,
                                                        graph
                                                    );
                                                  }
                                                } else {
                                                  return nqUdGenerator.delRelation(
                                                      quad.getSubject(),
                                                      quad.getPredicate(),
                                                      quad.getObject(),
                                                      graph
                                                  );
                                                }
                                              }).map(String::getBytes);

      final Future<ImportStatus> deletePromise = importManager.addLog(
          dataSetBaseUri,
          graph,
          "deleteCurrentData.nqud",
          new ByteArrayStreamInputStream(deleteCurrentData),
          Optional.of(StandardCharsets.UTF_8),
          new MediaType("application", "vnd.timbuctoo-rdf.nquads_unified_diff")
      );
      // Wait until the deletions are done, before starting to import the new data,
      // to make sure all new data is shown to the user
      deletePromise.get();
    } catch (InterruptedException | ExecutionException e) {
      LoggerFactory.getLogger(RdfUpload.class).error("data could not be deleted");
    }
  }
}
