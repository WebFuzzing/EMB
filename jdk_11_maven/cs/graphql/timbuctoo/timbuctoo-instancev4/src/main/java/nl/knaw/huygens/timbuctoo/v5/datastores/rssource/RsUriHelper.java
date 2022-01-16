package nl.knaw.huygens.timbuctoo.v5.datastores.rssource;

import nl.knaw.huygens.timbuctoo.remote.rs.xml.Capability;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.ResourceSyncConstants;
import nl.knaw.huygens.timbuctoo.util.UriHelper;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints.RsEndpoint;

import static javax.ws.rs.core.UriBuilder.fromResource;


public class RsUriHelper {

  private static final String RESOURCE_SYNC_PATH = fromResource(RsEndpoint.class).build().getPath();
  private static final String WELL_KNOWN_RESOURCESYNC = ".well-known/resourcesync";
  // We know that the FileSystemFileStorage implementation of FileStorage stores files in this location..
  private static final String FILES_DIR = "files";
  private final UriHelper uriHelper;

  RsUriHelper(UriHelper uriHelper) {
    this.uriHelper = uriHelper;
  }

  public String uriForWellKnownResourceSync() {
    return String.format("%s/%s", uriHelper.getBaseUri(), WELL_KNOWN_RESOURCESYNC);
  }

  public String uriForRsDocument(DataSetMetaData dataSetMetaData, Capability capability) {
    return uriForRsDocument(dataSetMetaData, capability.getFilename());
  }

  public String uriForRsDocument(DataSetMetaData dataSetMetaData, String filename) {
    return String.format("%s/%s/%s/%s/%s", uriHelper.getBaseUri(), RESOURCE_SYNC_PATH, dataSetMetaData.getOwnerId(),
      dataSetMetaData.getDataSetId(), filename);
  }

  public String uriForToken(DataSetMetaData dataSetMetaData, String token) {

    return String.format("%s/%s/%s/%s/%s/%s", uriHelper.getBaseUri(), RESOURCE_SYNC_PATH, dataSetMetaData.getOwnerId(),
      dataSetMetaData.getDataSetId(), FILES_DIR, token);
  }

  public String uriForRsDataSet(DataSetMetaData dataSetMetaData) {
    return String.format("%s/%s/%s/%s/%s", uriHelper.getBaseUri(), RESOURCE_SYNC_PATH,
      dataSetMetaData.getOwnerId(),
      dataSetMetaData.getDataSetId(), ResourceSyncConstants.DATASET_NAME);
  }

  public String uriForChanges(DataSetMetaData dataSetMetaData, String changeFileName) {
    return String.format("%s/%s/%s/%s/changes/%s", uriHelper.getBaseUri(), RESOURCE_SYNC_PATH,
      dataSetMetaData.getOwnerId(),
      dataSetMetaData.getDataSetId(), changeFileName);
  }

}
