package nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import org.immutables.value.Value;

@Value.Immutable
public interface DirectionalStep {

  String getStep();

  Direction getDirection();

  @JsonCreator
  static DirectionalStep create(@JsonProperty("step") String step, @JsonProperty("direction") Direction direction) {
    return ImmutableDirectionalStep.builder().step(step).direction(direction).build();
  }

}
