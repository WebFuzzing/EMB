package nl.knaw.huygens.timbuctoo.rml.jena;

import nl.knaw.huygens.timbuctoo.rml.DataSource;
import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfLiteral;
import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;
import nl.knaw.huygens.timbuctoo.rml.rmldata.RmlMappingDocument;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.MappingDocumentBuilder;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.PredicateObjectMapBuilder;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.ReferencingObjectMapBuilder;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.SubjectMapBuilder;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.TermMapBuilder;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.TriplesMapBuilder;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static nl.knaw.huygens.timbuctoo.rml.rmldata.RmlMappingDocument.rmlMappingDocument;

//This class reads an RDF graph and calls the appropriate builder methods. It does not do any validation, delegating
// that to the builder itself
public class JenaBasedReader {

  private static final String NS_RR = "http://www.w3.org/ns/r2rml#";
  private static final String NS_RML = "http://semweb.mmlab.be/ns/rml#";

  public RmlMappingDocument fromRdf(Model data, Function<RdfResource, Optional<DataSource>> dataSourceFactory) {
    ResIterator tripleMaps = data.listSubjectsWithProperty(data.createProperty(NS_RR + "subjectMap"));
    MappingDocumentBuilder resultBuilder = rmlMappingDocument();
    try {
      while (tripleMaps.hasNext()) {
        Resource resource = tripleMaps.nextResource();
        buildTripleMap(JenaResource.fromModel(data, resource), resultBuilder.withTripleMap(resource.getURI()));
      }
    } finally {
      tripleMaps.close();
    }
    return resultBuilder.build(dataSourceFactory);
  }

  private void buildTripleMap(RdfResource resource, TriplesMapBuilder mapBuilder) {

    resource.out(NS_RML + "logicalSource").forEach(mapBuilder::withLogicalSource);
    resource.out(NS_RR + "logicalTable").forEach(mapBuilder::withLogicalSource);

    resource.out(NS_RR + "subjectMap").forEach(subjectMap -> {
      buildSubjectMap(
        subjectMap,
        mapBuilder.withSubjectMap()
      );
    });
    resource.out(NS_RR + "predicateObjectMap").forEach(property ->
      buildPredicateObjectMap(
        property,
        mapBuilder.withPredicateObjectMap()
      )
    );
  }

  private void buildPredicateObjectMap(RdfResource object, PredicateObjectMapBuilder builder) {
    object.out(NS_RR + "predicate").forEach(predicate ->
      builder.withPredicate(predicate.asIri().orElseThrow(() -> InvalidRdfResourceException.notAnIri(predicate)))
    );
    object.out(NS_RR + "predicateMap").forEach(templateValue ->
      buildTermMap(templateValue, builder.withPredicateMap())
    );

    object.out(NS_RR + "object").forEach(builder::withObject);
    object.out(NS_RR + "objectMap").forEach(templateValue -> {
      Set<RdfResource> parentTriplesMapDefinitions = templateValue.out(NS_RR + "parentTriplesMap");
      if (parentTriplesMapDefinitions.isEmpty()) {
        buildTermMap(templateValue, builder.withObjectMap());
      } else {
        buildReferencingObjectMap(templateValue, builder.withReference());
      }
    });
  }

  private void buildReferencingObjectMap(RdfResource templateValue, ReferencingObjectMapBuilder builder) {

    templateValue.out(NS_RR + "parentTriplesMap").forEach(x ->
      builder.withParentTriplesMap(x.asIri().orElseThrow(() -> InvalidRdfResourceException.notAnIri(x)))
    );

    templateValue.out(NS_RR + "joinCondition").forEach(x ->
      builder.withJoinCondition(getJoinConditionProp(x, "child"), getJoinConditionProp(x, "parent"))
    );
  }

  private String getJoinConditionProp(RdfResource node, String propName) {
    return node.out(NS_RR + propName).stream()
        .findAny()
        .flatMap(RdfResource::asLiteral)
        .map(RdfLiteral::getValue)
        .orElseThrow(() -> InvalidRdfResourceException.noValue(node));
  }

  private void buildSubjectMap(RdfResource object, SubjectMapBuilder builder) {
    object.out(NS_RR + "class").forEach(builder::withClass);

    buildTermMap(object, builder.withTermMap());
  }

  private void buildTermMap(RdfResource object, TermMapBuilder termMapBuilder) {
    Set<RdfResource> termType = object.out(NS_RR + "termType");
    Set<RdfResource> language = object.out(NS_RR + "language");
    //check if language is only literals and the rest is only Resource
    Set<RdfResource> datatype = object.out(NS_RR + "datatype");

    object.out(NS_RR + "column").forEach(columnValue ->
      termMapBuilder.withColumnTerm(columnValue, termType, language, datatype)
    );
    object.out(NS_RR + "template").forEach(templateValue ->
      termMapBuilder.withTemplateTerm(templateValue, termType, language, datatype)
    );
    object.out(NS_RR + "constant").forEach(templateValue -> {
      termMapBuilder.withConstantTerm(templateValue, termType, language, datatype);
    });

  }

  private static class InvalidRdfResourceException extends RuntimeException {
    public InvalidRdfResourceException(String message) {
      super(message);
    }

    public static InvalidRdfResourceException notAnIri(RdfResource rdfResource) {
      return new InvalidRdfResourceException("\" + " + rdfResource + "\" cannot be represented as an IRI");
    }

    public static InvalidRdfResourceException noValue(RdfResource rdfResource) {
      return new InvalidRdfResourceException("\"" + rdfResource + "\" has no value");
    }
  }

}
