package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.google.common.collect.Lists;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.remote.rs.download.ResourceSyncFileLoader;
import nl.knaw.huygens.timbuctoo.remote.rs.download.ResourceSyncImport;
import nl.knaw.huygens.timbuctoo.remote.rs.download.exceptions.CantRetrieveFileException;
import nl.knaw.huygens.timbuctoo.remote.rs.download.exceptions.CantDetermineDataSetException;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportInfo;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataSetCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.IllegalDataSetNameException;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.ResourceSyncReport;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class ResourceSyncImportMutation extends Mutation {
  private static final Logger LOG = LoggerFactory.getLogger(ResourceSyncImportMutation.class);
  private final DataSetRepository dataSetRepository;
  private final ResourceSyncFileLoader resourceSyncFileLoader;

  public ResourceSyncImportMutation(Runnable schemaUpdater, DataSetRepository dataSetRepository,
                                    ResourceSyncFileLoader resourceSyncFileLoader) {
    super(schemaUpdater);
    this.dataSetRepository = dataSetRepository;
    this.resourceSyncFileLoader = resourceSyncFileLoader;
  }

  @Override
  public Object executeAction(DataFetchingEnvironment env) {
    User user = MutationHelpers.getUser(env);

    String dataSetName = env.getArgument("dataSetName");
    String capabilityListUri = env.getArgument("capabilityListUri");
    String userSpecifiedDataSet = env.getArgument("userSpecifiedDataSet");
    String authString = env.getArgument("authorization");

    try {
      ImportInfo importInfo = new ImportInfo(capabilityListUri, Date.from(Instant.now()));
      DataSet dataSet = dataSetRepository.createDataSet(user, dataSetName, Lists.newArrayList(importInfo));

      MutationHelpers.checkPermission(env, dataSet.getMetadata(), Permission.IMPORT_RESOURCESYNC);

      ResourceSyncReport resourceSyncReport = new ResourceSyncReport();
      ResourceSyncMutationFileHelper fileHelper = new ResourceSyncMutationFileHelper(dataSet, resourceSyncReport);

      ResourceSyncImport resourceSyncImport = new ResourceSyncImport(resourceSyncFileLoader, false);
      resourceSyncImport.filterAndImport(capabilityListUri, userSpecifiedDataSet, authString, null, fileHelper);

      return resourceSyncReport;
    } catch (DataStoreCreationException | IllegalDataSetNameException | IOException |
        CantRetrieveFileException | CantDetermineDataSetException | DataSetCreationException e) {
      LOG.error("Failed to do a resource sync import. ", e);
      throw new RuntimeException(e);
    }
  }
}
