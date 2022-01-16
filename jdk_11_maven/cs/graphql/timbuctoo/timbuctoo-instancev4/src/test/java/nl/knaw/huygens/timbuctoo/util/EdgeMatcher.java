package nl.knaw.huygens.timbuctoo.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import nl.knaw.huygens.hamcrest.PropertyMatcher;
import nl.knaw.huygens.timbuctoo.model.Change;
import org.apache.tinkerpop.gremlin.structure.Edge;

import java.io.IOException;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;

public class EdgeMatcher extends CompositeMatcher<Edge> {

  private final ObjectMapper objectMapper;

  private EdgeMatcher() {
    objectMapper = new ObjectMapper();
  }

  public static EdgeMatcher likeEdge() {
    return new EdgeMatcher();
  }

  public EdgeMatcher withLabel(String label) {
    this.addMatcher(new PropertyEqualityMatcher<Edge, String>("label", label) {
      @Override
      protected String getItemValue(Edge item) {
        return item.label();
      }
    });
    return this;
  }

  public EdgeMatcher withModifiedTimestamp(long value) {
    this.addMatcher(new PropertyEqualityMatcher<Edge, Long>("modifiedTimeStamp", value) {
      @Override
      protected Long getItemValue(Edge item) {
        try {
          Change modified = objectMapper.readValue(item.<String>value("modified"), Change.class);
          return modified.getTimeStamp();
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    });

    return this;
  }

  public EdgeMatcher withId(String id) {
    this.addMatcher(new PropertyEqualityMatcher<Edge, String>("id", id) {
      @Override
      protected String getItemValue(Edge item) {
        return item.value("tim_id");
      }
    });
    return this;
  }

  public EdgeMatcher withProperty(String propertyName) {
    this.addMatcher(new PropertyMatcher<Edge, Object>(propertyName, notNullValue()) {
      @Override
      protected Object getItemValue(Edge item) {
        return item.property(propertyName).orElse(null);
      }
    });
    return this;
  }

  public EdgeMatcher withProperty(final String name, final Object value) {
    this.addMatcher(new PropertyEqualityMatcher<Edge, Object>(name, value) {
      @Override
      protected Object getItemValue(Edge item) {
        return item.property(name).orElse(null);
      }
    });
    return this;
  }

  public EdgeMatcher withSourceWithId(UUID id) {
    this.addMatcher(new PropertyEqualityMatcher<Edge, String>("id of source vertex", id.toString()) {
      @Override
      protected String getItemValue(Edge item) {
        return item.outVertex().value("tim_id");
      }
    });
    return this;
  }

  public EdgeMatcher withTargetWithId(UUID id) {
    this.addMatcher(new PropertyEqualityMatcher<Edge, String>("id of source vertex", id.toString()) {
      @Override
      protected String getItemValue(Edge item) {
        return item.inVertex().value("tim_id");
      }
    });
    return this;
  }


  public EdgeMatcher withTypeId(UUID id) {
    this.addMatcher(new PropertyEqualityMatcher<Edge, String>("id of target vertex", id.toString()) {
      @Override
      protected String getItemValue(Edge item) {
        return item.value("typeId");
      }
    });
    return this;
  }
}
