package nl.knaw.huygens.timbuctoo.v5.elasticsearch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * Created on 2017-10-04 11:13.
 */
public class FilterResultTest {

  private final ObjectMapper mapper = new ObjectMapper();

  @Test
  public void readResult() throws Exception {
    EsFilterResult pageableResult = new EsFilterResult(createQuery(), createResult(), "_id");

    String expectedQuery = "{\"size\":3,\"query\":{\"match\":{\"gender\":\"F\"}},\"search_after\":[1464," +
      "\"account#174\"],\"sort\":[{\"balance\":\"asc\"},{\"_uid\":\"desc\"}]}";
    assertThat(pageableResult.getQuery(), equalTo(expectedQuery));

    String expectedResultStart = "{\"took\":25,\"timed_out\":false";
    assertThat(pageableResult.getResult().startsWith(expectedResultStart), equalTo(true));

    assertThat(pageableResult.getUriList().size(), equalTo(3));
    // System.out.println(pageableResult.getIdList());
    assertThat(pageableResult.getUriList(), contains("348", "490", "427"));

    String expectedToken = "3";
    assertThat(pageableResult.getNextToken(), equalTo(expectedToken));

    assertThat(pageableResult.getTotal(), equalTo(1000));
    assertThat(pageableResult.getSearchTime(), equalTo(25));
  }

  @Test
  public void readEmptyResult() throws Exception {
    EsFilterResult pageableResult = new EsFilterResult(createQuery(), createEmptyResult(), "_id");

    assertThat(pageableResult.getUriList().size(), equalTo(0));
    assertThat(pageableResult.getNextToken(), equalTo(null));
    assertThat(pageableResult.getTotal(), equalTo(0));
    assertThat(pageableResult.getSearchTime(), equalTo(5));
  }

  private JsonNode createQuery() throws IOException {
    String queryString = "{\n" +
      "    \"size\": 3,\n" +
      "    \"query\": {\n" +
      "        \"match\" : {\n" +
      "            \"gender\" : \"F\"\n" +
      "        }\n" +
      "    },\n" +
      "    \"search_after\": [1464, \"account#174\"],\n" +
      "    \"sort\": [\n" +
      "        {\"balance\": \"asc\"},\n" +
      "        {\"_uid\": \"desc\"}\n" +
      "    ]\n" +
      "}";
    return mapper.readTree(queryString);
  }

  private JsonNode createResult() throws IOException {
    String resultString = "{\n" +
      "  \"took\" : 25,\n" +
      "  \"timed_out\" : false,\n" +
      "  \"_shards\" : {\n" +
      "    \"total\" : 5,\n" +
      "    \"successful\" : 5,\n" +
      "    \"skipped\" : 0,\n" +
      "    \"failed\" : 0\n" +
      "  },\n" +
      "  \"hits\" : {\n" +
      "    \"total\" : 1000,\n" +
      "    \"max_score\" : null,\n" +
      "    \"hits\" : [\n" +
      "      {\n" +
      "        \"_index\" : \"bank\",\n" +
      "        \"_type\" : \"account\",\n" +
      "        \"_id\" : \"348\",\n" +
      "        \"_score\" : null,\n" +
      "        \"_source\" : {\n" +
      "          \"account_number\" : 348,\n" +
      "          \"balance\" : 1360,\n" +
      "          \"firstname\" : \"Karina\",\n" +
      "          \"lastname\" : \"Russell\",\n" +
      "          \"age\" : 37,\n" +
      "          \"gender\" : \"M\",\n" +
      "          \"address\" : \"797 Moffat Street\",\n" +
      "          \"employer\" : \"Limozen\",\n" +
      "          \"email\" : \"karinarussell@limozen.com\",\n" +
      "          \"city\" : \"Riegelwood\",\n" +
      "          \"state\" : \"RI\"\n" +
      "        },\n" +
      "        \"sort\" : [\n" +
      "          1360,\n" +
      "          \"account#348\"\n" +
      "        ]\n" +
      "      },\n" +
      "      {\n" +
      "        \"_index\" : \"bank\",\n" +
      "        \"_type\" : \"account\",\n" +
      "        \"_id\" : \"490\",\n" +
      "        \"_score\" : null,\n" +
      "        \"_source\" : {\n" +
      "          \"account_number\" : 490,\n" +
      "          \"balance\" : 1447,\n" +
      "          \"firstname\" : \"Strong\",\n" +
      "          \"lastname\" : \"Hendrix\",\n" +
      "          \"age\" : 26,\n" +
      "          \"gender\" : \"F\",\n" +
      "          \"address\" : \"134 Beach Place\",\n" +
      "          \"employer\" : \"Duoflex\",\n" +
      "          \"email\" : \"stronghendrix@duoflex.com\",\n" +
      "          \"city\" : \"Allentown\",\n" +
      "          \"state\" : \"ND\"\n" +
      "        },\n" +
      "        \"sort\" : [\n" +
      "          1447,\n" +
      "          \"account#490\"\n" +
      "        ]\n" +
      "      },\n" +
      "      {\n" +
      "        \"_index\" : \"bank\",\n" +
      "        \"_type\" : \"account\",\n" +
      "        \"_id\" : \"427\",\n" +
      "        \"_score\" : null,\n" +
      "        \"_source\" : {\n" +
      "          \"account_number\" : 427,\n" +
      "          \"balance\" : 1463,\n" +
      "          \"firstname\" : \"Rebekah\",\n" +
      "          \"lastname\" : \"Garrison\",\n" +
      "          \"age\" : 36,\n" +
      "          \"gender\" : \"F\",\n" +
      "          \"address\" : \"837 Hampton Avenue\",\n" +
      "          \"employer\" : \"Niquent\",\n" +
      "          \"email\" : \"rebekahgarrison@niquent.com\",\n" +
      "          \"city\" : \"Zarephath\",\n" +
      "          \"state\" : \"NY\"\n" +
      "        },\n" +
      "        \"sort\" : [\n" +
      "          1463,\n" +
      "          \"account#427\"\n" +
      "        ]\n" +
      "      }\n" +
      "    ]\n" +
      "  }\n" +
      "}\n";
    return mapper.readTree(resultString);
  }

  private JsonNode createEmptyResult() throws IOException {
    String resultString = "{\n" +
      "  \"took\" : 5,\n" +
      "  \"timed_out\" : false,\n" +
      "  \"_shards\" : {\n" +
      "    \"total\" : 5,\n" +
      "    \"successful\" : 5,\n" +
      "    \"skipped\" : 0,\n" +
      "    \"failed\" : 0\n" +
      "  },\n" +
      "  \"hits\" : {\n" +
      "    \"total\" : 0,\n" +
      "    \"max_score\" : null,\n" +
      "    \"hits\" : [ ]\n" +
      "  }\n" +
      "}";
    return mapper.readTree(resultString);
  }

}
