package nl.knaw.huygens.timbuctoo.v5.dropwizard.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sleepycat.je.DatabaseException;
import graphql.ExecutionResult;
import graphql.GraphQL;
import graphql.GraphQLException;
import graphql.schema.GraphQLSchema;
import nl.knaw.huygens.timbuctoo.util.UriHelper;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.contenttypes.SerializerWriter;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.contenttypes.SerializerWriterRegistry;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.RootData;
import nl.knaw.huygens.timbuctoo.v5.graphql.security.PermissionBasedFieldVisibility;
import nl.knaw.huygens.timbuctoo.v5.graphql.security.UserPermissionCheck;
import nl.knaw.huygens.timbuctoo.v5.graphql.serializable.SerializerExecutionStrategy;
import nl.knaw.huygens.timbuctoo.v5.security.PermissionFetcher;
import nl.knaw.huygens.timbuctoo.v5.security.UserValidator;
import nl.knaw.huygens.timbuctoo.v5.security.dto.User;
import nl.knaw.huygens.timbuctoo.v5.security.exceptions.UserValidationException;
import nl.knaw.huygens.timbuctoo.v5.serializable.SerializableResult;
import org.slf4j.LoggerFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import static com.google.common.collect.Sets.newHashSet;
import static graphql.ExecutionInput.newExecutionInput;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData.contextData;

@Path("/v5/graphql")
public class GraphQl {
  private final Supplier<GraphQLSchema> graphqlGetter;
  private final SerializerWriterRegistry serializerWriterRegistry;
  private final UserValidator userValidator;
  private final UriHelper uriHelper;
  private final PermissionFetcher permissionFetcher;
  private final ObjectMapper objectMapper;
  private final DataSetRepository dataSetRepository;

  public GraphQl(Supplier<GraphQLSchema> graphqlGetter, SerializerWriterRegistry serializerWriterRegistry,
                 UserValidator userValidator, UriHelper uriHelper, PermissionFetcher permissionFetcher,
                 DataSetRepository dataSetRepository)
    throws DatabaseException, RdfProcessingFailedException {
    this.graphqlGetter = graphqlGetter;
    this.serializerWriterRegistry = serializerWriterRegistry;
    this.userValidator = userValidator;
    this.uriHelper = uriHelper;
    this.permissionFetcher = permissionFetcher;
    this.dataSetRepository = dataSetRepository;
    objectMapper = new ObjectMapper();
  }

  public URI getUri() {
    return uriHelper.fromResourceUri(UriBuilder.fromResource(GraphQl.class).build());
  }

  @POST
  @Consumes("application/json")
  public Response postJson(JsonNode body, @QueryParam("query") String query,
                           @HeaderParam("accept") String acceptHeader,
                           @QueryParam("accept") String acceptParam,
                           @HeaderParam("Authorization") String authHeader) {
    final String queryFromBody;
    if (body.has("query")) {
      queryFromBody = body.get("query").asText();
    } else {
      queryFromBody = null;
    }
    Map variables = null;
    if (body.has("variables")) {
      try {
        variables = objectMapper.treeToValue(body.get("variables"), HashMap.class);
      } catch (JsonProcessingException e) {
        return Response
          .status(400)
          .entity("'variables' should be an object node")
          .build();
      }
    }
    final String operationName = body.has("operationName") && !body.get("operationName").isNull() ?
      body.get("operationName").asText() :
      null;

    return executeGraphql(query, acceptHeader, acceptParam, queryFromBody, variables, operationName, authHeader);
  }

  @POST
  @Consumes("application/graphql")
  public Response postGraphql(String query, @QueryParam("query") String queryParam,
                              @HeaderParam("accept") String acceptHeader,
                              @QueryParam("accept") String acceptParam,
                              @HeaderParam("Authorization") String authHeader) {
    return executeGraphql(queryParam, acceptHeader, acceptParam, query, null, null, authHeader);
  }

