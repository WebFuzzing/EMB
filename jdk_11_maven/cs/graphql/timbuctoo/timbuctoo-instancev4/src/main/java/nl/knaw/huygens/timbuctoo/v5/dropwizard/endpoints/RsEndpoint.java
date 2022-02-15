package nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints;

import nl.knaw.huygens.timbuctoo.remote.rs.xml.Urlset;
import nl.knaw.huygens.timbuctoo.v5.datastores.rssource.RsDocumentBuilder;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedFile;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;

@Path("v5/resourcesync")
public class RsEndpoint {

  private static final Logger LOG = LoggerFactory.getLogger(RsEndpoint.class);
  private final RsDocumentBuilder rsDocumentBuilder;
  private final UserValidator userValidator;

  public RsEndpoint(RsDocumentBuilder rsDocumentBuilder, UserValidator userValidator) {
    this.rsDocumentBuilder = rsDocumentBuilder;
    this.userValidator = userValidator;
  }

  @GET
  @Path(RsDocumentBuilder.SOURCE_DESCRIPTION_PATH)
  @Produces(MediaType.APPLICATION_XML)
  public Response getSourceDescription(@HeaderParam("authorization") String authHeader) {
    return Response.ok(rsDocumentBuilder.getSourceDescription(getUser(authHeader))).build();
  }

  @GET
  @Path("{ownerId}/{dataSetName}/capabilitylist.xml")
  @Produces(MediaType.APPLICATION_XML)
  public Response getCapabilityList(@HeaderParam("authorization") String authHeader,
                                    @PathParam("ownerId") String owner,
                                    @PathParam("dataSetName") String dataSetName) {
    User user = getUser(authHeader);
    Optional<Urlset> maybeCapabilityList = rsDocumentBuilder.getCapabilityList(user, owner, dataSetName);
    if (maybeCapabilityList.isPresent()) {
      return Response.ok(maybeCapabilityList.get()).build();
    } else if (user != null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @GET
  @Path("{ownerId}/{dataSetName}/resourcelist.xml")
  @Produces(MediaType.APPLICATION_XML)
  public Response getResourceList(@HeaderParam("authorization") String authHeader,
                                  @PathParam("ownerId") String owner,
                                  @PathParam("dataSetName") String dataSetName) throws IOException {
    User user = getUser(authHeader);
    Optional<Urlset> maybeResourceList = rsDocumentBuilder.getResourceList(user, owner, dataSetName);
    if (maybeResourceList.isPresent()) {
      return Response.ok(maybeResourceList.get()).build();
    } else if (user != null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @GET
  @Path("{ownerId}/{dataSetName}/changelist.xml")
  @Produces(MediaType.APPLICATION_XML)
  public Response getChangeList(@HeaderParam("authorization") String authHeader,
                                @PathParam("ownerId") String owner,
                                @PathParam("dataSetName") String dataSetName) throws IOException {
    User user = getUser(authHeader);
    Optional<Urlset> maybeChangeList = rsDocumentBuilder.getChangeList(user, owner, dataSetName);
    if (maybeChangeList.isPresent()) {
      return Response.ok(maybeChangeList.get()).build();
    } else if (user != null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @GET
  @Path("{ownerId}/{dataSetName}/changes/{fileId}")
  @Produces(MediaType.TEXT_PLAIN)
  public Response getChanges(@HeaderParam("authorization") String authHeader,
                             @PathParam("ownerId") String owner,
                             @PathParam("dataSetName") String dataSetName,
                             @PathParam("fileId") String fileId) throws IOException {
    User user = getUser(authHeader);
    Optional<Stream<String>> changesStream = rsDocumentBuilder.getChanges(user, owner, dataSetName, fileId);

    if (changesStream.isPresent()) {
      return streamToStreamingResponse(changesStream.get());
    }

    return Response.status(Response.Status.NOT_FOUND).build();
  }

  @GET
  @Path("{ownerId}/{dataSetName}/dataset.nq")
  public Response getDataSet(@HeaderParam("authorization") String authHeader,
                             @PathParam("ownerId") String owner,
                             @PathParam("dataSetName") String dataSetName) {
    User user = getUser(authHeader);
    Optional<Stream<String>> resourceStream = rsDocumentBuilder.getResourceData(user, owner, dataSetName);

    if (resourceStream.isPresent()) {
      return streamToStreamingResponse(resourceStream.get());
    }

    return Response.status(Response.Status.NOT_FOUND).build();
  }

  private Response streamToStreamingResponse(final Stream<String> dataStream) {
    StreamingOutput streamingData = output -> {
      BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
      try (Stream<String> data = dataStream) {

        for (Iterator<String> dataIt = data.iterator(); dataIt.hasNext(); ) {
          writer.write(dataIt.next());
        }
      }
      writer.flush();
    };

    return Response.ok(streamingData).build();
  }


  @GET
  @Path("{ownerId}/{dataSetName}/files/{fileId}")
  public Response getFile(@HeaderParam("authorization") String authHeader,
                          @PathParam("ownerId") String owner,
                          @PathParam("dataSetName") String dataSetName,
                          @PathParam("fileId") String fileId
  ) throws IOException {
    User user = getUser(authHeader);
    Optional<CachedFile> maybeFile = rsDocumentBuilder.getCachedFile(user, owner, dataSetName, fileId);
    if (maybeFile.isPresent()) {
      CachedFile cachedFile = maybeFile.get();
      File file = cachedFile.getFile();
      if (file != null && file.exists()) {
        return Response.ok(cachedFile.getFile(), cachedFile.getMimeType()).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    } else if (user != null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  @GET
  @Path("{ownerId}/{dataSetName}/description.xml")
  public Response getDescription(@HeaderParam("authorization") String authHeader,
                                 @PathParam("ownerId") String owner,
                                 @PathParam("dataSetName") String dataSetName) {
    User user = getUser(authHeader);
    Optional<File> maybeFile = rsDocumentBuilder.getDataSetDescription(user, owner, dataSetName);
    if (maybeFile.isPresent()) {
      if (maybeFile.get().exists()) {
        return Response.ok(maybeFile.get(), MediaType.APPLICATION_XML_TYPE).build();
      } else {
        return Response.status(Response.Status.NOT_FOUND).build();
      }
    } else if (user != null) {
      return Response.status(Response.Status.FORBIDDEN).build();
    } else {
      return Response.status(Response.Status.UNAUTHORIZED).build();
    }
  }

  private User getUser(String authHeader) {
    User user = null;
    try {
      user = userValidator.getUserFromAccessToken(authHeader).orElse(null);
    } catch (UserValidationException e) {
      LOG.error("Exception validating user", e);
    }
    return user;
  }

}
