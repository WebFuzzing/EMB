package nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

public class CustomProvenance {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @JsonProperty("fields")
  private List<CustomProvenanceValueFieldInput> fields;

  @JsonCreator
  public CustomProvenance(@JsonProperty("fields") List<CustomProvenanceValueFieldInput> fields) {
    this.fields = fields;
  }

  public static CustomProvenance getCustomProvenance(Map entity) throws JsonProcessingException {
    TreeNode jsonNode = OBJECT_MAPPER.valueToTree(entity);
    return OBJECT_MAPPER.treeToValue(jsonNode, CustomProvenance.class);
  }

  public List<CustomProvenanceValueFieldInput> getFields() {
    return fields;
  }

  public static class CustomProvenanceObjectFieldInput extends CustomProvenance {
    @JsonProperty
    private String type;

    @JsonCreator
    public CustomProvenanceObjectFieldInput(@JsonProperty("type") String type,
                                            @JsonProperty("fields") List<CustomProvenanceValueFieldInput> fields) {
      super(fields);
      this.type = type;
    }

    public String getType() {
      return type;
    }
  }

  public static class CustomProvenanceValueFieldInput {
    @JsonProperty
    private String uri;
    @JsonProperty("isList")
    private boolean isList;
    @JsonProperty
    private String valueType;
    @JsonProperty
    private CustomProvenanceObjectFieldInput object;

    @JsonCreator
    public CustomProvenanceValueFieldInput(
      @JsonProperty("uri") String uri,
      @JsonProperty("isList") boolean isList,
      @JsonProperty("valueType") String valueType,
      @JsonProperty("object") CustomProvenanceObjectFieldInput object) {
      this.uri = uri;
      this.isList = isList;
      this.valueType = valueType;
      this.object = object;
    }

    public String getUri() {
      return uri;
    }

    @JsonProperty("isList")
    public boolean isList() {
      return isList;
    }

    public String getValueType() {
      return valueType;
    }

    public CustomProvenanceObjectFieldInput getObject() {
      return object;
    }
  }
}
