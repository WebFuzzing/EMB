package org.graphqlncs;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

import java.util.List;

public class GraphQLException extends RuntimeException implements GraphQLError {

    String customMessage;

    public GraphQLException(String customMessage) {
        this.customMessage = customMessage;
    }

    @Override
    public String getMessage() {
        return customMessage;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return null;
    }
}
