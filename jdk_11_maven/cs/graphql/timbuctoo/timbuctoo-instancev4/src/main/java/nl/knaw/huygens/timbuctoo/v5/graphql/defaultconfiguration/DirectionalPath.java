package nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@JsonTypeName("DirectionalPath")
public interface DirectionalPath extends SummaryProp {
  @JsonCreator
  static DirectionalPath create(@JsonProperty("path") List<DirectionalStep> path) {
    return ImmutableDirectionalPath.builder().path(path).build();
  }

  List<DirectionalStep> getPath();

}
