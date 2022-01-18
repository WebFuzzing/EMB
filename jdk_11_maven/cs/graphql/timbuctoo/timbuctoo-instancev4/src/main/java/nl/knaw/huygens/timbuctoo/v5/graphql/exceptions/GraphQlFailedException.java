package nl.knaw.huygens.timbuctoo.v5.graphql.exceptions;

import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

public class GraphQlFailedException extends Exception {
  private final List<GraphQLError> errors;

  public GraphQlFailedException(List<GraphQLError> errors) {
    this.errors = errors;
  }

  private List<GraphQLError> getErrors() {
    return errors;
  }

  @Override
  public String getMessage() {
    String result = "";
    for (GraphQLError error : errors) {
      result += error.getErrorType() + ": " + error.getMessage() + "\n";
      for (SourceLocation sourceLocation : error.getLocations()) {
        result += "  at line: " + sourceLocation.getLine() + " column: " + sourceLocation.getColumn();
      }
    }
    return result;
  }
}
