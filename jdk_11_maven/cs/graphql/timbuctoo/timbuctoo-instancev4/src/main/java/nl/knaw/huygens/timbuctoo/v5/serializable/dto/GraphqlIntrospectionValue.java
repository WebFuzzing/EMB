package nl.knaw.huygens.timbuctoo.v5.serializable.dto;

import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Represents a leaf node that contains the actual data. In the graphql query it's the object that is represented by
 * {value}
 */
@org.immutables.value.Value.Immutable
public interface GraphqlIntrospectionValue extends RdfData {
  Logger LOG = getLogger(GraphqlIntrospectionValue.class);

  Object getValue();

  static GraphqlIntrospectionValue create(Object value) {
    return ImmutableGraphqlIntrospectionValue.builder()
      .value(value)
      .build();
  }

  static GraphqlIntrospectionValue fromRawJavaType(Object value) {
    if (value == null) {
      return null;
    } else {
      return create(value);
    }
  }
}
