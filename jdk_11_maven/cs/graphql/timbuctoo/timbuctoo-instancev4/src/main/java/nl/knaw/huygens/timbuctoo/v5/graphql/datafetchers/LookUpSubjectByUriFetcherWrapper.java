package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;
import org.eclipse.rdf4j.common.net.ParsedURI;

/**
 * Creates a SubjectReference from the passed argument instead of reading it from the database
 */
public class LookUpSubjectByUriFetcherWrapper implements DataFetcher {
  private final String uriArgument;
  private final LookUpSubjectByUriFetcher lookUpSubjectByUriFetcher;

  public LookUpSubjectByUriFetcherWrapper(String uriArgument, LookUpSubjectByUriFetcher lookUpSubjectByUriFetcher) {
    this.uriArgument = uriArgument;
    this.lookUpSubjectByUriFetcher = lookUpSubjectByUriFetcher;
  }

  @Override
  public Object get(DataFetchingEnvironment environment) {
    final DatabaseResult source = environment.getSource();
    ParsedURI baseUri = new ParsedURI(source.getDataSet().getMetadata().getBaseUri());
    String uri = environment.getArgument(uriArgument);
    return lookUpSubjectByUriFetcher.getItem(
      baseUri.resolve(uri).toString(),
      source.getDataSet()
    );
  }
}
