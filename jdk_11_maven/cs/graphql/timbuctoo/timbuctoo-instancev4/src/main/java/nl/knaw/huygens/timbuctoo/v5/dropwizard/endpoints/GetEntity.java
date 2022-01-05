package nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints;

import com.google.common.collect.ImmutableMap;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

@Path("/v5/{ownerId}/{dataSetId}/{id}")
public class GetEntity {
  private final DataSetRepository dataSetRepository;
  private final UserValidator userValidator;

  public GetEntity(DataSetRepository dataSetRepository, UserValidator userValidator) {
    this.dataSetRepository = dataSetRepository;
    this.userValidator = userValidator;
  }

  private static String escapeCharacters(String value) {
    return value
      .replace("\\", "\\\\")
      .replace("\n", "\\n")
      .replace("\r", "\\r")
      .replace("\"", "\\\"");
  }

  public static URI makeUrl(String ownerId, String dataSetId, String id) throws UnsupportedEncodingException {
    return UriBuilder.fromResource(GetEntity.class)
      .buildFromMap(ImmutableMap.of(
        "ownerId", ownerId,
        "dataSetId", dataSetId,
        "id", escapeCharacters(URLEncoder.encode(id, "UTF-8"))
      ));
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response getEntity(@HeaderParam("authorization") String authHeader,
                            @PathParam("ownerId") String ownerId,
                            @PathParam("dataSetId") String dataSetId,
                            @PathParam("id") String id) {

    Optional<User> user;
    try {
      user = userValidator.getUserFromAccessToken(authHeader);
    } catch (UserValidationException e) {
      return Response.status(404).build();
    }

    Optional<DataSet> dataSet;

    if (user.isPresent()) {
      dataSet = dataSetRepository.getDataSet(user.get(), ownerId, dataSetId);
    } else {
      dataSet = dataSetRepository.getDataSet(null, ownerId, dataSetId);
    }


    if (!dataSet.isPresent()) {
      return Response.status(Response.Status.FORBIDDEN).build();
    }

    return streamToStreamingResponse(dataSet.get().getQuadStore().getQuads(id));

  }

  private Response streamToStreamingResponse(final Stream<CursorQuad> dataStream) {
    StreamingOutput streamingData = output -> {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
      try (Stream<CursorQuad> data = dataStream) {

        for (Iterator<CursorQuad> dataIt = data.iterator(); dataIt.hasNext(); ) {
          writer.write(dataIt.next().toString() + "\n");
        }
      }
      writer.flush();
    };

    return Response.ok(streamingData).build();
  }

}
