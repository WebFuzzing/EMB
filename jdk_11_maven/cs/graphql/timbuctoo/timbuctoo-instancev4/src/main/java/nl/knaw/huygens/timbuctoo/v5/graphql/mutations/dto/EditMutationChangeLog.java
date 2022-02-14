package nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@JsonTypeName("EditMutationChangeLog")
public class EditMutationChangeLog extends ChangeLog {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @JsonProperty
  private final String subject;

  @JsonProperty
  private final RawEditChangeLog changeLog;

  public EditMutationChangeLog(String subject, Map entity) throws JsonProcessingException {
    this.subject = subject;

    TreeNode jsonNode = OBJECT_MAPPER.valueToTree(entity);
    this.changeLog = OBJECT_MAPPER.treeToValue(jsonNode, RawEditChangeLog.class);
  }

  private EditMutationChangeLog(String subject, RawEditChangeLog changeLog) {
    this.subject = subject;
    this.changeLog = changeLog;
  }

  @JsonCreator
  public static EditMutationChangeLog fromJson(@JsonProperty("subject") String subject,
                                               @JsonProperty("changeLog") RawEditChangeLog changeLog) {
    return new EditMutationChangeLog(subject, changeLog);
  }

  @Override
  public Stream<Change> getProvenance(DataSet dataSet, String... subjects) {
    return getProvenanceChanges(dataSet, subjects, dataSet.getCustomProvenance(), changeLog.getProvenance());
  }

  @Override
  public Stream<Change> getAdditions(DataSet dataSet) {
    Stream<Change> additions =
      changeLog.getAdditions().entrySet().stream()
               .map(entry -> createAdditionsChange(dataSet, entry.getKey(), entry.getValue()));

    Stream<Change> replacementAdditions =
      changeLog.getReplacements().entrySet().stream()
               .filter(entry -> !hasOldValues(dataSet, entry))
               .filter(entry -> !entry.getValue().isNull() &&
                 !(entry.getValue().isArray() && entry.getValue().size() == 0))
               .map(entry -> createAdditionsChange(dataSet, entry.getKey(), entry.getValue()));

    return Stream.concat(additions, replacementAdditions);
  }

  @Override
  public Stream<Change> getDeletions(DataSet dataSet) {
    Stream<Change> deletions =
      changeLog.getDeletions().entrySet().stream()
               .map(entry -> createDeletionsChange(dataSet, entry.getKey(), entry.getValue()));

    Stream<Change> replacementDeletions =
      changeLog.getReplacements().entrySet().stream()
               .filter(entry -> hasOldValues(dataSet, entry))
               .filter(entry -> entry.getValue().isNull() ||
                 (entry.getValue().isArray() && entry.getValue().size() == 0))
               .map(entry -> createDeletionsChange(dataSet, entry.getKey(), entry.getValue()));

    return Stream.concat(deletions, replacementDeletions);
  }

  @Override
  public Stream<Change> getReplacements(DataSet dataSet) {
    return changeLog.getReplacements().entrySet().stream()
                    .filter(entry -> hasOldValues(dataSet, entry))
                    .filter(entry -> !entry.getValue().isNull() &&
                      !(entry.getValue().isArray() && entry.getValue().size() == 0))
                    .map((entry) -> createReplacementsChange(dataSet, entry.getKey(), entry.getValue()));
  }

  private boolean hasOldValues(DataSet dataSet, Map.Entry<String, JsonNode> entry) {
    try (Stream<CursorQuad> quads = dataSet.getQuadStore().getQuads(
      subject,
      dataSet.getTypeNameStore().makeUriForPredicate(entry.getKey()).get().getLeft(),
      Direction.OUT,
      ""
    )) {
      return quads.findAny().isPresent();
    }
  }

  private Change createAdditionsChange(DataSet dataSet, String graphQlpred, JsonNode val) {
    String pred = getPredicate(dataSet, graphQlpred);
    List<Value> values = getValues(dataSet, val);

    return new Change(subject, pred, values, Stream.empty());
  }

  private Change createDeletionsChange(DataSet dataSet, String graphQlpred, JsonNode val) {
    List<Value> values = getValues(dataSet, val);

    String pred = getPredicate(dataSet, graphQlpred);
    Stream<Value> oldValues = getOldValues(dataSet, subject, pred)
                                       .filter(value -> values.isEmpty() || values.contains(value));

    return new Change(subject, pred, Lists.newArrayList(), oldValues);
  }

  private Change createReplacementsChange(DataSet dataSet, String graphQlpred, JsonNode val) {
    String pred = getPredicate(dataSet, graphQlpred);
    Stream<Value> oldValues = getOldValues(dataSet, subject, pred);
    List<Value> values = getValues(dataSet, val);

    return new Change(subject, pred, values, oldValues);
  }

  public static class RawEditChangeLog {
    private LinkedHashMap<String, ArrayNode> additions;
    private LinkedHashMap<String, ArrayNode> deletions;
    private LinkedHashMap<String, JsonNode> replacements;
    private LinkedHashMap<String, JsonNode> provenance;

    @JsonCreator
    public RawEditChangeLog(
      @JsonProperty("additions") LinkedHashMap<String, ArrayNode> additions,
      @JsonProperty("deletions") LinkedHashMap<String, ArrayNode> deletions,
      @JsonProperty("replacements") LinkedHashMap<String, JsonNode> replacements,
      @JsonProperty("provenance") LinkedHashMap<String, JsonNode> provenance) {
      this.additions = additions == null ? Maps.newLinkedHashMap() : additions;
      this.deletions = deletions == null ? Maps.newLinkedHashMap() : deletions;
      this.replacements = replacements == null ? Maps.newLinkedHashMap() : replacements;
      this.provenance = provenance == null ? Maps.newLinkedHashMap() : provenance;
    }


    public LinkedHashMap<String, ArrayNode> getAdditions() {
      return additions;
    }

    public LinkedHashMap<String, ArrayNode> getDeletions() {
      return deletions;
    }

    public LinkedHashMap<String, JsonNode> getReplacements() {
      return replacements;
    }

    public LinkedHashMap<String, JsonNode> getProvenance() {
      return provenance;
    }
  }
}
