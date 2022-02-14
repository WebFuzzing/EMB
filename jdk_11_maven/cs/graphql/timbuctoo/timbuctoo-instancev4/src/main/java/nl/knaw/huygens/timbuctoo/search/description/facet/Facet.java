package nl.knaw.huygens.timbuctoo.search.description.facet;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

public class Facet {

  private final String type;

  private final String name;
  private final List<Option> options;

  public Facet(String name, List<Option> options, String type) {
    this.name = name;
    this.options = options;
    this.type = type;
  }

  public List<Option> getOptions() {
    return this.options;
  }


  public String getName() {
    return name;
  }

  public String getType() {
    return type;
  }

  public interface Option {
  }

  public static class DefaultOption implements Option {
    private final String name;
    private final long count;

    public DefaultOption(String name, long count) {
      this.name = name;
      this.count = count;
    }


    public String getName() {
      return name;
    }

    public long getCount() {
      return count;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }
  }

  public static class RangeOption implements Option {
    private final long lowerLimit;
    private final long upperLimit;

    public RangeOption(long lowerLimit, long upperLimit) {
      this.lowerLimit = lowerLimit;
      this.upperLimit = upperLimit;
    }

    @Override
    public String toString() {
      return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public boolean equals(Object obj) {
      return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
      return HashCodeBuilder.reflectionHashCode(this);
    }

    public long getLowerLimit() {
      return lowerLimit;
    }

    public long getUpperLimit() {
      return upperLimit;
    }
  }

}


