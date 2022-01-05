package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class ImportInfo {

  @JsonProperty
  private String importSource;

  @JsonProperty
  private Date lastImportedOn;

  @JsonCreator
  public ImportInfo(@JsonProperty("importSource") String importSource,
                    @JsonProperty("lastImportedOn") Date lastImportedOn) {
    this.importSource = importSource;
    this.lastImportedOn = lastImportedOn;
  }


  public String getImportSource() {
    return importSource;
  }

  public Date getLastImportedOn() {
    return lastImportedOn;
  }

  public void setLastImportedOn(Date lastImportedOn) {
    this.lastImportedOn = lastImportedOn;
  }
}
