package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.tinkerpop.gremlin.process.traversal.Order;

public class SortParameter {
  @JsonProperty("fieldname")
  private String fieldName;

  private Direction direction;

  public SortParameter(String fieldName, Direction direction) {
    this.fieldName = fieldName;
    this.direction = direction;
  }

  public SortParameter() {
    // constructor for Jackson
  }

  public String getFieldName() {
    return fieldName;
  }

  public void setFieldName(String fieldName) {
    this.fieldName = fieldName;
  }

  public Direction getDirection() {
    return direction;
  }

  public void setDirection(Direction direction) {
    this.direction = direction;
  }

  public enum Direction {
    asc {
      @Override
      public Order toOrder() {
        return Order.incr;
      }
    }, desc {
      @Override
      public Order toOrder() {
        return Order.decr;
      }
    };

    public abstract Order toOrder();
  }
}
