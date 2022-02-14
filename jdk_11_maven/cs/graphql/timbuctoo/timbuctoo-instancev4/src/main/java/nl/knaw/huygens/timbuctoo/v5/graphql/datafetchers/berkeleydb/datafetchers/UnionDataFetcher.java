package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.dto.LazyTypeSubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.TypedValue;

public class UnionDataFetcher extends WalkTriplesDataFetcher<DatabaseResult> {

  public UnionDataFetcher(String predicate, Direction direction) {
    super(predicate, direction);
  }

  @Override
  protected DatabaseResult makeItem(CursorQuad quad, DataSet dataSet) {
    if (quad.getValuetype().isPresent()) {
      return TypedValue.create(quad.getObject(), quad.getValuetype().get(), dataSet);
    } else {
      return new LazyTypeSubjectReference(quad.getObject(), dataSet);
    }
  }
}
