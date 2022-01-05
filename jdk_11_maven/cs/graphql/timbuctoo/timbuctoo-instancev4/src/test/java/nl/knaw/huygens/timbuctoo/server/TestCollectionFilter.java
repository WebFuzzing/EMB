package nl.knaw.huygens.timbuctoo.server;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter.CollectionFilter;
import nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter.FilterResult;

import java.io.IOException;
import java.util.Optional;

// Used as replacement filter in the DropwizrardLaunchesTest
public class TestCollectionFilter implements CollectionFilter {

  @JsonCreator
  public TestCollectionFilter(@JsonProperty("hostname") String hostname, @JsonProperty("port") int port,
                             @JsonProperty("username") Optional<String> username,
                             @JsonProperty("password") Optional<String> password) {

  }

  @Override
  public FilterResult query(String dataSetId, String fieldName, String elasticSearchQuery, String token,
                            int preferredPageSize) throws IOException {
    throw new UnsupportedOperationException("Not yet implemented");//FIXME: implement
  }

  @Override
  public Tuple<Boolean, String> isHealthy() {
    return Tuple.tuple(true, "");
  }
}
