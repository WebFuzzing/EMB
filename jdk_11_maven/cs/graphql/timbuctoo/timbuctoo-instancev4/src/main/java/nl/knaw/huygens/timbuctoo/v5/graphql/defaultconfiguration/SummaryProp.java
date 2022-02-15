package nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXTERNAL_PROPERTY, property = "type")
@JsonSubTypes({
    @JsonSubTypes.Type(name = "SimplePath", value = SimplePath.class),
    @JsonSubTypes.Type(name = "DirectionalPath", value = DirectionalPath.class)
  })
public interface SummaryProp {
  List<DirectionalStep> getPath();

  static List<String> getUndirectedPath(SummaryProp summaryProp)  {
    if (summaryProp instanceof SimplePath) {
      return ((SimplePath) summaryProp).getSimplePath();
    } else if (summaryProp instanceof DirectionalPath) {
      return summaryProp.getPath().stream()
                        .map(DirectionalStep::getStep).collect(Collectors.toList());
    }
    return Lists.newArrayList();
  }
}
