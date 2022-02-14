package nl.knaw.huygens.timbuctoo.core.dto;

import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.immutables.value.Value;

import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.model.GraphReadUtils.getProp;

@Value.Immutable
public abstract class RelationType {

  public static RelationType relationType(Vertex vertex) {
    return ImmutableRelationType
      .builder()
      .outName(getProp(vertex, "relationtype_regularName", String.class).orElse("<no name>"))
      .inverseName(getProp(vertex, "relationtype_inverseName", String.class).orElse("<no name>"))
      .sourceTypeName(getProp(vertex, "relationtype_sourceTypeName", String.class).orElse(""))
      .targetTypeName(getProp(vertex, "relationtype_targetTypeName", String.class).orElse(""))
      .isReflexive(getProp(vertex, "relationtype_reflexive", Boolean.class).orElse(false))
      .isSymmetric(getProp(vertex, "relationtype_symmetric", Boolean.class).orElse(false))
      .isDerived(getProp(vertex, "relationtype_derived", Boolean.class).orElse(false))
      .timId(UUID.fromString(getProp(vertex, "tim_id", String.class).orElse("")))
      .build();
  }

  public static RelationType relationType(String sourceType, String outName, String targetType, String inName,
                                          boolean isReflexive, boolean isSymmetric, boolean isDerived, UUID timId) {
    return ImmutableRelationType
      .builder()
      .outName(outName)
      .inverseName(inName)
      .sourceTypeName(sourceType)
      .targetTypeName(targetType)
      .isReflexive(isReflexive)
      .isSymmetric(isSymmetric)
      .isDerived(isDerived)
      .timId(timId)
      .build();
  }

  public Optional<DirectionalRelationType> getForDirection(Collection collA, Collection collB) {
    boolean sourceIsCollA = getSourceTypeName().equals(collA.getAbstractType());
    boolean sourceIsEmpty = getSourceTypeName().equals("");
    boolean sourceIsCollB = getSourceTypeName().equals(collB.getAbstractType());

    boolean targetIsCollA = getTargetTypeName().equals(collA.getAbstractType());
    boolean targetIsEmpty = getTargetTypeName().equals("");
    boolean targetIsCollB = getTargetTypeName().equals(collB.getAbstractType());


    //FIXME: complex evaluation is needed to as a workaround for unittests. In reality source and target are never empty
    if ((sourceIsCollA && targetIsCollB) || (sourceIsEmpty && targetIsCollB) || (sourceIsCollA && targetIsEmpty) ||
      (sourceIsEmpty && targetIsEmpty)) {
      return Optional.of(new DirectionalRelationType(
        getOutName(),
        getInverseName(),
        getSourceTypeName(),
        getTargetTypeName(),
        isReflexive(),
        isSymmetric(),
        isDerived(),
        false,
        getTimId().toString()
      ));
    }
    if ((sourceIsCollB && targetIsCollA) || (sourceIsEmpty && targetIsCollA) || (sourceIsCollB && targetIsEmpty)) {
      return Optional.of(new DirectionalRelationType(
        getOutName(),
        getInverseName(),
        getSourceTypeName(),
        getTargetTypeName(),
        isReflexive(),
        isSymmetric(),
        isDerived(),
        true,
        getTimId().toString()
      ));
    }
    return Optional.empty();
  }

  public abstract UUID getTimId();

  public abstract String getOutName();

  public abstract String getInverseName();

  public abstract String getSourceTypeName();

  public abstract String getTargetTypeName();

  public abstract boolean isReflexive();

  public abstract boolean isSymmetric();

  public abstract boolean isDerived();

}
