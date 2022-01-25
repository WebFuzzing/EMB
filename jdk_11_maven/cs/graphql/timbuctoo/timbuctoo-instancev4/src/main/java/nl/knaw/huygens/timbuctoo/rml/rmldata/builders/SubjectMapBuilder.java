package nl.knaw.huygens.timbuctoo.rml.rmldata.builders;

import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;
import nl.knaw.huygens.timbuctoo.rml.rmldata.RrSubjectMap;
import nl.knaw.huygens.timbuctoo.rml.rmldata.termmaps.RrTermMap;

import java.util.function.Consumer;

public class SubjectMapBuilder {
  private RdfResource classUri;
  private TermMapBuilder termMap;

  SubjectMapBuilder() {
  }

  public SubjectMapBuilder withClass(RdfResource classUri) {
    this.classUri = classUri;
    return this;
  }

  public SubjectMapBuilder withTermMap(Consumer<TermMapBuilder> subBuilder) {
    subBuilder.accept(withTermMap());
    return this;
  }

  public TermMapBuilder withTermMap() {
    this.termMap = new TermMapBuilder(false);
    return termMap;
  }

  RrSubjectMap build(Consumer<PredicateObjectMapBuilder> consumer) {
    RrTermMap termMap = this.termMap.build();
    RrSubjectMap instance = new RrSubjectMap(termMap);
    if (this.classUri != null) {
      consumer.accept(new PredicateObjectMapBuilder()
        .withPredicate("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
        .withObject(this.classUri)
      );
    }
    return instance;
  }

}
