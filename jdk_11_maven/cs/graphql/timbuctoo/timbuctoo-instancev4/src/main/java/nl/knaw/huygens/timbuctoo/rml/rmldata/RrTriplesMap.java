package nl.knaw.huygens.timbuctoo.rml.rmldata;

import nl.knaw.huygens.timbuctoo.rml.DataSource;
import nl.knaw.huygens.timbuctoo.rml.ErrorHandler;
import nl.knaw.huygens.timbuctoo.rml.Row;
import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps.RrRefObjectMap;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RrTriplesMap {
  private final List<Tuple<RrRefObjectMap, String>> subscriptions = new ArrayList<>();
  private final RrSubjectMap subjectMap;
  private final List<RrPredicateObjectMap> predicateObjectMaps;
  private final DataSource dataSource;
  private final String uri;

  public RrTriplesMap(RrSubjectMap subjectMap, DataSource dataSource, String uri) {
    this.subjectMap = subjectMap;
    this.predicateObjectMaps = new ArrayList<>();
    this.dataSource = dataSource;
    this.uri = uri;
  }

  public String getUri() {
    return uri;
  }

  public void addPredicateObjectMap(RrPredicateObjectMap objectMap) {
    predicateObjectMaps.add(objectMap);
  }

  public void subscribeToSubjectsWith(RrRefObjectMap subscriber, String fieldName) {
    subscriptions.add(Tuple.tuple(subscriber, fieldName));
  }

  Stream<Quad> getItems(ErrorHandler defaultErrorHandler) {
    final int[] numberOfItemsProcessed = new int[1];

    Stream<Quad> quadStream = dataSource.getRows(defaultErrorHandler)
      .peek(e -> {
        numberOfItemsProcessed[0]++;
        if  (numberOfItemsProcessed[0] == 1) {
          LoggerFactory.getLogger(RrTriplesMap.class).info("collection '{}' has at least one item", uri);
        }
      }).flatMap(row -> {
        Optional<RdfUri> subjectOpt = subjectMap.generateValue(row);

        if (subjectOpt.isPresent()) {
          RdfUri subject = subjectOpt.get();
          for (Tuple<RrRefObjectMap, String> subscription : subscriptions) {
            subscription.getLeft().onNewSubject(row.getRawValue(subscription.getRight()), subject);
          }

          return predicateObjectMaps.stream()
                                    .flatMap(predicateObjectMap -> predicateObjectMap.generateValue(subject, row));

        } else {
          defaultErrorHandler.subjectGenerationFailed(uri, row);
          return Stream.empty();
        }
      });

    return quadStream;
  }

  public DataSource getDataSource() {
    return dataSource;
  }

  @Override
  public String toString() {
    return String.format("  TriplesMap: \n  uri: %s\n  datasource:\n%s  subjectMap:\n%s  predicateObjectMaps:\n%s",
      uri,
      this.dataSource,
      this.subjectMap,
      String.join("", this.predicateObjectMaps.stream().map(x -> String.format("%s", x)).collect(Collectors.toList()))
    );
  }

}
