package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import com.google.common.collect.Lists;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.PaginationArgumentsHelper;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto.LazyTypeSubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginatedDynamicList;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginatedList;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginationArguments;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.TypedValue;

import java.util.Optional;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.IN;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.OUT;
import static nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers.PaginationHelper
  .getPaginatedList;

public class DynamicRelationDataFetcher implements DataFetcher<PaginatedDynamicList> {
  private final PaginationArgumentsHelper argumentsHelper;

  public DynamicRelationDataFetcher(PaginationArgumentsHelper argumentsHelper) {
    this.argumentsHelper = argumentsHelper;
  }

  private DatabaseResult makeItem(CursorQuad triple, DataSet dataSet) {
    if (triple.getValuetype().isPresent()) {
      return TypedValue.create(triple.getObject(), triple.getValuetype().get(), dataSet);
    } else {
      return new LazyTypeSubjectReference(triple.getObject(), dataSet);
    }
  }

  @Override
  public PaginatedDynamicList get(DataFetchingEnvironment environment) {
    if (environment.getSource() instanceof SubjectReference) {
      SubjectReference source = environment.getSource();
      PaginationArguments arguments = argumentsHelper.getPaginationArguments(environment);
      DataSet dataSet = ((DatabaseResult) environment.getSource()).getDataSet();
      String cursor = arguments.getCursor();
      String predicate = environment.getArgument("uri");
      Direction direction = environment.getArgument("outgoing") ? OUT : IN;

      try (Stream<CursorQuad> q = dataSet.getQuadStore()
        .getQuads(source.getSubjectUri(), predicate, direction, cursor)) {
        final PaginatedList<DatabaseResult> paginatedList = getPaginatedList(
          q,
          qd -> this.makeItem(qd, dataSet),
          arguments,
          Optional.empty()
        );
        return PaginatedDynamicList.create(
          paginatedList.getPrevCursor(),
          paginatedList.getNextCursor(),
          paginatedList.getItems()
        );
      }
    } else {
      return PaginatedDynamicList.create(
        Optional.empty(),
        Optional.empty(),
        Lists.newArrayList()
      );
    }
  }
}
