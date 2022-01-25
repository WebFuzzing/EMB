package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportInfo;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto.LazyTypeSubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.security.UserPermissionCheck;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DataSetWithDatabase extends LazyTypeSubjectReference implements DataSetMetaData {
  private final DataSetMetaData dataSetMetaData;
  private final Set<String> userPermissions;

  public DataSetWithDatabase(DataSet dataSet, UserPermissionCheck userPermissionCheck) {
    super(dataSet.getMetadata().getBaseUri(), dataSet);
    this.dataSetMetaData = dataSet.getMetadata();
    this.userPermissions = userPermissionCheck.getPermissions(dataSet.getMetadata()).stream()
                                              .map(Enum::toString)
                                              .collect(Collectors.toSet());
    ;
  }

  @Override
  public String getDataSetId() {
    return dataSetMetaData.getDataSetId();
  }

  @Override
  public String getOwnerId() {
    return dataSetMetaData.getOwnerId();
  }

  @Override
  public String getBaseUri() {
    return dataSetMetaData.getBaseUri();
  }

  @Override
  public String getGraph() {
    return dataSetMetaData.getGraph();
  }

  @Override
  public String getUriPrefix() {
    return dataSetMetaData.getUriPrefix();
  }

  @Override
  public String getCombinedId() {
    return dataSetMetaData.getCombinedId();
  }

  @Override
  public boolean isPromoted() {
    return dataSetMetaData.isPromoted();
  }

  @Override
  public boolean isPublished() {
    return dataSetMetaData.isPublished();
  }

  @Override
  public List<ImportInfo> getImportInfo() {
    return dataSetMetaData.getImportInfo();
  }

  @Override
  public String getSubjectUri() {
    return dataSetMetaData.getBaseUri();
  }

  @Override
  public void publish() {
    dataSetMetaData.publish();
  }

  public Set<String> getUserPermissions() {
    return userPermissions;
  }

}
