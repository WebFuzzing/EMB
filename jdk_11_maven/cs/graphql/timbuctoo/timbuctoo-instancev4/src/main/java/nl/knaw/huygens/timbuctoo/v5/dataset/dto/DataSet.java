package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbEnvironmentCreator;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.BdbDbCreationException;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.IsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.dataset.ChangesRetriever;
import nl.knaw.huygens.timbuctoo.v5.dataset.CurrentStateRetriever;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportManager;
import nl.knaw.huygens.timbuctoo.v5.dataset.ReadOnlyChecker;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.datastorage.DataSetStorage;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.RdfDescriptionSaver;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbBackedData;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbRmlDataSourceStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbSchemaStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTripleStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTruePatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.StoreUpdater;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.UpdatedPerPatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.VersionStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.SchemaStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.ExplicitField;
import nl.knaw.huygens.timbuctoo.v5.filestorage.FileStorage;
import nl.knaw.huygens.timbuctoo.v5.graphql.customschema.SchemaHelper;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CustomProvenance;
import nl.knaw.huygens.timbuctoo.v5.rml.RdfDataSourceFactory;
import org.immutables.value.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Value.Immutable
public abstract class DataSet {
  private static final Logger LOG = LoggerFactory.getLogger(DataSet.class);


  public static DataSet dataSet(DataSetMetaData metadata, ExecutorService executorService,
                                String rdfPrefix, BdbEnvironmentCreator dataStoreFactory,
                                Runnable onUpdated, DataSetStorage dataSetStorage, ReadOnlyChecker readOnlyChecker)
    throws IOException, DataStoreCreationException {

    String userId = metadata.getOwnerId();
    String dataSetId = metadata.getDataSetId();
    File descriptionFile = dataSetStorage.getResourceSyncDescriptionFile();
    FileStorage fileStorage = dataSetStorage.getFileStorage();

    ImportManager importManager = new ImportManager(
      dataSetStorage.getLogList(),
      fileStorage,
      fileStorage,
      dataSetStorage.getLogStorage(),
      executorService,
      dataSetStorage.getRdfIo(),
      onUpdated
    );

    try {
      importManager.subscribeToRdf(new RdfDescriptionSaver(descriptionFile, metadata.getBaseUri(),
        importManager.getImportStatus()));
    } catch (ParserConfigurationException | SAXException e) {
      LOG.error("Could not construct import manager of data set", e);
    }

    final TupleBinding<String> stringBinding = TupleBinding.getPrimitiveBinding(String.class);
    try {
      StringStringIsCleanHandler stringStringIsCleanHandler = new StringStringIsCleanHandler();
      BdbTripleStore quadStore = new BdbTripleStore(dataStoreFactory.getDatabase(
        userId,
        dataSetId,
        "rdfData",
        true,
        stringBinding,
        stringBinding,
        stringStringIsCleanHandler
      ));
      final BdbTypeNameStore typeNameStore = new BdbTypeNameStore(
        new BdbBackedData(dataStoreFactory.getDatabase(
          userId,
          dataSetId,
          "typenames",
          false,
          stringBinding,
          stringBinding,
          stringStringIsCleanHandler
        )),
        rdfPrefix
      );
      final BdbSchemaStore schema = new BdbSchemaStore(
        new BdbBackedData(dataStoreFactory.getDatabase(
          userId,
          dataSetId,
          "schema",
          false,
          stringBinding,
          stringBinding,
          stringStringIsCleanHandler
        )),
        importManager.getImportStatus()
      );
      final TupleBinding<Integer> integerBinding = TupleBinding.getPrimitiveBinding(Integer.class);
      final UpdatedPerPatchStore updatedPerPatchStore = new UpdatedPerPatchStore(
        dataStoreFactory.getDatabase(
          userId,
          dataSetId,
          "updatedPerPatch",
          true,
          integerBinding,
          stringBinding,
          new IsCleanHandler<Integer, String>() {
            @Override
            public Integer getKey() {
              return Integer.MAX_VALUE;
            }

            @Override
            public String getValue() {
              return "isClean";
            }
          }
        )
      );
      final BdbTruePatchStore truePatchStore = new BdbTruePatchStore(version ->
          dataStoreFactory.getDatabase(
              userId,
              dataSetId,
              "truePatch" + version,
              true,
              stringBinding,
              stringBinding,
              stringStringIsCleanHandler
          ), updatedPerPatchStore);
      final BdbRmlDataSourceStore rmlDataSourceStore = new BdbRmlDataSourceStore(
        dataStoreFactory.getDatabase(
          userId,
          dataSetId,
          "rmlSource",
          true,
          stringBinding,
          stringBinding,
          stringStringIsCleanHandler
        ),
        importManager.getImportStatus()
      );
      VersionStore versionStore = new VersionStore(dataStoreFactory.getDatabase(
        userId,
        dataSetId,
        "versions",
        false,
        stringBinding,
        integerBinding,
        new IsCleanHandler<String, Integer>() {
          @Override
          public String getKey() {
            return "isClean";
          }

          @Override
          public Integer getValue() {
            return Integer.MAX_VALUE;
          }
        }
      ));

      final StoreUpdater storeUpdater = new StoreUpdater(
        quadStore,
        typeNameStore,
        truePatchStore,
        updatedPerPatchStore,
        Lists.newArrayList(schema, rmlDataSourceStore),
        versionStore,
        importManager.getImportStatus()
      );
      importManager.subscribeToRdf(storeUpdater);

      final ChangesRetriever changesRetriever = new ChangesRetriever(truePatchStore, updatedPerPatchStore);

      final CurrentStateRetriever currentStateRetriever = new CurrentStateRetriever(quadStore);


      ImmutableDataSet dataSet = ImmutableDataSet.builder()
        .ownerId(userId)
        .dataSetName(dataSetId)
        .bdbEnvironmentCreator(dataStoreFactory)
        .metadata(metadata)
        .quadStore(quadStore)
        .typeNameStore(typeNameStore)
        .schemaStore(schema)
        .updatedPerPatchStore(updatedPerPatchStore)
        .truePatchStore(truePatchStore)
        .versionStore(versionStore)
        .dataSource(new RdfDataSourceFactory(rmlDataSourceStore))
        .schemaStore(schema)
        .importManager(importManager)
        .dataSetStorage(dataSetStorage)
        .changesRetriever(changesRetriever)
        .currentStateRetriever(currentStateRetriever)
        .readOnlyChecker(readOnlyChecker)
        .build();
      importManager.init(dataSet);


      if (!quadStore.isClean() || !typeNameStore.isClean() || !schema.isClean() || !truePatchStore.isClean() ||
        !updatedPerPatchStore.isClean() || !rmlDataSourceStore.isClean() || !versionStore.isClean()) {
        LOG.error("Data set '{}__{}' data is corrupted, starting to reimport.", userId, dataSetId);
        quadStore.empty();
        typeNameStore.empty();
        schema.empty();
        truePatchStore.empty();
        updatedPerPatchStore.empty();
        rmlDataSourceStore.empty();
        versionStore.empty();

        importManager.reprocessLogs();

      } else {
        importManager.processLogs(); // process unprocessed logs
      }


      return dataSet;
    } catch (BdbDbCreationException e) {
      throw new DataStoreCreationException(e.getCause());
    }
  }

