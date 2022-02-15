package nl.knaw.huygens.timbuctoo.rml.rmldata.builders;

import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;
import nl.knaw.huygens.timbuctoo.rml.rmldata.RrPredicateObjectMapOfTermMap;
import nl.knaw.huygens.timbuctoo.rml.rmldata.RrTriplesMap;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class PredicateObjectMapBuilder {
  private ReferencingObjectMapBuilder referencingObjectMapBuilder;
  private TermMapBuilder objectMap;
  private TermMapBuilder predicateMap;

  PredicateObjectMapBuilder() {}

  public PredicateObjectMapBuilder withObject(RdfResource object) {
    this.objectMap = new TermMapBuilder(true)
      .withConstantTerm(object.asIri().orElseThrow(() -> new RuntimeException("")));
    return this;
  }

  public PredicateObjectMapBuilder withObjectMap(Consumer<TermMapBuilder> subBuilder) {
    subBuilder.accept(withObjectMap());
    return this;
  }

  public TermMapBuilder withObjectMap() {
    this.objectMap = new TermMapBuilder(true);
    return objectMap;
  }

  public PredicateObjectMapBuilder withPredicate(String predicate) {
    this.predicateMap = new TermMapBuilder(false).withConstantTerm(predicate);
    return this;
  }

  public PredicateObjectMapBuilder withPredicateMap(Consumer<TermMapBuilder> subBuilder) {
    subBuilder.accept(withPredicateMap());
    return this;
  }

  public TermMapBuilder withPredicateMap() {
    this.predicateMap = new TermMapBuilder(false);
    return predicateMap;
  }


  public PredicateObjectMapBuilder withReference(Consumer<ReferencingObjectMapBuilder> subBuilder) {
    subBuilder.accept(withReference());
    return this;
  }

  public ReferencingObjectMapBuilder withReference() {
    this.referencingObjectMapBuilder = new ReferencingObjectMapBuilder();
    return this.referencingObjectMapBuilder;
  }

  void build(Function<String, PromisedTriplesMap> getTriplesMap, RrTriplesMap owningTriplesMap)
    throws IOException {
    if (this.referencingObjectMapBuilder != null) {
      this.referencingObjectMapBuilder.build(
        predicateMap,
        getTriplesMap,
        owningTriplesMap
      );
    } else {
      owningTriplesMap.addPredicateObjectMap(
        new RrPredicateObjectMapOfTermMap(this.predicateMap.build(), this.objectMap.build())
      );
    }
  }

  Optional<String> getReferencedMap() {
    return Optional.ofNullable(this.referencingObjectMapBuilder)
                   .map(ReferencingObjectMapBuilder::getReferencedMap);
  }
}
