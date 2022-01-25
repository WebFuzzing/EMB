package nl.knaw.huygens.timbuctoo.v5.elasticsearch;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter.CollectionFilter;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

public class ElasticSearchFilter implements CollectionFilter {

  public static final String UNIQUE_FIELD_NAME = "_uid";

  private static final String METHOD_GET = "GET";
  public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  public static final Logger LOG = LoggerFactory.getLogger(ElasticSearchFilter.class);

  private final RestClient restClient;
  private final ObjectMapper mapper;

  @JsonCreator
  public ElasticSearchFilter(@JsonProperty("hostname") String hostname, @JsonProperty("port") int port,
                             @JsonProperty("username") Optional<String> username,
                             @JsonProperty("password") Optional<String> password) {
    Header[] headers = {
      new BasicHeader(HttpHeaders.CONTENT_TYPE, "application/json"),
      new BasicHeader("Role", "Read")};
    final RestClientBuilder restClientBuilder = RestClient.builder(new HttpHost(hostname, port))
      .setDefaultHeaders(headers);
    if (username.isPresent() && !username.get().isEmpty() && password.isPresent() && !password.get().isEmpty()) {
      final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

      credentialsProvider.setCredentials(
        AuthScope.ANY,
        new UsernamePasswordCredentials(username.get(), password.get())
      );

      restClientBuilder.setHttpClientConfigCallback(b -> b.setDefaultCredentialsProvider(credentialsProvider));
    }
    restClient = restClientBuilder.build();
    mapper = new ObjectMapper();
  }

  @Override
  public EsFilterResult query(String dataSetId, String fieldName, String elasticSearchQuery, String token,
                              int preferredPageSize) throws IOException {
    String endpoint = dataSetId + (fieldName != null && !fieldName.isEmpty() ? "/" + fieldName : "") + "/_search";
    JsonNode queryNode = elaborateQuery(elasticSearchQuery, token, preferredPageSize);
    Map<String, String> params = Collections.singletonMap("pretty", "true");

    HttpEntity entity = new NStringEntity(queryNode.toString(), ContentType.APPLICATION_JSON);
    Response response = restClient.performRequest(METHOD_GET, endpoint, params, entity);

    JsonNode responseNode = mapper.readTree(response.getEntity().getContent());
    return new EsFilterResult(queryNode, responseNode);
  }

  @Override
  public Tuple<Boolean, String> isHealthy() {
    try {
      final Response response = restClient.performRequest(METHOD_GET, "_cluster/health");
      final StatusLine statusLine = response.getStatusLine();
      if (statusLine.getStatusCode() != 200) {
        return Tuple.tuple(false, "Request failed: " + statusLine.getReasonPhrase());
      }

      final JsonNode jsonNode = OBJECT_MAPPER.readTree(response.getEntity().getContent());
      final String status = jsonNode.get("status").asText();
      if (status.equals("red")) {
        return Tuple.tuple(false, "Elasticsearch cluster status is 'red'.");
      }

      return Tuple.tuple(true, "Elasticsearch filter is healthy.");
    } catch (IOException e) {
      LOG.error("Elasticsearch request failed", e);
      return Tuple.tuple(false, "Request threw an exception: " + e.getMessage());
    }
  }

  protected ObjectNode elaborateQuery(String elasticSearchQuery, String fromValue, int preferredPageSize)
    throws IOException {
    try {
      ObjectNode node = (ObjectNode) mapper.readTree(elasticSearchQuery);

      // size -1 gives the default 10 results. size 0 gives 0 results. totals are always given.
      // requests without a 'query' clause are legal, so don't check.
      // if 'search_after' is present, 'sort' must contain just as many fields of same type (not checked).
      // 'sort' must be present and must contain "..one unique value per document.." (we check on/put
      // UNIQUE_FIELD_NAME).
      node.put("size", preferredPageSize);

      // from
      if (fromValue != null && !fromValue.isEmpty()) {
        node.set("from", new IntNode(getFrom(fromValue)));
      } else {
        node.remove("from");
      }

      // sort
      ArrayNode sortNode = (ArrayNode) node.findValue("sort");
      if (sortNode == null) {
        sortNode = node.putArray("sort");
      }
      if (sortNode.findValue(UNIQUE_FIELD_NAME) == null) {
        ObjectNode objNode = JsonNodeFactory.instance.objectNode();
        objNode.put(UNIQUE_FIELD_NAME, "desc");
        sortNode.add(objNode);
      }
      return node;
    } catch (IOException e) {
      throw new IOException("Elasticsearch query is not a wellformed JSON document", e);
    }
  }

  private int getFrom(String token) {
    try {
      return Integer.parseInt(token);
    } catch (IllegalArgumentException ex) {
      LOG.error("Token not a number", ex);
    }
    return 0;
  }


}
