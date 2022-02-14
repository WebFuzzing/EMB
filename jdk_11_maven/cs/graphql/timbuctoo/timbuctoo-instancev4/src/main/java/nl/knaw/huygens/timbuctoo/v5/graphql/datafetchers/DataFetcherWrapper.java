package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;

public class DataFetcherWrapper implements DataFetcher {
  private final PaginationArgumentsHelper argumentsHelper;
  private final boolean isList;
  private final RelatedDataFetcher inner;

  public DataFetcherWrapper(PaginationArgumentsHelper argumentsHelper, boolean isList, RelatedDataFetcher inner) {
    this.argumentsHelper = argumentsHelper;
    this.isList = isList;
    this.inner = inner;
  }

  @Override
  public Object get(DataFetchingEnvironment environment) {
    if (environment.getSource() instanceof SubjectReference) {
      SubjectReference source = environment.getSource();
      if (isList) {
        return inner.getList(
          source,
          argumentsHelper.getPaginationArguments(environment),
          ((DatabaseResult) environment.getSource()).getDataSet()
        );
      } else {
        return inner.getItem(source, ((DatabaseResult) environment.getSource()).getDataSet());
      }
    } else {
      throw new IllegalStateException("Source is not a SubjectReference");
    }
  }

}
