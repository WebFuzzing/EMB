package nl.knaw.huygens.timbuctoo.v5.datastorage;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface DataStorage {

  boolean dataSetExists(String ownerId, String dataSetName);

  DataSetStorage getDataSetStorage(String ownerId, String dataSetName);

  Map<String, Set<DataSetMetaData>> loadDataSetMetaData() throws IOException;

}
