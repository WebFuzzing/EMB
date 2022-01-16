package nl.knaw.huygens.timbuctoo.v5.datastorage.implementations.filesystem;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.BasicDataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.datastorage.DataSetStorage;
import nl.knaw.huygens.timbuctoo.v5.datastorage.DataStorage;
import nl.knaw.huygens.timbuctoo.v5.filehelper.FileHelper;
import nl.knaw.huygens.timbuctoo.v5.jsonfilebackeddata.JsonFileBackedData;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfIoFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileSystemDataStorage implements DataStorage {
  @JsonIgnore
  private final FileHelper fileHelper;
  @JsonProperty("rootDir")
  private final String dataSetMetadataLocation;
  @JsonProperty("rdfIo")
  private final RdfIoFactory rdfIo;

  @JsonCreator
  public FileSystemDataStorage(
      @JsonProperty("rootDir") String dataSetMetadataLocation,
      @JsonProperty("rdfIo") RdfIoFactory rdfIo) {
    fileHelper = new FileHelper(dataSetMetadataLocation);
    this.dataSetMetadataLocation = dataSetMetadataLocation;
    this.rdfIo = rdfIo;
  }

  @Override
  public boolean dataSetExists(String ownerId, String dataSetName) {
    return fileHelper.dataSetExists(ownerId, dataSetName);
  }

  @Override
  public DataSetStorage getDataSetStorage(String ownerId, String dataSetName) {
    return new FileSystemDataSetStorage(ownerId, dataSetName, fileHelper, rdfIo);
  }

  @Override
  public Map<String, Set<DataSetMetaData>> loadDataSetMetaData() throws IOException {
    Map<String, Set<DataSetMetaData>> metaDataSet = Maps.newHashMap();

    File[] directories = new File(dataSetMetadataLocation).listFiles(File::isDirectory);
    for (File directory : directories) {
      String dirName = directory.toString();
      String currentOwnerId = dirName.substring(dirName.lastIndexOf("/") + 1);
      Set<DataSetMetaData> tempMetaDataSet = new HashSet<>();
      try (Stream<Path> fileStream = Files.walk(directory.toPath())) {
        Set<Path> paths = fileStream.filter(current -> Files.isDirectory(current)).collect(Collectors.toSet());
        for (Path path : paths) {
          File tempFile = new File(path.toString() + "/metaData.json");
          if (tempFile.exists()) {
            JsonFileBackedData<BasicDataSetMetaData> metaDataFromFile = JsonFileBackedData.getOrCreate(
                tempFile,
                null,
                new TypeReference<>() {
                });

            BasicDataSetMetaData updated = metaDataFromFile.getData().update();
            if (updated != null) {
              metaDataFromFile.updateData(data -> updated);
            }

            tempMetaDataSet.add(metaDataFromFile.getData());
          }
        }
      }

      metaDataSet.put(currentOwnerId, tempMetaDataSet);
    }
    return metaDataSet;
  }
}
