package nl.knaw.huygens.timbuctoo.v5.datastorage;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.datastorage.exceptions.DataStorageSaveException;
import nl.knaw.huygens.timbuctoo.v5.filestorage.FileStorage;
import nl.knaw.huygens.timbuctoo.v5.filestorage.LogStorage;
import nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata.JsonDataStore;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfIoFactory;

import java.io.File;
import java.io.IOException;

public interface DataSetStorage {
  void saveMetaData(DataSetMetaData metaData) throws DataStorageSaveException;

  FileStorage getFileStorage() throws IOException;

  LogStorage getLogStorage() throws IOException;

  RdfIoFactory getRdfIo();

  File getResourceSyncDescriptionFile();

  File getCustomSchemaFile();

  JsonDataStore<LogList> getLogList() throws IOException;

  void clear() throws IOException;

  File getCustomProvenanceFile();
}
