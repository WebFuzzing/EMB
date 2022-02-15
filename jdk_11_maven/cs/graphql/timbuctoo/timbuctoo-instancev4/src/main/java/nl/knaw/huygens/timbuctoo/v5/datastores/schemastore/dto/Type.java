package nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static com.google.common.base.Objects.equal;

public class Type {
  private String name;
  private Map<String, Predicate> predicates = new HashMap<>();
  private long subjectsWithThisType = 0;
  private final BiFunction<String, Direction, Predicate> predicateMaker = (name, direction) -> {
    Predicate predicate = new Predicate(name, direction);
    predicate.setOwner(this);
    return predicate;
  };

  public Collection<Predicate> getPredicates() {
    return predicates.values();
  }

  public void setPredicates(Collection<Predicate> predicates) {
    for (Predicate predicate : predicates) {
      predicate.setOwner(this);
      this.predicates.put(predicate.getDirection() + "\n" + predicate.getName(), predicate);
    }
  }

  @JsonCreator
  public Type(@JsonProperty("name") String name) {
    this.name = name;
    predicates = new HashMap<>(64);
  }

  public Predicate getOrCreatePredicate(String name, Direction direction) {
    return predicates.computeIfAbsent(direction.name() + "\n" + name, key -> predicateMaker.apply(name, direction));
  }

  public long getSubjectsWithThisType() {
    return subjectsWithThisType;
  }

  /**
   * The RDF name i.e. the URI
   */
  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    } else if (getClass() != obj.getClass()) {
      return false;
    } else {
      final Type other = (Type) obj;

      boolean propsEqual = equal(this.name, other.name) &&
        equal(this.subjectsWithThisType, other.subjectsWithThisType);

      if (propsEqual && predicates.size() == other.predicates.size()) {
        for (Map.Entry<String, Predicate> predicateEntry : predicates.entrySet()) {
          if (!predicateEntry.getValue().equals(other.predicates.get(predicateEntry.getKey()))) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    }
  }


  public Predicate getPredicate(String name, Direction direction) {
    return predicates.get(direction.name() + "\n" + name);
  }

  public void registerSubject(int mut) {
    subjectsWithThisType += mut;
  }
}
