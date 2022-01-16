package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;

class SearchResponseV2_1Ref {

  private final String type;
  private final String id;
  private final String path;
  private String relationName;
  private String displayName;
  private Object data;

  private String sourceName;
  private Map<String, Object> sourceData;
  private String targetName;
  private Map<String, Object> targetData;

  public SearchResponseV2_1Ref(String id, String type, String path, String displayName, Object data) {

    this.id = id;
    this.type = type;
    this.path = path;
    this.displayName = displayName;
    this.data = data;
  }

  public SearchResponseV2_1Ref(String id, String type, String path, String sourceName, Map<String, Object> sourceData,
                               String targetName, Map<String, Object> targetData, String relationName) {
    this.id = id;
    this.type = type;
    this.path = path;
    this.sourceName = sourceName;
    this.sourceData = sourceData;
    this.targetName = targetName;
    this.targetData = targetData;
    this.relationName = relationName;
  }

  public String getType() {
    return type;
  }

  public String getId() {
    return id;
  }

  public String getPath() {
    return path;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String getDisplayName() {
    return displayName;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Object getData() {
    return data;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String getSourceName() {
    return sourceName;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Map<String, Object> getSourceData() {
    return sourceData;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String getTargetName() {
    return targetName;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Map<String, Object> getTargetData() {
    return targetData;
  }

  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String getRelationName() {
    return relationName;
  }
}
