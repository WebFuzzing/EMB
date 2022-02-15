package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.CollectionFetcher;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto.LazyTypeSubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginatedList;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginationArguments;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;

import java.util.Optional;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.PaginationHelper
  .getPaginatedList;

public class RelationsOfSubjectDataFetcher implements CollectionFetcher {

  private final String source;
  private final String predicate;
  private final Direction direction;

  public RelationsOfSubjectDataFetcher(String source, String predicate, Direction direction) {
    this.source = source;
    this.predicate = predicate;
    this.direction = direction;
  }

  @Override
  public PaginatedList<SubjectReference> getList(PaginationArguments arguments, DataSet dataSet) {
    String cursor = arguments.getCursor();
    try (Stream<CursorQuad> q = dataSet.getQuadStore().getQuads(source, predicate, direction, cursor)) {
      return getPaginatedList(
        q,
        cursorSubject -> {
          final LazyTypeSubjectReference lazyTypeSubjectReference = new LazyTypeSubjectReference(
            cursorSubject.getSubject(),
            dataSet
          );
          return lazyTypeSubjectReference;
        },
        arguments,
        Optional.empty()
      );
    }

  }
}
