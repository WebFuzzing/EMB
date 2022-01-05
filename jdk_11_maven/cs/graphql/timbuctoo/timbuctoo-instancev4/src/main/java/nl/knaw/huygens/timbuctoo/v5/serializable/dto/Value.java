package nl.knaw.huygens.timbuctoo.v5.serializable.dto;

import org.slf4j.Logger;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents a leaf node that contains the actual data. In the graphql query it's the object that is represented by
 * {value}
 */
@org.immutables.value.Value.Immutable
public interface Value extends RdfData {
  Logger LOG = getLogger(Value.class);

  String getType();

  String getValue();

  Optional<String> getGraphqlTypeName();

  static Value create(String value, String type) {
    return ImmutableValue.builder()
      .value(value)
      .type(type)
      .build();
  }

  static Value create(String value, String type, String graphqlType) {
    return ImmutableValue.builder()
      .value(value)
      .type(type)
      .graphqlTypeName(graphqlType)
      .build();
  }

  default Serializable withGraphqlType(String graphqlType) {
    return ImmutableValue.copyOf(this).withGraphqlTypeName(graphqlType);
  }

  static Value fromRawJavaType(Object value) {
    if (value == null) {
      return null;
    } else if (value instanceof Integer) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#int");
    } else if (value instanceof Long) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#long");
    } else if (value instanceof Short) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#short");
    } else if (value instanceof Byte) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#byte");
    } else if (value instanceof Double) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#double");
    } else if (value instanceof BigInteger) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#integer");
    } else if (value instanceof BigDecimal) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#decimal");
    } else if (value instanceof String) {
      return create((String) value, "http://www.w3.org/2001/XMLSchema#string");
    } else if (value instanceof Boolean) {
      return create((Boolean) value ? "true" : "false", "http://www.w3.org/2001/XMLSchema#boolean");
    } else if (value instanceof Character) {
      return create(value + "", "http://www.w3.org/2001/XMLSchema#string");
    } else {
      LOG.error("Unknown type: " + value.getClass());
      return create(value + "", "http://www.w3.org/2001/XMLSchema#string");
    }
  }
}
