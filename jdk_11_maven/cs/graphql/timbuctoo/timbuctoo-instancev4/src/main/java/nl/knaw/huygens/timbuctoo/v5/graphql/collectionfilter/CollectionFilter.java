package nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.timbuctoo.util.Tuple;

import java.io.IOException;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface CollectionFilter {
  FilterResult query(String dataSetId, String fieldName, String elasticSearchQuery, String token, int preferredPageSize)
    throws IOException;

  Tuple<Boolean, String> isHealthy();
}
