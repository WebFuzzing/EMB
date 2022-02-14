package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;
import java.util.stream.Stream;

public class Change {
  private final String subject;
  private final String predicate;
  private final List<Value> values;
  private final Stream<Value> oldValues;

  public Change(String subject, String predicate, Value value) {
    this.subject = subject;
    this.predicate = predicate;

    this.values = Lists.newArrayList(value);
    this.oldValues = Stream.empty();
  }

  public Change(String subject, String predicate, List<Value> values, Stream<Value> oldValues) {
    this.subject = subject;
    this.predicate = predicate;

    this.values = values;
    this.oldValues = oldValues;
  }

  public String getSubject() {
    return subject;
  }

  public String getPredicate() {
    return predicate;
  }


  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }

    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    Change change = (Change) other;

    return new EqualsBuilder()
      .append(subject, change.subject)
      .append(predicate, change.predicate)
      .append(values, change.values)
      .append(oldValues, change.oldValues)
      .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
      .append(subject)
      .append(predicate)
      .append(values)
      .append(oldValues)
      .toHashCode();
  }

  @Override
  public String toString() {
    return "Change{" +
      "subject='" + subject + '\'' +
      ", predicate='" + predicate + '\'' +
      ", values=" + values +
      ", oldValues=" + oldValues +
      '}';
  }

  public List<Value> getValues() {
    return values;
  }

  public Stream<Value> getOldValues() {
    return oldValues;
  }

  public static class Value {
    private final String rawValue;
    private final String type;

    public Value(String rawValue, String type) {
      this.rawValue = rawValue;
      this.type = type;
    }

    public String getRawValue() {
      return rawValue;
    }

    public String getType() {
      return type;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }

      if (other == null || getClass() != other.getClass()) {
        return false;
      }

      Value value = (Value) other;

      return new EqualsBuilder()
        .append(rawValue, value.rawValue)
        .append(type, value.type)
        .isEquals();
    }

    @Override
    public int hashCode() {
      return new HashCodeBuilder(17, 37)
        .append(rawValue)
        .append(type)
        .toHashCode();
    }

    @Override
    public String toString() {
      return "Value{" +
        "rawValue='" + rawValue + '\'' +
        ", type='" + type + '\'' +
        '}';
    }
  }
}
