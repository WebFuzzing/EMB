package nl.knaw.huygens.timbuctoo.v5.rml;

import nl.knaw.huygens.timbuctoo.rml.DataSource;
import nl.knaw.huygens.timbuctoo.rml.ErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.datasource.JoinHandler;
import nl.knaw.huygens.timbuctoo.rml.datasource.RowFactory;
import nl.knaw.huygens.timbuctoo.v5.datastores.rmldatasource.RmlDataSourceStore;

import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.StringEscapeUtils.unescapeJava;

public class RdfDataSource implements DataSource {
  private final RmlDataSourceStore rmlDataSourceStore;
  private final String collectionUri;
  private final RowFactory rowFactory;
  private final JoinHandler joinHandler;

  public RdfDataSource(RmlDataSourceStore rmlDataSourceStore, String collectionUri, RowFactory rowFactory) {
    this.rmlDataSourceStore = rmlDataSourceStore;
    this.collectionUri = collectionUri;
    this.rowFactory = rowFactory;
    this.joinHandler = this.rowFactory.getJoinHandler();
  }

  @Override
  public Stream<Row> getRows(ErrorHandler defaultErrorHandler) {
    return rmlDataSourceStore
      .get(collectionUri)
      .map(row -> {
        try (Stream<String> values = rmlDataSourceStore.get(row)) {
          return rowFactory.makeRow(
            values.map(value -> value.split("\n", 2)).collect(toMap(
              x -> unescapeJava(x[0]),
              x -> x[1]
            )),
            defaultErrorHandler
          );
        }
      });
  }

  @Override
  public void willBeJoinedOn(String fieldName, String referenceJoinValue, String uri, String outputFieldName) {
    joinHandler.willBeJoinedOn(fieldName, referenceJoinValue, uri, outputFieldName);
  }
}