  @GET
  public Response get(@QueryParam("query") String query, @HeaderParam("accept") String acceptHeader,
                      @QueryParam("accept") String acceptParam,
                      @HeaderParam("Authorization") String authHeader) {
    return executeGraphql(null, acceptHeader, acceptParam, query, null, null, authHeader);
  }


  public Response executeGraphql(String query, String acceptHeader, String acceptParam, String queryFromBody,
                                 Map variables, String operationName, String authHeader) {

    final SerializerWriter serializerWriter;
    if (acceptParam != null && !acceptParam.isEmpty()) {
      acceptHeader = acceptParam; //Accept param overrules header because it's more under the user's control
    }
    if (unSpecifiedAcceptHeader(acceptHeader)) {
      acceptHeader = MediaType.APPLICATION_JSON;
    }
    if (MediaType.APPLICATION_JSON.equals(acceptHeader)) {
      serializerWriter = null;
    } else {
      Optional<SerializerWriter> bestMatch = serializerWriterRegistry.getBestMatch(acceptHeader);
      if (bestMatch.isPresent()) {
        serializerWriter = bestMatch.get();
      } else {
        return Response
          .status(415)
          .type(MediaType.APPLICATION_JSON_TYPE)
          .entity("{\"errors\": [\"The available mediatypes are: " +
            String.join(", ", serializerWriterRegistry.getSupportedMimeTypes()) + "\"]}")
          .build();
      }
    }
    if (query != null && queryFromBody != null) {
      return Response
        .status(400)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity("{\"errors\": [\"There's both a query as url paramatere and a query in the body. Please pick one.\"]}")
        .build();
    }
    if (query == null && queryFromBody == null) {
      return Response
        .status(400)
        .type(MediaType.APPLICATION_JSON_TYPE)
        .entity("{\"errors\": [\"Please provide the graphql query as the query property of a JSON encoded object. " +
          "E.g. {query: \\\"{\\n  persons {\\n ... \\\"}\"]}")
        .build();
    }
    Optional<User> user;
    try {
      user = userValidator.getUserFromAccessToken(authHeader);
    } catch (UserValidationException e) {
      user = Optional.empty();
    }

    UserPermissionCheck userPermissionCheck = new UserPermissionCheck(
      user,
      permissionFetcher
    );

    final GraphQLSchema transform = graphqlGetter
      .get()
      .transform(b -> b.fieldVisibility(new PermissionBasedFieldVisibility(userPermissionCheck,dataSetRepository)));
    final GraphQL.Builder builder = GraphQL.newGraphQL(transform);

    if (serializerWriter != null) {
      builder.queryExecutionStrategy(new SerializerExecutionStrategy());
    }

    GraphQL graphQl = builder.build();

    try {
      final ExecutionResult result = graphQl
        .execute(newExecutionInput()
          .root(new RootData(user))
          .context(contextData(userPermissionCheck, user))
          .query(queryFromBody)
          .operationName(operationName)
          .variables(variables == null ? Collections.emptyMap() : variables)
          .build());

      if (serializerWriter == null) {
        return Response
          .ok()
          .type(MediaType.APPLICATION_JSON_TYPE)
          .entity(result.toSpecification())
          .build();
      } else {
        if (result.getErrors() != null && !result.getErrors().isEmpty()) {
          return Response
            .status(415)
            .type(MediaType.APPLICATION_JSON_TYPE)
            .entity(result.toSpecification())
            .build();
        }
        return Response
          .ok()
          .type(serializerWriter.getMimeType())
          .entity((StreamingOutput) os -> {
            serializerWriter.getSerializationFactory().create(os).serialize(new SerializableResult(result.getData()));
          })
          .build();
      }
    } catch (GraphQLException e) {
      LoggerFactory.getLogger(GraphQl.class).error("GraphQL execution failed", e);
      return Response.status(500).entity(jsnO("code", jsn(500), "message", jsn(e.getMessage()))).build();
      // throw e;
    }
  }


  public boolean unSpecifiedAcceptHeader(@HeaderParam("accept") String acceptHeader) {
    return acceptHeader == null || acceptHeader.isEmpty() || "*/*".equals(acceptHeader);
  }

}
