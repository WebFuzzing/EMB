package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import nl.knaw.huygens.timbuctoo.remote.rs.download.ResourceSyncImport;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportManager;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.ResourceSyncReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ResourceSyncMutationFileHelper implements ResourceSyncImport.WithFile {
  private static final Logger LOG = LoggerFactory.getLogger(ResourceSyncMutationFileHelper.class);

  private final DataSet dataSet;
  private final ResourceSyncReport resourceSyncReport;

  public ResourceSyncMutationFileHelper(DataSet dataSet, ResourceSyncReport resourceSyncReport) {
    this.dataSet = dataSet;
    this.resourceSyncReport = resourceSyncReport;
  }

  @Override
  public Future<?> withFile(InputStream inputStream, String url, String mediaType, Date dateTime) throws Exception {
    MediaType parsedMediatype = MediaType.APPLICATION_OCTET_STREAM_TYPE;
    try {
      parsedMediatype = MediaType.valueOf(mediaType);
    } catch (IllegalArgumentException e) {
      LOG.error("Failed to get mediatype", e);
    }

    ImportManager importManager = dataSet.getImportManager();
    if (importManager.isRdfTypeSupported(parsedMediatype)) {
      resourceSyncReport.importedFiles.add(url);
      return importManager.addLog(
          dataSet.getMetadata().getBaseUri(), dataSet.getMetadata().getGraph(),
          url.substring(url.lastIndexOf('/') + 1),
          inputStream,
          Optional.of(StandardCharsets.UTF_8),
          parsedMediatype);
    }

    resourceSyncReport.ignoredFiles.add(url);
    importManager.addFile(inputStream, url, parsedMediatype);

    return CompletableFuture.completedFuture(null);
  }
}
