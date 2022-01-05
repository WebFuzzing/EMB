package nl.knaw.huygens.timbuctoo.v5.datastorage.implementations.filesystem;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetConfiguration;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.datastorage.DataSetStorage;
import nl.knaw.huygens.timbuctoo.v5.datastorage.exceptions.DataStorageSaveException;
import nl.knaw.huygens.timbuctoo.v5.filehelper.FileHelper;
import nl.knaw.huygens.timbuctoo.v5.filestorage.FileStorage;
import nl.knaw.huygens.timbuctoo.v5.filestorage.LogStorage;
import nl.knaw.huygens.timbuctoo.v5.filestorage.implementations.filesystem.FileSystemFileStorage;
import nl.knaw.huygens.timbuctoo.v5.jacksonserializers.TimbuctooCustomSerializers;
import nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata.JsonDataStore;
import nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata.JsonFileBackedData;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfIoFactory;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

class FileSystemDataSetStorage implements DataSetStorage {

  private static final Logger LOG = LoggerFactory.getLogger(FileSystemDataStorage.class);
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new GuavaModule())
    .registerModule(new TimbuctooCustomSerializers())
    .enable(SerializationFeature.INDENT_OUTPUT);

  private final String ownerPrefix;
  private final String dataSetId;
  private final FileHelper fileHelper;
  private final File logFile;
  private final File metaDataFile;
  private final RdfIoFactory rdfIo;

  FileSystemDataSetStorage(String ownerId, String dataSetId, FileHelper fileHelper, RdfIoFactory rdfIo) {
    this.ownerPrefix = ownerId;
    this.dataSetId = dataSetId;
    this.fileHelper = fileHelper;
    logFile = fileHelper.fileInDataSet(ownerPrefix, dataSetId, "log.json");
    metaDataFile = fileHelper.fileInDataSet(ownerPrefix, dataSetId, "metaData.json");
    this.rdfIo = rdfIo;
  }

  @Override
  public void saveMetaData(DataSetMetaData metaData) throws DataStorageSaveException {

    try {
      OBJECT_MAPPER.writeValue(metaDataFile, metaData);
    } catch (IOException e) {
      throw new DataStorageSaveException(e);
    }
  }

  @Override
  public FileStorage getFileStorage() throws IOException {
    File filePath = fileHelper.pathInDataSet(ownerPrefix, dataSetId, "files");
    return new FileSystemFileStorage(filePath);
  }

  @Override
  public LogStorage getLogStorage() throws IOException {
    File filePath = fileHelper.pathInDataSet(ownerPrefix, dataSetId, "files");
    return new FileSystemFileStorage(filePath);
  }

  @Override
  public RdfIoFactory getRdfIo() {
    return rdfIo;
  }

  @Override
  public File getResourceSyncDescriptionFile() {
    return fileHelper.fileInDataSet(ownerPrefix, dataSetId, "description.xml");
  }

  @Override
  public File getCustomSchemaFile() {
    return fileHelper.fileInDataSet(ownerPrefix, dataSetId, "customSchema.json");
  }

  @Override
  public File getCustomProvenanceFile() {
    return fileHelper.fileInDataSet(ownerPrefix, dataSetId, "customProvenance.json");
  }

  @Override
  public JsonDataStore<LogList> getLogList() throws IOException {
    return JsonFileBackedData.getOrCreate(logFile, LogList::new, new TypeReference<LogList>(){});
  }

  @Override
  public void clear() throws IOException {
    JsonFileBackedData.remove(logFile);
    JsonFileBackedData.remove(metaDataFile);
    getFileStorage().clear();
    this.deleteDataSetData(fileHelper.dataSetPath(ownerPrefix, dataSetId), 5);

  }

  private void deleteDataSetData(File path, int retryCount) {
    try {
      FileUtils.deleteDirectory(path);
    } catch (IOException e) {
      if (retryCount > 0) {
        LOG.warn("Deleting directory {}, failed, {} tries remaining.", path.getAbsolutePath(), retryCount);
        try {
          Thread.sleep(500);
          deleteDataSetData(path, retryCount - 1);
        } catch (InterruptedException e1) {
          //ignore and stop trying
        }
      } else {
        LOG.error("Deleting directory {}, failed! Manual cleanup necessary", path.getAbsolutePath());
      }
    }
  }
}
