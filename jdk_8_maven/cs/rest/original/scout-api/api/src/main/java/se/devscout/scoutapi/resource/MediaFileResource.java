package se.devscout.scoutapi.resource;

import com.codahale.metrics.annotation.Timed;
import com.drew.imaging.ImageProcessingException;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;
import io.dropwizard.auth.Auth;
import io.dropwizard.hibernate.UnitOfWork;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.media.multipart.BodyPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.Permission;
import se.devscout.scoutapi.dao.MediaFileDao;
import se.devscout.scoutapi.model.MediaFile;
import se.devscout.scoutapi.util.MediaFileUtils;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@Path("/v1/media_files")
@Produces(MediaType.APPLICATION_JSON)
@Api(tags = {"media files"})
public class MediaFileResource extends AbstractResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(MediaFileResource.class);
    private final MediaFileDao dao;
    private final File mediaFilesFolder;
    private final Map<String, String> imageMimeTypes;

    public MediaFileResource(MediaFileDao dao, File mediaFilesFolder) throws IOException {
        this.dao = dao;
        this.mediaFilesFolder = mediaFilesFolder;

        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/mime-types.properties"));
        imageMimeTypes = Maps.fromProperties(properties);
    }

    @GET
    @Timed
    @UnitOfWork
    @ApiOperation(value = "List all media files referenced in activities.")
    public Response all(
            @ApiParam(value = "" +
                    "Filter media files based on their path. " +
                    "The parameter value may be found anywhere in the URI." +
                    "Case sensitive.")//,
                    //example = "Forband")
            @QueryParam("uri") String uri,

            @ApiParam(value = API_DOCS_ATTRS_DESCR)
            @QueryParam("attrs") String attrs) {
        return okResponse(Strings.isNullOrEmpty(uri) ? dao.all() : dao.find(uri), attrs);
    }

    @DELETE
    @Timed
    @Path("{id}")
    @UnitOfWork
    @ApiOperation(value = "Delete a media file. By default, deletes even if referenced by activities.")
    public void delete(@Auth @ApiParam(hidden = true) AuthResult authResult,
                       @Context HttpServletResponse response,
                       @PathParam("id") long id,
                       @ApiParam(
                               value = "Verify that media file is not referenced by any activities before deleting it.",
                               defaultValue = "false"//,
                               //example = "true"
                       )
                       @QueryParam("verify_unused") boolean verifyUnused) {
        doAuth(authResult, response, Permission.mediaitem_edit);
        if (verifyUnused && dao.isUsed(id)) {
            throw new WebApplicationException("Will not delete because media file is in use", Response.Status.CONFLICT);
        }
        dao.delete(dao.read(id));
    }

    @POST
    @Timed
    @UnitOfWork
    @Consumes(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Add a media file to the system. Specify URL of media file or use 'data URI' to upload base64-encoded file.")
    public MediaFile create(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response,
                            MediaFile mediaFile) {
        doAuth(authResult, response, Permission.mediaitem_create);

        try {
            URI uri = new URI(mediaFile.getUri());
            if ("data".equals(uri.getScheme())) {
                String relativeLocalPath = saveDataUriAsFile(uri);

                File localFile = new File(mediaFilesFolder, relativeLocalPath);

                try {
                    MediaFileUtils.initMediaFileMetaData(
                            mediaFile,
                            new FileInputStream(localFile),
                            localFile.getName());
                } catch (ImageProcessingException e) {
                    LOGGER.info("Could not read image metadata from uploaded file. Perhaps it was not an image at all?", e);
                } catch (IOException e) {
                    LOGGER.info("Could not read uploaded file. Perhaps it was not an image?", e);
                }

                mediaFile.setUri(getLocalFileURL(relativeLocalPath));
            }
        } catch (URISyntaxException e) {
            throw new WebApplicationException(e);
        } catch (StringIndexOutOfBoundsException e) {
            throw new WebApplicationException(e);
        } catch (MalformedURLException e) {
            throw new WebApplicationException(e);
        } catch (IOException e) {
            throw new WebApplicationException(e);
        }
        return dao.create(mediaFile);
    }


    /*
        WARNING: this is a bug/limitation in Swagger, as it does now allow
        same endpoint with different @Consumes, and it silently reports just one :(
     */
//    @POST
//    @Timed
//    @UnitOfWork
//    @Consumes(MediaType.MULTIPART_FORM_DATA)
//    @ApiOperation(value = "Upload media files (presumably images) to the system. Media files can then be associated with activities. Keywords and copyright are automatically extracted from EXIF and IPTC metadata.")
    public List<MediaFile> createFromMultiPart(@Auth @ApiParam(hidden = true) AuthResult authResult,
                                               MultiPart multiPart,
                                               @Context HttpServletResponse response) {
        doAuth(authResult, response, Permission.mediaitem_create);

        ArrayList<MediaFile> mediaFiles = new ArrayList<>();
        multiPart.getBodyParts().forEach(bodyPart -> {
            try {
                MediaFile mediaFile = new MediaFile();

                if ("image".equals(bodyPart.getMediaType().getType())) {
                    MediaFileUtils.initMediaFileMetaData(
                            mediaFile,
                            bodyPart.getEntityAs(InputStream.class),
                            bodyPart.getContentDisposition().getFileName());
                }

                // Metadata has been read, meaning that the upload file is actually an image!
                String relativeLocalPath = saveBodyPartAsFile(bodyPart);

                // Uploaded file has been properly saved. Now we can add it to the database.

                mediaFile.setMimeType(bodyPart.getMediaType().toString());
                mediaFile.setUri(getLocalFileURL(relativeLocalPath));

                MediaFile mf = dao.create(mediaFile);

                mediaFiles.add(mf);
                LOGGER.info("Added new media file: " + mf.getName());
            } catch (IOException e) {
                LOGGER.warn("Could not save file", e);
            } catch (ImageProcessingException e) {
                LOGGER.warn("Could not save file", e);
            }
        });
        return mediaFiles;
    }

    private String saveBodyPartAsFile(BodyPart bodyPart) throws IOException {
        String relativeLocalPath = getLocalFileName(bodyPart.getMediaType().toString());
        InputStream stream = bodyPart.getEntityAs(InputStream.class);
        Files.copy(stream, new File(mediaFilesFolder, relativeLocalPath).toPath());
        stream.close();
        return relativeLocalPath;
    }

    protected String getLocalFileURL(String relativeLocalPath) throws MalformedURLException {
        return new URL("file", "localhost", "/" + relativeLocalPath).toExternalForm();
    }

    private String saveDataUriAsFile(URI uri) throws IOException {
        String s = uri.getSchemeSpecificPart();
        String data = s.substring(s.indexOf(',') + 1);
        String metaData = s.substring(0, s.indexOf(','));
//                String charset = StringUtils.defaultString(StringUtils.substringBetween(metaData, ";charset=", ","), "US-ASCII");
        String mimeType = StringUtils.defaultString(StringUtils.substringBefore(metaData, ";"), "text/plain");
        boolean isBase64Encoded = metaData.contains(";base64");
        String relativeLocalPath = getLocalFileName(mimeType);
        File localFile = new File(mediaFilesFolder, relativeLocalPath);
        if (isBase64Encoded) {
            saveBase64EncodedFile(data, localFile);
        } else {
            saveTextFile(data, localFile);
        }
        return relativeLocalPath;
    }

    private String getLocalFileName(String mimeType) {
        String extension = imageMimeTypes.containsKey(mimeType) ? imageMimeTypes.get(mimeType) : "unknown-type";

        return String.format("%s.%s.%s", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()), UUID.randomUUID().toString(), extension);
    }

    private void saveTextFile(String data, File localFile) throws IOException {
        Files.write(localFile.toPath(), Collections.singletonList(data));
    }

    private void saveBase64EncodedFile(String data, File localFile) throws IOException {
        byte[] bytes = Base64.getDecoder().decode(data);
        java.nio.file.Path path = Files.write(localFile.toPath(), bytes);
        LOGGER.info("Saved uploaded file as " + path.toFile().getAbsolutePath());
    }

    @GET
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public Response get(@PathParam("id") long id,

                        @ApiParam(value = API_DOCS_ATTRS_DESCR)
                        @QueryParam("attrs") String attrs) {
        return okResponse(dao.read(id), attrs);
    }

    @GET
    @Timed
    @Path("{id}/file")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    @UnitOfWork
    @ApiOperation(value = "Download media file. Can resize images (but images will never be enlarged).")
    public Response downloadFile(@PathParam("id") long id,

                                 @ApiParam(value = "" +
                                         "The maximum width/height of returned images. " +
                                         "The specified value will be rounded up to the " +
                                         "next 'power of 2', e.g. 256, 512, 1024 and so on.")
                                 @QueryParam("size") int size) {
        MediaFile mediaFile = dao.read(id);
        try {
            URI sourceURI = new URI(mediaFile.getUri());
            File localFile;
            if ("file".equals(sourceURI.getScheme())) {
                localFile = new File(mediaFilesFolder, sourceURI.getPath());
            } else {
                localFile = new File(mediaFilesFolder, mediaFile.getId() + "_" + StringUtils.right(mediaFile.getUri().replaceAll("[^a-zA-Z0-9._-]", ""), 50));
                // Download file if not already downloaded
                if (!localFile.isFile()) {
                    Files.write(localFile.toPath(), Resources.toByteArray(sourceURI.toURL()));
                }
            }
            if (imageMimeTypes.containsKey(mediaFile.getMimeType()) && size > 0) {
                String formatName = imageMimeTypes.get(mediaFile.getMimeType());
                localFile = MediaFileUtils.getSuitableImage(localFile, size, formatName);
            }
            byte[] bytes = Files.readAllBytes(localFile.toPath());
            return Response.ok(bytes, StringUtils.defaultString(mediaFile.getMimeType(), MediaType.APPLICATION_OCTET_STREAM)).header(HttpHeaders.CONTENT_LENGTH, bytes.length).build();
        } catch (IOException e) {
            LOGGER.warn("Could not read file " + mediaFile.getUri(), e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        } catch (URISyntaxException e) {
            LOGGER.warn("URI syntax error when parsing path stored in database.", e);
            throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @PUT
    @Timed
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @UnitOfWork
    public MediaFile update(@Auth @ApiParam(hidden = true) AuthResult authResult, @Context HttpServletResponse response, @PathParam("id") long id, MediaFile updatedMediaFile) {
        doAuth(authResult, response, Permission.mediaitem_edit);
        MediaFile persisted = dao.read(id);

        persisted.setName(updatedMediaFile.getName());
        persisted.setMimeType(updatedMediaFile.getMimeType());
        persisted.setUri(updatedMediaFile.getUri());

        dao.update(persisted);
        return persisted;
    }
}
