package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;

public class CollectionFetcherWrapper implements DataFetcher {
  private final PaginationArgumentsHelper argumentsHelper;
  private final CollectionFetcher fetcher;

  public CollectionFetcherWrapper(PaginationArgumentsHelper argumentsHelper, CollectionFetcher fetcher) {
    this.argumentsHelper = argumentsHelper;
    this.fetcher = fetcher;
  }

  @Override
  public Object get(DataFetchingEnvironment environment) {
    return fetcher.getList(
      argumentsHelper.getPaginationArguments(environment),
      ((DatabaseResult) environment.getSource()).getDataSet()
    );
  }

}
