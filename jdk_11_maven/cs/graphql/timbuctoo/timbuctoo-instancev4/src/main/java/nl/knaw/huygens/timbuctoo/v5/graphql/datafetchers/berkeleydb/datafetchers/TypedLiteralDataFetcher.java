package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.TypedValue;

public class TypedLiteralDataFetcher extends WalkTriplesDataFetcher<TypedValue> {

  public TypedLiteralDataFetcher(String predicate) {
    super(predicate, Direction.OUT);
  }

  @Override
  protected TypedValue makeItem(CursorQuad triple, DataSet dataSet) {
    if (triple.getValuetype().isPresent()) {
      return TypedValue.create(triple.getObject(), triple.getValuetype().get(), dataSet);
    } else {
      throw new IllegalStateException("Source is not a triple referencing a value");
    }
  }
}
