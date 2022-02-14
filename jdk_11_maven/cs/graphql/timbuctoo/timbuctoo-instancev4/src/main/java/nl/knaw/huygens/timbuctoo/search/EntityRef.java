package nl.knaw.huygens.timbuctoo.search;

import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class EntityRef {
  private String id;
  private String type;
  private String displayName;
  private Map<String, Object> data;
  private Map<String, Object> targetData;
  private String targetName;
  private Map<String, Object> sourceData;
  private String sourceName;
  private String relationName;

  public EntityRef(String type, String id) {
    this.id = id;
    this.type = type;
  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  @Override
  public boolean equals(Object obj) {
    return reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return reflectionHashCode(this);
  }

  @Override
  public String toString() {
    return reflectionToString(this);
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public void setData(Map<String, Object> data) {
    this.data = data;
  }

  public void setTargetData(Map<String, Object> targetData) {
    this.targetData = targetData;
  }

  public void setTargetName(String targetName) {
    this.targetName = targetName;
  }

  public void setSourceData(Map<String, Object> sourceData) {
    this.sourceData = sourceData;
  }

  public void setSourceName(String sourceName) {
    this.sourceName = sourceName;
  }

  public Map<String, Object> getTargetData() {
    return targetData;
  }

  public String getTargetName() {
    return targetName;
  }

  public Map<String, Object> getSourceData() {
    return sourceData;
  }

  public String getSourceName() {
    return sourceName;
  }

  public void setRelationName(String relationName) {
    this.relationName = relationName;
  }

  public String getRelationName() {
    return relationName;
  }
}
