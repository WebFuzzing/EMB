package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter.FilterResult;

import java.io.IOException;

public interface ConfiguredFilter {

  FilterResult query() throws IOException;
}
