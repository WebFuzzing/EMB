package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.IllegalDataSetNameException;
import org.immutables.value.Value;

import static nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData.createCombinedId;

public class OldDataSetMetaData {
  private static final String VALID_ID = "^[a-z](_?[a-z0-9]+)+$";
  private final String dataSetId;
  private final String ownerId;
  private final String baseUri;
  private final String uriPrefix;
  private final String combinedId;
  private boolean promoted;

  @JsonCreator
  public OldDataSetMetaData(@JsonProperty("ownerId") String ownerId,
                            @JsonProperty("dataSetId") String dataSetId,
                            @JsonProperty("baseUri") String baseUri,
                            @JsonProperty("uriPrefix") String uriPrefix,
                            @JsonProperty("isPromoted") boolean isPromoted) throws IllegalDataSetNameException {

    if (!ownerId.matches(VALID_ID) || !dataSetId.matches(VALID_ID)) {
      throw new IllegalDataSetNameException("Owner id and dataSet id should match " + VALID_ID);
    }

    this.dataSetId = dataSetId;
    this.ownerId = ownerId;
    this.combinedId = createCombinedId(ownerId, dataSetId);
    this.baseUri = baseUri;
    this.uriPrefix = uriPrefix;
    this.promoted = isPromoted;
  }


  public String getDataSetId() {
    return dataSetId;
  }


  public String getOwnerId() {
    return ownerId;
  }

  /**
   * Returns the baseUri that is used to resolve relative uri's in uploaded rdf files that have no explicit baseUri set.
   */

  public String getBaseUri() {
    return baseUri;
  }

  /**
   * Returns a uri that you can use to generate dataSet-local uri's
   */

  public String getUriPrefix() {
    return uriPrefix;
  }


  public String getCombinedId() {
    return combinedId;
  }


  @Value.Auxiliary
  public boolean isPromoted() {
    return promoted;
  }

  public BasicDataSetMetaData convertToDataSetMetaData() throws IllegalDataSetNameException {
    BasicDataSetMetaData dataSetMetaData;

    String graph = baseUri.endsWith("/") ? baseUri.substring(0, baseUri.length() - 1) : baseUri;
    dataSetMetaData = new BasicDataSetMetaData(this.ownerId, this.dataSetId, this.baseUri, graph,
      this.uriPrefix, this.promoted, false);


    return dataSetMetaData;
  }

}
