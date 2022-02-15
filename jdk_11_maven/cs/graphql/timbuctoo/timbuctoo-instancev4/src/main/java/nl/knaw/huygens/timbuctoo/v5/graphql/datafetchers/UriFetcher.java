package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;

public class UriFetcher implements DataFetcher {

  @Override
  public Object get(DataFetchingEnvironment environment) {
    if (environment.getSource() instanceof SubjectReference) {
      return ((SubjectReference) environment.getSource()).getSubjectUri();
    } else {
      throw new IllegalStateException("Source is not a SubjectReference");
    }
  }
}
