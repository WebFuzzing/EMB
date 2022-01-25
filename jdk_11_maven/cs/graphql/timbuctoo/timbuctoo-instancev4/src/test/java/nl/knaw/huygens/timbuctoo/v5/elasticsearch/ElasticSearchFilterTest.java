package nl.knaw.huygens.timbuctoo.v5.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Iterator;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ElasticSearchFilterTest {

  private static ElasticSearchFilter eSearch;

  @BeforeClass
  public static void initialize() throws Exception {
    eSearch = new ElasticSearchFilter("not-existing-host", 0, Optional.of(""), Optional.empty());
  }

  @Test
  public void elaborateEmptyQuery() throws Exception {
    // empty query with and without from.
    String query = "{}";
    String expectedSort = "{\"" + ElasticSearchFilter.UNIQUE_FIELD_NAME + "\":\"desc\"}";

    JsonNode node = eSearch.elaborateQuery(query, null, 5);

    JsonNode sizeNode = node.findValue("size");
    assertThat(sizeNode.asInt(), equalTo(5));
    assertThat(node.has("from"), equalTo(false));
    JsonNode sortNode = node.findValue("sort");
    assertThat(sortNode.elements().next().toString(), equalTo(expectedSort));
    String expected = "{\"size\":5,\"sort\":[" + expectedSort + "]}";
    assertThat(node.toString(), equalTo(expected));

    // Add from to query
    expected = "{\"size\":6,\"from\":123,\"sort\":[" + expectedSort + "]}";
    node = eSearch.elaborateQuery(query, "123", 6);

    sizeNode = node.findValue("size");
    assertThat(sizeNode.asInt(), equalTo(6));
    JsonNode from = node.findValue("from");
    assertThat(from.asInt(), equalTo(123));
    sortNode = node.findValue("sort");
    assertThat(sortNode.elements().next().toString(), equalTo(expectedSort));
    assertThat(node.toString(), equalTo(expected));
  }

  @Test
  public void elaborateCompleteQuery() throws Exception {
    String query = "{\n" +
        "    \"size\": 3,\n" +
        "    \"query\": {\n" +
        "        \"match\" : {\n" +
        "            \"gender\" : \"F\"\n" +
        "        }\n" +
        "    },\n" +
        "    \"sort\": [\n" +
        "        {\"balance\": \"asc\"},\n" +
        "        {\"" + ElasticSearchFilter.UNIQUE_FIELD_NAME + "\": \"desc\"}\n" +
        "    ]\n" +
        "}";
    testElaborate(query);
  }

  @Test
  public void elaborateIncompleteQuery() throws Exception {
    // missing unique field.
    String query = "{\n" +
        "    \"size\": 3,\n" +
        "    \"query\": {\n" +
        "        \"match\" : {\n" +
        "            \"gender\" : \"F\"\n" +
        "        }\n" +
        "    },\n" +
        "    \"sort\": [\n" +
        "        {\"balance\": \"asc\"}\n" +
        "    ],\n" +
        "    \"from\": 1314\n" +
        "}";
    testElaborate(query);
  }

  private void testElaborate(String query) throws Exception {
    // test elaborate with and without token.

    JsonNode node = eSearch.elaborateQuery(query, null, 5);

    JsonNode sizeNode = node.findValue("size");
    assertThat(sizeNode.asInt(), equalTo(5));
    assertThat(node.has("search_after"), equalTo(false));
    JsonNode sortNode = node.findValue("sort");
    Iterator<JsonNode> sortIter = sortNode.elements();
    String expectedSort1 = "{\"balance\":\"asc\"}";
    String expectedSort2 = "{\"" + ElasticSearchFilter.UNIQUE_FIELD_NAME + "\":\"desc\"}";
    assertThat(sortIter.next().toString(), equalTo(expectedSort1));
    assertThat(sortIter.next().toString(), equalTo(expectedSort2));
    String expectedSort = expectedSort1 + "," + expectedSort2;
    String expected = "{\"size\":5,\"query\":{\"match\":{\"gender\":\"F\"}},\"sort\":[" + expectedSort + "]}";
    assertThat(node.toString(), equalTo(expected));

    expected = "{\"size\":6,\"query\":{\"match\":{\"gender\":\"F\"}},\"sort\":[" + expectedSort + "]," +
        "\"from\":49223}";
    node = eSearch.elaborateQuery(query, "49223", 6);

    sizeNode = node.findValue("size");
    assertThat(sizeNode.asInt(), equalTo(6));
    JsonNode searchAfterNode = node.findValue("from");
    assertThat(searchAfterNode.toString(), equalTo("49223"));
    sortNode = node.findValue("sort");
    sortIter = sortNode.elements();
    assertThat(sortIter.next().toString(), equalTo(expectedSort1));
    assertThat(sortIter.next().toString(), equalTo(expectedSort2));
    assertThat(node.toString(), equalTo(expected));
  }

}

