package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter.FilterResult;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.CollectionFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto.LazyTypeSubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginatedList;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginationArguments;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.IN;
import static nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.PaginationHelper.getPaginatedList;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;

public class CollectionDataFetcher implements CollectionFetcher {
  private static final Logger LOG = LoggerFactory.getLogger(CollectionDataFetcher.class);

  private final String collectionUri;

  public CollectionDataFetcher(String collectionUri) {
    this.collectionUri = collectionUri;
  }

  @Override
  public PaginatedList<SubjectReference> getList(PaginationArguments arguments, DataSet dataSet) {
    String cursor = arguments.getCursor();
    if (arguments.getFilter().isPresent()) {
      try {
        final FilterResult result = arguments.getFilter().get().query();
        return PaginatedList.create(
          result.getPrevToken(),
          result.getNextToken(),
          result.getUriList().stream().map(x -> new LazyTypeSubjectReference(x, dataSet)).collect(Collectors.toList()),
          Optional.of((long) result.getTotal()),
          result.getFacets()
        );
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    } else {
      try (Stream<CursorQuad> subjectStream = dataSet.getQuadStore().getQuads(collectionUri, RDF_TYPE, IN, cursor)) {
        Optional<Long> total = Optional.empty();
        if (dataSet.getSchemaStore().getStableTypes() != null &&
          dataSet.getSchemaStore().getStableTypes().get(collectionUri) != null) {

          total = Optional.of(
            dataSet.getSchemaStore().getStableTypes().get(collectionUri).getSubjectsWithThisType()
          );
        }
        return getPaginatedList(
          subjectStream,
          cursorSubject -> new LazyTypeSubjectReference(cursorSubject.getObject(), dataSet),
          arguments,
          total
        );
      }
    }
  }

}
