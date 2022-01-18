package nl.knaw.huygens.timbuctoo.v5.elasticsearch;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assume.assumeTrue;

/**
 * Tests in this class assume an elasticSearch instance at localhost:9200.
 * The instance should have an index with the name 'bank'.
 * See example at
 * https://www.elastic.co/guide/en/elasticsearch/reference/current/_exploring_your_data.html#_loading_the_sample_dataset
 */
@Ignore("This test is handy when working with elastic search. " +
  "This test will fail when you run your own elastic search server." +
  "So for now ignore this test, to make sure no test fails unexpectedly.")
public class ElasticSearchFilterOnLineTest {

  private static final String hostname = "localhost";
  private static final int port = 9200;
  private static final String username = "elastic";
  private static final String password = "changeme";

  private static ElasticSearchFilter eSearch;

  @BeforeClass
  public static void initialize() throws Exception {
    assumeTrue("No host at " + hostname + ":" + port + ", skipping tests." +
      "\nPlease start an ElasticSearch instance at the specified host and port.", hostIsAvailable());
    eSearch = new ElasticSearchFilter(hostname, port, Optional.ofNullable(username), Optional.ofNullable(password));
  }

  private static boolean hostIsAvailable() {
    try (Socket s = new Socket(hostname, port)) {
      return true;
    } catch (IOException ex) {
        /* ignore */
    }
    return false;
  }

  @Test
  public void querySimple() throws Exception {
    String index = "bank";
    String elasticsearchQuery = createQuery1();
    String token = null;
    int preferredPageSize = 3;
    EsFilterResult pageableResult = eSearch.query(index, null, elasticsearchQuery, token, preferredPageSize);

    //System.out.println(pageableResult.getResult());
    token = pageableResult.getNextToken();
    assertThat(token, equalTo("[1447,\"account#490\"]"));
    assertThat(pageableResult.getUriList(), contains("820", "315", "490"));
    assertThat(pageableResult.getTotal(), equalTo(493));

    pageableResult = eSearch.query(index, null, elasticsearchQuery, token, preferredPageSize);

    //System.out.println(pageableResult.getResult());
    token = pageableResult.getNextToken();
    assertThat(token, equalTo("[1696,\"account#159\"]"));
    assertThat(pageableResult.getUriList(), contains("427", "174", "159"));
    assertThat(pageableResult.getTotal(), equalTo(493));
  }

  @Test(expected = org.elasticsearch.client.ResponseException.class)
  public void queryAndIndexDoesNotExist() throws Exception {
    String index = "does_not_exist";
    String elasticsearchQuery = createQuery1();
    int preferredPageSize = 3;
    eSearch.query(index, null, elasticsearchQuery, null, preferredPageSize);
  }

  @Test
  public void queryAndNoResult() throws Exception {
    String index = "bank";
    String elasticsearchQuery = createQuery2();
    String token = null;
    int preferredPageSize = 3;
    EsFilterResult pageableResult = eSearch.query(index, null, elasticsearchQuery, token, preferredPageSize);

    assertThat(pageableResult.getNextToken(), equalTo(null));
    assertThat(pageableResult.getUriList().size(), equalTo(0));
    assertThat(pageableResult.getTotal(), equalTo(0));

    token = pageableResult.getNextToken();
    pageableResult = eSearch.query(index, null, elasticsearchQuery, token, preferredPageSize);

    assertThat(pageableResult.getTotal(), equalTo(0));
  }

  private String createQuery1() {
    return "{\n" +
      "    \"size\": 3,\n" +
      "    \"query\": {\n" +
      "        \"match\" : {\n" +
      "            \"gender\" : \"F\"\n" +
      "        }\n" +
      "    },\n" +
      "    \"sort\": [\n" +
      "        {\"balance\": \"asc\"}\n" +
      "    ]\n" +
      "}";
  }

  private String createQuery2() {
    return "{\n" +
      "    \"size\": 3,\n" +
      "    \"query\": {\n" +
      "        \"match\" : {\n" +
      "            \"gender\" : \"U\"\n" +
      "        }\n" +
      "    },\n" +
      "    \"sort\": [\n" +
      "        {\"balance\": \"asc\"},\n" +
      "        {\"" + ElasticSearchFilter.UNIQUE_FIELD_NAME + "\": \"desc\"}\n" +
      "    ]\n" +
      "}";
  }
}
