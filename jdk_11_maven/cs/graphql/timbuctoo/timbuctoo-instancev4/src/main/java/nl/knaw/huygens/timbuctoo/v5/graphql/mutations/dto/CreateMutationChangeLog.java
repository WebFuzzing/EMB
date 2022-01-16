package nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;

@JsonTypeName("CreateMutationChangeLog")
public class CreateMutationChangeLog extends ChangeLog {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @JsonProperty
  private final String subject;

  @JsonProperty
  private final String typeUri;

  @JsonProperty
  private final RawCreateChangeLog changeLog;

  public CreateMutationChangeLog(String subject, String typeUri, Map entity) throws JsonProcessingException {
    this.subject = subject;
    this.typeUri = typeUri;

    TreeNode jsonNode = OBJECT_MAPPER.valueToTree(entity);
    this.changeLog = OBJECT_MAPPER.treeToValue(jsonNode, RawCreateChangeLog.class);
  }

  private CreateMutationChangeLog(String subject, String typeUri, RawCreateChangeLog changeLog) {
    this.subject = subject;
    this.typeUri = typeUri;
    this.changeLog = changeLog;
  }

  @JsonCreator
  public static CreateMutationChangeLog fromJson(@JsonProperty("subject") String subject,
                                                 @JsonProperty("typeUri") String typeUri,
                                                 @JsonProperty("changeLog") RawCreateChangeLog changeLog) {
    return new CreateMutationChangeLog(subject, typeUri, changeLog);
  }

  @Override
  public Stream<Change> getProvenance(DataSet dataSet, String... subjects) {
    return getProvenanceChanges(dataSet, subjects, dataSet.getCustomProvenance(), changeLog.getProvenance());
  }

  @Override
  public Stream<Change> getAdditions(DataSet dataSet) {
    Change typeAddition =
      new Change(subject, RDF_TYPE, Collections.singletonList(new Value(typeUri, null)), Stream.empty());

    Stream<Change> additions = changeLog.getCreations().entrySet().stream()
                                        .filter(entry -> !entry.getValue().isNull() &&
                                          !(entry.getValue().isArray() && entry.getValue().size() == 0))
                                        .map(entry -> createChange(dataSet, entry.getKey(), entry.getValue()));

    return Stream.concat(Stream.of(typeAddition), additions);
  }

  @Override
  public Stream<Change> getDeletions(DataSet dataSet) {
    return Stream.empty();
  }

  @Override
  public Stream<Change> getReplacements(DataSet dataSet) {
    return Stream.empty();
  }

  private Change createChange(DataSet dataSet, String graphQlpred, JsonNode val) {
    String pred = getPredicate(dataSet, graphQlpred);
    List<Value> values = getValues(dataSet, val);

    return new Change(subject, pred, values, Stream.empty());
  }

  public static class RawCreateChangeLog {
    private LinkedHashMap<String, JsonNode> creations;
    private LinkedHashMap<String, JsonNode> provenance;

    @JsonCreator
    public RawCreateChangeLog(@JsonProperty("creations") LinkedHashMap<String, JsonNode> creations,
                              @JsonProperty("provenance") LinkedHashMap<String, JsonNode> provenance) {
      this.creations = creations == null ? Maps.newLinkedHashMap() : creations;
      this.provenance = provenance == null ? Maps.newLinkedHashMap() : provenance;
    }

    public LinkedHashMap<String, JsonNode> getCreations() {
      return creations;
    }

    public LinkedHashMap<String, JsonNode> getProvenance() {
      return provenance;
    }
  }
}
