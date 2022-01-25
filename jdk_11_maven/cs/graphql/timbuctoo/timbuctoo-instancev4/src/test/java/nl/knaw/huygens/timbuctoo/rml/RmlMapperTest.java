package nl.knaw.huygens.timbuctoo.rml;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.rml.dto.Quad;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfUri;
import nl.knaw.huygens.timbuctoo.rml.dto.RdfValue;
import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfLiteral;
import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;
import nl.knaw.huygens.timbuctoo.rml.rmldata.RmlMappingDocument;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.TriplesMapBuilder;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.rml.TripleMatcher.ANY;
import static nl.knaw.huygens.timbuctoo.rml.TripleMatcher.likeTriple;
import static nl.knaw.huygens.timbuctoo.rml.rmldata.RmlMappingDocument.rmlMappingDocument;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RmlMapperTest {

  public static final String EXAMPLE_CLASS = "http://example.org/someClass";

  private static RdfUri uri(String uri) {
    return new RdfUri(uri);
  }

  private static RdfValue literal(String value) {
    return new RdfValue(value, "http://www.w3.org/2001/XMLSchema#string");
  }

  @Test
  public void generatesRdfTypeTriplesForSubjectMapsWithRrClass() {
    DataSource input = new TestDataSource(Lists.newArrayList(Maps.newHashMap()));

    List<Quad> result = rmlMappingDocument()
      .withTripleMap("http://example.org/mapping1", trip -> trip
        .withSubjectMap(sm -> sm
          .withClass(rdf(EXAMPLE_CLASS))
          .withTermMap(tm -> tm
              .withConstantTerm("http://example.com/myItem")
          )
        )
      )
      .build(x -> Optional.of(input))
      .execute(new ThrowingErrorHandler())
      .collect(toList());

    assertThat(result, contains(likeTriple(
      ANY,
      uri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type"),
      uri(EXAMPLE_CLASS)
    )));
  }

  @Test
  public void generatesSubjectsForEachInputObject() {

    DataSource input = new TestDataSource(Lists.newArrayList(
      ImmutableMap.of("rdfUri", "http://www.example.org/example/1"),
      ImmutableMap.of("rdfUri", "http://www.example.org/example/2")
    ));

    List<Quad> result = rmlMappingDocument()
      .withTripleMap("http://example.org/mapping1", trip -> trip
        .withSubjectMap(sm -> sm
          .withTermMap(tm -> tm
            .withColumnTerm("rdfUri")
          )
          .withClass(rdf(EXAMPLE_CLASS))
        )
      )
      .build(x -> Optional.of(input))
      .execute(new ThrowingErrorHandler())
      .collect(toList());

    assertThat(result, contains(
      likeTriple(uri("http://www.example.org/example/1"), ANY, ANY),
      likeTriple(uri("http://www.example.org/example/2"), ANY, ANY)
    ));
  }

  @Test
  public void generatesPredicateForEachInputObject() {
    final String theNamePredicate = "http://example.org/vocab#name";

    DataSource input = new TestDataSource(Lists.newArrayList(ImmutableMap.of(
      "rdfUri", "http://www.example.org/example/1",
      "naam", "Bill"
    )));

    List<Quad> result = rmlMappingDocument()
      .withTripleMap("http://example.org/mapping1", trip -> trip
        .withSubjectMap(sm -> sm
          .withTermMap(tm -> tm.withColumnTerm("rdfUri"))
        )
        .withPredicateObjectMap(pom -> pom
          .withPredicate(theNamePredicate)
          .withObjectMap(tm -> tm.withColumnTerm("naam"))
        )
      )
      .build(x -> Optional.of(input))
      .execute(new ThrowingErrorHandler())
      .collect(toList());

    assertThat(result, contains(
      likeTriple(ANY, uri(theNamePredicate), literal("Bill")))
    );
  }

  @Test
  public void handlesTemplateMaps() {
    DataSource input = new TestDataSource(Lists.newArrayList(ImmutableMap.of(
      "naam", "Bill"
    )));

    List<Quad> result = rmlMappingDocument()
      .withTripleMap("http://example.org/mapping1", trip -> trip
        .withSubjectMap(sm -> sm
          .withClass(rdf(EXAMPLE_CLASS))
          .withTermMap(tm -> tm.withTemplateTerm("http://example.org/items/{naam}?blah"))
        )
      )
      .build(x -> Optional.of(input))
      .execute(new ThrowingErrorHandler())
      .collect(toList());

    assertThat(result, contains(
      likeTriple(uri("http://example.org/items/Bill?blah"), ANY, ANY)
    ));
  }

  @Test
  public void canGenerateLinks() {
    final String theNamePredicate = "http://example.org/vocab#name";
    final String theWrittenByPredicate = "http://example.org/vocab#writtenBy";
    RmlMappingDocument rmlMappingDocument = rmlMappingDocument()
            .withTripleMap("http://example.org/personsMap", makePersonMap(theNamePredicate))
            .withTripleMap("http://example.org/documentsMap", makeDocumentMap(theWrittenByPredicate))
            .build(makePersonDocumentSourceFactory());

    List<Quad> result = rmlMappingDocument
      .execute(new ThrowingErrorHandler())
      .collect(toList());

    assertThat(result, contains(
      likeTriple(
        uri("http://www.example.org/persons/1"),
        uri(theNamePredicate),
        literal("Bill")
      ),
      likeTriple(
        uri("http://www.example.org/documents/1"),
        uri(theWrittenByPredicate),
        uri("http://www.example.org/persons/1")
      )
    ));
  }

  @Test
  public void canHandleMappingsInTheWrongOrder() {
    final String theNamePredicate = "http://example.org/vocab#name";
    final String theWrittenByPredicate = "http://example.org/vocab#writtenBy";
    RmlMappingDocument rmlMappingDocument = rmlMappingDocument()
            .withTripleMap("http://example.org/documentsMap", makeDocumentMap(theWrittenByPredicate))
            .withTripleMap("http://example.org/personsMap", makePersonMap(theNamePredicate))
            .build(makePersonDocumentSourceFactory());

    List<Quad> result = rmlMappingDocument
      .execute(new ThrowingErrorHandler())
      .collect(toList());

    assertThat(result, contains(
      likeTriple(
        uri("http://www.example.org/persons/1"),
        uri(theNamePredicate),
        literal("Bill")
      ),
      likeTriple(
        uri("http://www.example.org/documents/1"),
        uri(theWrittenByPredicate),
        uri("http://www.example.org/persons/1")
      )
    ));
  }

  @Test
  public void canHandleCircularReferenceToSelf() {
    final String theNamePredicate = "http://example.org/vocab#name";
    final String theIsParentOfPredicate = "http://example.org/vocab#isParentOf";
    final String theIsRelatedToPredicate = "http://example.org/vocab#isRelatedTo";
    DataSource input = new TestDataSource(Lists.newArrayList(
      ImmutableMap.of(
              "rdfUri", "http://example.com/persons/1",
              "naam", "Bill",
              "parentOf", "Joe",
              "relatedTo", ""
      ),
      ImmutableMap.of(
              "rdfUri", "http://example.com/persons/2",
              "naam", "Joe",
              "parentOf", "",
              "relatedTo", "Bill")
    ));
    RmlMappingDocument rmlMappingDocument = rmlMappingDocument()
            .withTripleMap("http://example.org/personsMap",
                    makePersonMap(theNamePredicate, theIsParentOfPredicate, theIsRelatedToPredicate))
            .build(x -> Optional.of(input));

    List<Quad> result = rmlMappingDocument
            .execute(new LoggingErrorHandler())
            .collect(toList());

    assertThat(result, containsInAnyOrder(
      likeTriple(
        uri("http://example.com/persons/1"),
        uri(theNamePredicate),
        literal("Bill")
      ),
      likeTriple(
        uri("http://example.com/persons/2"),
        uri(theNamePredicate),
        literal("Joe")
      ),
      likeTriple(
        uri("http://example.com/persons/1"),
        uri(theIsParentOfPredicate),
        uri("http://example.com/persons/2")
      ),
      likeTriple(
        uri("http://example.com/persons/2"),
        uri(theIsRelatedToPredicate),
        uri("http://example.com/persons/1")
      )
    ));
  }

  @Test
  public void canHandleCircularReferenceWithIndirection() {
    final String theNamePredicate = "http://example.org/vocab#name";
    final String theWrittenByPredicate = "http://example.org/vocab#writtenBy";
    final String theCoAuthorOfPredicate = "http://example.org/vocab#coAuthorOf";
    RmlMappingDocument rmlMappingDocument = rmlMappingDocument()
            .withTripleMap("http://example.org/documentsMap", makeDocumentMap(theWrittenByPredicate))
            .withTripleMap("http://example.org/personsMap", makePersonMap(theNamePredicate, theCoAuthorOfPredicate))
            .build(makePersonDocumentSourceFactory());

    List<Quad> result = rmlMappingDocument
            .execute(new ThrowingErrorHandler())
            .collect(toList());

    assertThat(result, containsInAnyOrder(
      likeTriple(
              uri("http://www.example.org/persons/1"),
              uri(theNamePredicate),
              literal("Bill")
      ),
      likeTriple(
              uri("http://www.example.org/persons/1"),
              uri(theCoAuthorOfPredicate),
              uri("http://www.example.org/documents/1")
      ),
      likeTriple(
              uri("http://www.example.org/documents/1"),
              uri(theWrittenByPredicate),
              uri("http://www.example.org/persons/1")
      )
    ));
  }

  @Test
  public void createsExactlyAsManySplitOffsAsNecessary() {
    /* given

      x dep y
      y dep a
      y dep y
      y dep z
      z dep x

      and unsortedList = [z, x, y, a]

      we expect exactly one splitOff for y (y') such that
      y dep a
      y' dep y
      y' dep z

      in a sortedList as follows: [a, y, x, z, y']

      before this test it was:

      y dep a
      y' dep y
      y'' dep z

      in a sortedList as follows: [a, y, x, z, y', y'']
    */

    final String nameSpace = "http://example.org/";
    final String mappingNameSpace = nameSpace + "mappings/";
    final String theDependsOnPredicate = mappingNameSpace + "vocab#dependsOn";
    final String theDepColumnKey = "dependsOn";
    final String theIdColumnKey = "ID";
    ErrorHandler errorHandler = mock(ErrorHandler.class);

    AtomicInteger passesThroughDataSourceX = new AtomicInteger(0);
    AtomicInteger passesThroughDataSourceY = new AtomicInteger(0);
    AtomicInteger passesThroughDataSourceZ = new AtomicInteger(0);
    AtomicInteger passesThroughDataSourceA = new AtomicInteger(0);

    RmlMappingDocument rmlMappingDocument = rmlMappingDocument()
      .withTripleMap(mappingNameSpace + "z", trip -> trip
        .withLogicalSource(rdf(nameSpace + "z"))
        .withSubjectMap(sm -> sm.withTermMap(tm -> tm.withColumnTerm("rdfUri")))
        .withPredicateObjectMap(pom -> pom
          .withPredicate(theDependsOnPredicate)
          .withReference(rm -> rm
            .withParentTriplesMap(mappingNameSpace + "x")
            .withJoinCondition(theDepColumnKey, theIdColumnKey)
          )
        )
      )
      .withTripleMap(mappingNameSpace + "x", trip -> trip
        .withSubjectMap(sm -> sm.withTermMap(tm -> tm.withColumnTerm("rdfUri")))
        .withLogicalSource(rdf(nameSpace + "x"))
        .withPredicateObjectMap(pom -> pom
          .withPredicate(theDependsOnPredicate)
          .withReference(rm -> rm
            .withParentTriplesMap(mappingNameSpace + "y")
            .withJoinCondition(theDepColumnKey, theIdColumnKey)
          )
        )
      )
      .withTripleMap(mappingNameSpace + "y", trip -> trip
        .withSubjectMap(sm -> sm.withTermMap(tm -> tm.withColumnTerm("rdfUri")))
        .withLogicalSource(rdf(nameSpace + "y"))
        .withPredicateObjectMap(pom -> pom
          .withPredicate(theDependsOnPredicate)
          .withReference(rm -> rm
            .withParentTriplesMap(mappingNameSpace + "y")
            .withJoinCondition(theDepColumnKey + "Y", theIdColumnKey)
          )
        )
        .withPredicateObjectMap(pom -> pom
          .withPredicate(theDependsOnPredicate)
          .withReference(rm -> rm
            .withParentTriplesMap(mappingNameSpace + "z")
            .withJoinCondition(theDepColumnKey + "Z", theIdColumnKey)
          )
        )
        .withPredicateObjectMap(pom -> pom
          .withPredicate(theDependsOnPredicate)
          .withReference(rm -> rm
            .withParentTriplesMap(mappingNameSpace + "a")
            .withJoinCondition(theDepColumnKey + "A", theIdColumnKey)
          )
        )
      )
      .withTripleMap(mappingNameSpace + "a", trip -> trip
        .withLogicalSource(rdf(nameSpace + "a"))
        .withSubjectMap(sm -> sm.withTermMap(tm -> tm.withColumnTerm("rdfUri")))
        .withPredicateObjectMap(pom -> pom
          .withPredicate(nameSpace + "vocab#name")
          .withObjectMap(tm -> tm.withColumnTerm("naam"))
        )
      )
      .build(logicalSource -> {

        if (logicalSource.asIri().get().equals(nameSpace + "z")) {
          passesThroughDataSourceZ.incrementAndGet();
          return Optional.of(new TestDataSource(Lists.newArrayList(
            ImmutableMap.of(theIdColumnKey, "z1", theDepColumnKey, "x1", "rdfUri", nameSpace + "z1")

          ), errorHandler));
        }
        if (logicalSource.asIri().get().equals(nameSpace + "x")) {
          passesThroughDataSourceX.incrementAndGet();
          return Optional.of(new TestDataSource(Lists.newArrayList(
            ImmutableMap.of(theIdColumnKey, "x1", theDepColumnKey, "y1", "rdfUri", nameSpace + "x1")

          ), errorHandler));
        }
        if (logicalSource.asIri().get().equals(nameSpace + "y")) {
          passesThroughDataSourceY.incrementAndGet();
          return Optional.of(new TestDataSource(Lists.newArrayList(
            ImmutableMap.of(theIdColumnKey, "y1", theDepColumnKey + "Y", "y2", "rdfUri", nameSpace + "y1"),
            ImmutableMap.of(theIdColumnKey, "y2", theDepColumnKey + "A", "a1", "rdfUri", nameSpace + "y2" ),
            ImmutableMap.of(theIdColumnKey, "y3", theDepColumnKey + "Z", "z1", "rdfUri", nameSpace + "y3")
          ), errorHandler));
        }
        if (logicalSource.asIri().get().equals(nameSpace + "a")) {
          passesThroughDataSourceA.incrementAndGet();
          return Optional.of(new TestDataSource(Lists.newArrayList(
            ImmutableMap.of(theIdColumnKey, "a1", "naam", "Naam van A", "rdfUri", nameSpace + "a1")

          ), errorHandler));
        }

        return null;
      });

    final List<Quad> result = rmlMappingDocument.execute(errorHandler).collect(Collectors.toList());

    // Verify that no unnecessary splitOffs have been generated by counting the amount of passes through each
    // datasource.
    assertThat(passesThroughDataSourceA.intValue(), equalTo(1));
    assertThat(passesThroughDataSourceX.intValue(), equalTo(1));
    assertThat(passesThroughDataSourceZ.intValue(), equalTo(1));
    assertThat(passesThroughDataSourceY.intValue(), equalTo(2));

    assertThat(result, containsInAnyOrder(
      likeTriple(
        uri(nameSpace + "a1"),
        uri(nameSpace + "vocab#name"),
        literal("Naam van A")
      ),
      likeTriple(
        uri(nameSpace + "y2"),
        uri(theDependsOnPredicate),
        uri(nameSpace + "a1")
      ),
      likeTriple(
        uri(nameSpace + "x1"),
        uri(theDependsOnPredicate),
        uri(nameSpace + "y1")
      ),
      likeTriple(
        uri(nameSpace + "z1"),
        uri(theDependsOnPredicate),
        uri(nameSpace + "x1")
      ),
      likeTriple(
        uri(nameSpace + "y3"),
        uri(theDependsOnPredicate),
        uri(nameSpace + "z1")
      ),
      likeTriple(
        uri(nameSpace + "y1"),
        uri(theDependsOnPredicate),
        uri(nameSpace + "y2")
      )
    ));
  }

  @Test
  public void whenALinkToAnOtherObjectIsNotAvailableTheExceptionIsRegistered() {
    ErrorHandler errorHandler = mock(ErrorHandler.class);
    final String theNamePredicate = "http://example.org/vocab#name";
    final String theWrittenByPredicate = "http://example.org/vocab#writtenBy";
    final Map<String, String> firstDocument = ImmutableMap.of(
      "rdfUri", "http://www.example.org/documents/1",
      "geschrevenDoor", "Bill"
    );

    final List<Quad> collect = rmlMappingDocument()
      .withTripleMap("http://example.org/personsMap", makePersonMap(theNamePredicate))
      .withTripleMap("http://example.org/documentsMap", makeDocumentMap(theWrittenByPredicate))
      .build(logicalSource -> {
        if (logicalSource.asIri().get().equals("http://example.org/persons")) {
          return Optional.of(new TestDataSource(Lists.newArrayList(ImmutableMap.of()), errorHandler));
        }
        if (logicalSource.asIri().get().equals("http://example.org/documents")) {
          return Optional.of(new TestDataSource(Lists.newArrayList(firstDocument), errorHandler));
        }
        return null;
      })
      .execute(new LoggingErrorHandler())
      .collect(toList());

    verify(errorHandler).linkError(firstDocument, "geschrevenDoor", "http://example.org/personsMap", "naam");
  }

  private Consumer<TriplesMapBuilder> makeDocumentMap(String theWrittenByPredicate) {
    return trip -> trip
      .withLogicalSource(rdf("http://example.org/documents"))
      .withSubjectMap(sm -> sm
        .withTermMap(tm -> tm.withColumnTerm("rdfUri"))
      )
      .withPredicateObjectMap(pom -> pom
        .withPredicate(theWrittenByPredicate)
        .withReference(rm -> rm
          .withParentTriplesMap("http://example.org/personsMap")
          .withJoinCondition("geschrevenDoor", "naam")
        )
      );
  }

  private Consumer<TriplesMapBuilder>  makePersonMap(String theNamePredicate) {
    return trip -> trip
      .withLogicalSource(rdf("http://example.org/persons"))
      .withSubjectMap(sm -> sm
        .withTermMap(tm -> tm.withColumnTerm("rdfUri"))
      )
      .withPredicateObjectMap(pom -> pom
        .withPredicate(theNamePredicate)
        .withObjectMap(tm -> tm.withColumnTerm("naam"))
      );
  }

  private Consumer<TriplesMapBuilder> makePersonMap(String theNamePredicate, String theCoAuthorOfPredicate) {
    return trip -> trip
      .withLogicalSource(rdf("http://example.org/persons"))
      .withSubjectMap(sm -> sm
        .withTermMap(tm -> tm.withColumnTerm("rdfUri"))
      )
      .withPredicateObjectMap(pom -> pom
        .withPredicate(theNamePredicate)
        .withObjectMap(tm -> tm.withColumnTerm("naam"))
      )
      .withPredicateObjectMap(pom -> pom
        .withPredicate(theCoAuthorOfPredicate)
        .withReference(rm -> rm
          .withParentTriplesMap("http://example.org/documentsMap")
          .withJoinCondition("heeftMeegeschrevenAan", "doc_id")
        )
      );
  }

  private Consumer<TriplesMapBuilder> makePersonMap(String theNamePredicate, String theIsParentOfPredicate,
                                                    String theIsRelatedToPredicate) {
    return trip -> trip
      .withLogicalSource(rdf("http://example.org/persons"))
      .withSubjectMap(sm -> sm
        .withTermMap(tm -> tm.withColumnTerm("rdfUri"))
      )
      .withPredicateObjectMap(pom -> pom
        .withPredicate(theNamePredicate)
        .withObjectMap(tm -> tm.withColumnTerm("naam"))
      ).withPredicateObjectMap(pom -> pom
        .withPredicate(theIsParentOfPredicate)
        .withReference(rm -> rm
          .withParentTriplesMap("http://example.org/personsMap")
          .withJoinCondition("parentOf", "naam")
        )
      ).withPredicateObjectMap(pom -> pom
        .withPredicate(theIsRelatedToPredicate)
        .withReference(rm -> rm
          .withParentTriplesMap("http://example.org/personsMap")
          .withJoinCondition("relatedTo", "naam")
        )
      );
  }

  private Function<RdfResource, Optional<DataSource>> makePersonDocumentSourceFactory() {
    return logicalSource -> {
      if (logicalSource.asIri().get().equals("http://example.org/persons")) {
        return Optional.of(new TestDataSource(Lists.newArrayList(ImmutableMap.of(
          "rdfUri", "http://www.example.org/persons/1",
          "naam", "Bill",
          "heeftMeegeschrevenAan", "1"
        ))));
      }
      if (logicalSource.asIri().get().equals("http://example.org/documents")) {
        return Optional.of(new TestDataSource(Lists.newArrayList(ImmutableMap.of(
          "rdfUri", "http://www.example.org/documents/1",
          "geschrevenDoor", "Bill",
          "doc_id", "1"
        ))));
      }
      return null;
    };
  }

  public RdfResource rdf(String uri) {
    return new MockRdfResource(uri, new HashMap<>());
  }

  private class MockRdfResource implements RdfResource {

    private final String uri;
    private final Map<String, Set<RdfResource>> outs;

    MockRdfResource(String uri, Map<String, Set<RdfResource>> outs) {
      this.uri = uri;
      this.outs = outs;
    }

    @Override
    public Set<RdfResource> out(String predicateUri) {
      return outs.putIfAbsent(predicateUri, new HashSet<>());
    }

    @Override
    public Optional<String> asIri() {
      return Optional.of(uri);
    }

    @Override
    public Optional<RdfLiteral> asLiteral() {
      return Optional.empty();
    }
  }
}
