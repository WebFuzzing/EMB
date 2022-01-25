package nl.knaw.huygens.timbuctoo.v5.graphql;

import graphql.language.StringValue;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;

import java.util.Optional;

public class DirectiveRetriever {
  public static Optional<String> getDirectiveArgument(GraphQLObjectType parentType, String directiveName,
                                                      String argumentName) {
    return Optional.ofNullable(parentType.getDefinition().getDirective(directiveName))
      .map(d -> d.getArgument(argumentName))
      .map(v -> (StringValue) v.getValue())
      .map(StringValue::getValue);
  }

  public static Optional<String> getDirectiveArgument(GraphQLFieldDefinition field, String directiveName,
                                                      String argumentName) {
    return Optional.ofNullable(field.getDefinition().getDirective(directiveName))
      .map(d -> d.getArgument(argumentName))
      .map(v -> (StringValue) v.getValue())
      .map(StringValue::getValue);
  }
}
