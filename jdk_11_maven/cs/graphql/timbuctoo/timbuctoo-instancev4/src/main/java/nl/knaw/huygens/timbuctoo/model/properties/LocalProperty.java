package nl.knaw.huygens.timbuctoo.model.properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import nl.knaw.huygens.timbuctoo.model.properties.converters.Converter;
import nl.knaw.huygens.timbuctoo.model.properties.converters.HasOptions;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.Optional;

public class LocalProperty extends ReadableProperty {
  private static final Logger LOG = LoggerFactory.getLogger(ReadableProperty.class);
  public static final String DATABASE_PROPERTY_NAME = "dbName";
  public static final String OPTIONS_PROPERTY_NAME = "options";

  private final String propName;
  private final Converter converter;

  public LocalProperty(String propName, Converter converter) {
    super(() -> __.<Object, String>values(propName).map(prop -> Try.of(() -> converter.tinkerpopToJson(prop.get()))),
      () -> __.values(propName).map(prop -> Try.of(prop::get)));
    this.propName = propName;
    this.converter = converter;
  }

  public String getDatabasePropertyName() {
    return propName;
  }

  public Optional<Collection<String>> getOptions() {
    if (converter instanceof HasOptions) {
      return Optional.of(((HasOptions) converter).getOptions());
    } else {
      return Optional.empty();
    }
  }

  public Optional<Collection<String>> getParts() {
    if (converter instanceof HasParts) {
      return Optional.of(((HasParts) converter).getParts());
    } else {
      return Optional.empty();
    }
  }

  public void setJson(Vertex vertex, JsonNode value) throws IOException {
    if (value == null) {
      vertex.property(propName).remove();
    } else {
      vertex.property(propName, converter.jsonToTinkerpop(value));
    }
  }

  public void setValue(Vertex vertex, Object value) throws IOException {
    converter.validate(value);
    if (value == null) {
      vertex.property(propName).remove();
    } else {
      vertex.property(propName, value);
    }
  }

  @Override
  public String getTypeId() {
    return converter.getGuiTypeId();
  }

  @Override
  public String getUniqueTypeId() {
    return converter.getUniqueTypeIdentifier();
  }

  @Override
  public Vertex save(Graph graph, String clientPropertyName) {
    Vertex propertyVertex = super.save(graph, clientPropertyName);

    propertyVertex.property(DATABASE_PROPERTY_NAME, propName);

    if (converter instanceof HasOptions) {
      try {
        propertyVertex.property(OPTIONS_PROPERTY_NAME,
          new ObjectMapper().writeValueAsString(((HasOptions) converter).getOptions()));
      } catch (JsonProcessingException e) {
        LOG.error("Failed to write options to database for property {}", propName);
      }
    }
    return propertyVertex;
  }
}