  public void stop() {
    // close the database environment
    getBdbEnvironmentCreator().closeEnvironment(getOwnerId(), getDataSetName());

  }

  public File getResourceSyncDescription() {
    return getDataSetStorage().getResourceSyncDescriptionFile();
  }

  private File getCustomSchemaFile() {
    return getDataSetStorage().getCustomSchemaFile();
  }

  public FileStorage getFileStorage() throws IOException {
    return getDataSetStorage().getFileStorage();
  }

  public Map<String, List<ExplicitField>> getCustomSchema() {
    return SchemaHelper.readExistingSchema(getCustomSchemaFile());
  }

  public void saveCustomSchema(Map<String, List<ExplicitField>> schema) throws IOException {
    SchemaHelper.saveSchema(schema, getCustomSchemaFile());
  }

  public CustomProvenance getCustomProvenance() {
    File customProvenanceFile = getDataSetStorage().getCustomProvenanceFile();
    if (customProvenanceFile.exists()) {
      try {
        return new ObjectMapper().readValue(customProvenanceFile, CustomProvenance.class);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return new CustomProvenance(Collections.emptyList());
  }

  public void setCustomProvenance(CustomProvenance customProvenance) throws IOException {
    new ObjectMapper().writeValue(getDataSetStorage().getCustomProvenanceFile(), customProvenance);
  }

  protected abstract String getOwnerId();

  protected abstract String getDataSetName();

  protected abstract BdbEnvironmentCreator getBdbEnvironmentCreator();

  protected abstract DataSetStorage getDataSetStorage();

  public abstract SchemaStore getSchemaStore();

  public abstract UpdatedPerPatchStore getUpdatedPerPatchStore();

  public abstract BdbTruePatchStore getTruePatchStore();

  public abstract TypeNameStore getTypeNameStore();

  public abstract VersionStore getVersionStore();

  public abstract ImportManager getImportManager();

  public abstract RdfDataSourceFactory getDataSource();

  public abstract QuadStore getQuadStore();

  public abstract DataSetMetaData getMetadata();

  public abstract ChangesRetriever getChangesRetriever();

  public abstract CurrentStateRetriever getCurrentStateRetriever();

  public abstract ReadOnlyChecker getReadOnlyChecker();

  public LogInfo getLogInfo() throws IOException {
    return new LogInfo(getDataSetStorage().getLogList().getData());
  }

  public void subscribeToDataChanges(Runnable updateListener) {
    getImportManager().subscribeImportSucceeded(updateListener);
  }

  public List<String> getUnavailableStores() {
    return getBdbEnvironmentCreator().getUnavailableDatabases(getMetadata().getOwnerId(), getMetadata().getDataSetId());
  }

  public void backupDatabases(String backupPath) throws IOException {
    getBdbEnvironmentCreator().backUpDatabases(backupPath, getMetadata().getOwnerId(), getMetadata().getDataSetId());
  }
}
