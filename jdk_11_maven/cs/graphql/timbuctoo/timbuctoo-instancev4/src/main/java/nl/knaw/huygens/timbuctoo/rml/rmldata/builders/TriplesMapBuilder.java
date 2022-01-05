package nl.knaw.huygens.timbuctoo.rml.rmldata.builders;

import nl.knaw.huygens.timbuctoo.rml.DataSource;
import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;
import nl.knaw.huygens.timbuctoo.rml.rmldata.RrTriplesMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class TriplesMapBuilder {
  private RdfResource logicalSource;
  private SubjectMapBuilder subjectMapBuilder;
  private List<PredicateObjectMapBuilder> predicateObjectMapBuilders = new ArrayList<>();
  private final String uri;

  TriplesMapBuilder(String uri) {
    this.uri = uri;
  }

  public TriplesMapBuilder withLogicalSource(RdfResource logicalSource) {
    this.logicalSource = logicalSource;
    return this;
  }

  public TriplesMapBuilder withSubjectMap(Consumer<SubjectMapBuilder> subBuilder) {
    subBuilder.accept(withSubjectMap());
    return this;
  }

  public SubjectMapBuilder withSubjectMap() {
    this.subjectMapBuilder = new SubjectMapBuilder();
    return this.subjectMapBuilder;
  }


  public TriplesMapBuilder withPredicateObjectMap(Consumer<PredicateObjectMapBuilder> subBuilder) {
    subBuilder.accept(withPredicateObjectMap());
    return this;
  }

  public PredicateObjectMapBuilder withPredicateObjectMap() {
    final PredicateObjectMapBuilder
      subBuilder = new PredicateObjectMapBuilder();
    this.predicateObjectMapBuilders.add(subBuilder);
    return subBuilder;
  }

  public Set<String> getReferencedTriplesMaps() {
    return predicateObjectMapBuilders.stream()
      .map(PredicateObjectMapBuilder::getReferencedMap)
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toSet());
  }

  RrTriplesMap build(Function<RdfResource, Optional<DataSource>> dataSourceFactory,
                     BiFunction<String, String, PromisedTriplesMap> getTriplesMap, Consumer<String> errorLogger) {


    Optional<DataSource> dataSource = dataSourceFactory.apply(logicalSource);
    if (dataSource.isPresent()) {

      RrTriplesMap instance = new RrTriplesMap(
        subjectMapBuilder.build(x -> predicateObjectMapBuilders.add(x)),
        dataSource.get(),
        uri
      );

      for (PredicateObjectMapBuilder builder : this.predicateObjectMapBuilders) {

        try {
          builder.build(requesteduri -> getTriplesMap.apply(this.uri, requesteduri), instance);
        } catch (IOException e) {
          errorLogger.accept(e.getMessage());
        }
      }
      return instance;
    } else {
      errorLogger.accept("No datasource could be constructed for map " + uri);
      return null;
    }
  }

  public String getUri() {
    return uri;
  }

  public TriplesMapBuilder splitOffDependendingPredObjMaps() {
    TriplesMapBuilder result = new TriplesMapBuilder(this.uri + "/split/" + UUID.randomUUID());
    result.logicalSource = this.logicalSource;
    result.subjectMapBuilder = this.subjectMapBuilder;
    result.predicateObjectMapBuilders = this.predicateObjectMapBuilders;
    this.predicateObjectMapBuilders = new LinkedList<>();
    return result;
  }

  @Override
  public String toString() {
    return "TriplesmapBuilder: " + this.getUri();
  }
}
