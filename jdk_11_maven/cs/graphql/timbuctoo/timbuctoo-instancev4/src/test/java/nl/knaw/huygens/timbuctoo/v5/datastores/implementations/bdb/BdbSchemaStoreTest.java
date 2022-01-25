package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.ChangeFetcher;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.LogList;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.ChangeType;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.PredicateMatcher.predicate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;

public class BdbSchemaStoreTest {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  @Test
  public void generatesASchemaForAllAdded() throws Exception {
    final DummyDataStorage dataStore = new DummyDataStorage();

    ChangeFetcher changeFetcher = new DummyChangeFetcher(
      CursorQuad.create("subj", "pred", Direction.OUT, ChangeType.ASSERTED, "obj", null, null, ""),
      CursorQuad.create("subj", RdfConstants.RDF_TYPE, Direction.OUT, ChangeType.ASSERTED, "type", null, null, ""),
      CursorQuad.create("obj", "pred", Direction.IN, ChangeType.ASSERTED, "subj", null, null, "")
    );

    BdbSchemaStore schemaStore = new BdbSchemaStore(dataStore, new ImportStatus(new LogList()));

    schemaStore.start();
    schemaStore.onChangedSubject("subj", changeFetcher);
    schemaStore.onChangedSubject("obj", changeFetcher);
    schemaStore.finish();

    assertThat(dataStore.getResult(), is("{\n" +
      "  \"http://timbuctoo.huygens.knaw.nl/static/v5/vocabulary#unknown\" : {\n" +
      "    \"name\" : \"http://timbuctoo.huygens.knaw.nl/static/v5/vocabulary#unknown\",\n" +
      "    \"predicates\" : [ {\n" +
      "      \"name\" : \"pred\",\n" +
      "      \"direction\" : \"IN\",\n" +
      "      \"valueTypes\" : { },\n" +
      "      \"referenceTypes\" : {\n" +
      "        \"type\" : 1\n" +
      "      },\n" +
      "      \"subjectsWithThisPredicate\" : 1,\n" +
      "      \"subjectsWithThisPredicateAsList\" : 0,\n" +
      "      \"hasBeenList\" : false,\n" +
      "      \"hasBeenSingular\" : true\n" +
      "    }, {\n" +
      "      \"name\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\",\n" +
      "      \"direction\" : \"IN\",\n" +
      "      \"valueTypes\" : { },\n" +
      "      \"referenceTypes\" : {\n" +
      "        \"type\" : 1\n" +
      "      },\n" +
      "      \"subjectsWithThisPredicate\" : 0,\n" +
      "      \"subjectsWithThisPredicateAsList\" : 0,\n" +
      "      \"hasBeenList\" : false,\n" +
      "      \"hasBeenSingular\" : false\n" +
      "    } ],\n" +
      "    \"subjectsWithThisType\" : 1\n" +
      "  },\n" +
      "  \"type\" : {\n" +
      "    \"name\" : \"type\",\n" +
      "    \"predicates\" : [ {\n" +
      "      \"name\" : \"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\",\n" +
      "      \"direction\" : \"OUT\",\n" +
      "      \"valueTypes\" : { },\n" +
      "      \"referenceTypes\" : {\n" +
      "        \"http://timbuctoo.huygens.knaw.nl/static/v5/vocabulary#unknown\" : 1\n" +
      "      },\n" +
      "      \"subjectsWithThisPredicate\" : 1,\n" +
      "      \"subjectsWithThisPredicateAsList\" : 0,\n" +
      "      \"hasBeenList\" : false,\n" +
      "      \"hasBeenSingular\" : true\n" +
      "    }, {\n" +
      "      \"name\" : \"pred\",\n" +
      "      \"direction\" : \"OUT\",\n" +
      "      \"valueTypes\" : { },\n" +
      "      \"referenceTypes\" : {\n" +
      "        \"http://timbuctoo.huygens.knaw.nl/static/v5/vocabulary#unknown\" : 1\n" +
      "      },\n" +
      "      \"subjectsWithThisPredicate\" : 1,\n" +
      "      \"subjectsWithThisPredicateAsList\" : 0,\n" +
      "      \"hasBeenList\" : false,\n" +
      "      \"hasBeenSingular\" : true\n" +
      "    } ],\n" +
      "    \"subjectsWithThisType\" : 1\n" +
      "  }\n" +
      "}"));
  }

  @Test
  public void doNotRemovePredicatesThatDoNotExistOnType() throws Exception {
    final DummyDataStorage dataStore = new DummyDataStorage();

    ChangeFetcher changeFetcher = new DummyChangeFetcher(
      CursorQuad.create("subj", "pred", Direction.OUT, ChangeType.ASSERTED, "obj", RdfConstants.STRING, null, "")
    );

    BdbSchemaStore schemaStore = new BdbSchemaStore(dataStore, new ImportStatus(new LogList()));

    schemaStore.start();
    schemaStore.onChangedSubject("subj", changeFetcher);
    schemaStore.finish();

    changeFetcher = new DummyChangeFetcher(
      CursorQuad.create("subj", "pred", Direction.OUT, ChangeType.UNCHANGED, "type", RdfConstants.STRING, null, ""),
      CursorQuad.create("subj", "pred2", Direction.OUT, ChangeType.ASSERTED, "type", RdfConstants.STRING, null, ""),
      CursorQuad.create("subj", RdfConstants.RDF_TYPE, Direction.OUT, ChangeType.ASSERTED, "type", null, null, ""),
      CursorQuad.create("type", RdfConstants.RDF_TYPE, Direction.IN, ChangeType.ASSERTED, "subj", null, null, "")
    );

    schemaStore.start();
    schemaStore.onChangedSubject("subj", changeFetcher);
    schemaStore.finish();

    Map<String, Type> schema = OBJECT_MAPPER.readValue(dataStore.getResult(), new TypeReference<Map<String, Type>>() {
    });

    assertThat(schema, hasEntry(is("type"), allOf(
      hasProperty("name", is("type")),
      hasProperty("predicates",
        hasItem(predicate().withName("pred").withDirection(Direction.OUT).withValueType(RdfConstants.STRING))
      ),
      hasProperty("predicates",
        hasItem(predicate().withName("pred2").withDirection(Direction.OUT).withValueType(RdfConstants.STRING))
      )
    )));

    assertThat(schema, hasEntry(
      is("http://timbuctoo.huygens.knaw.nl/static/v5/vocabulary#unknown"),
      hasProperty("predicates", not(hasItem(predicate().withName("pred2"))))
    ));
  }

  private static class DummyDataStorage implements DataStorage {
    private String result;

    @Override
    public String getValue() {
      return result;
    }

    @Override
    public void setValue(String newValue) throws DatabaseWriteException {
      result = newValue;
    }

    @Override
    public void close() throws Exception {

    }

    @Override
    public void commit() {

    }

    @Override
    public void beginTransaction() {

    }

    @Override
    public boolean isClean() {
      return true;
    }

    @Override
    public void empty() {

    }

    public String getResult() {
      return result;
    }
  }

  private static class DummyChangeFetcher implements ChangeFetcher {
    private final List<CursorQuad> triples;

    public DummyChangeFetcher(CursorQuad... triples) {
      this.triples = Lists.newArrayList(triples);
    }

    @Override
    public Stream<CursorQuad> getPredicates(
      String subject,
      boolean getRetracted,
      boolean getUnchanged,
      boolean getAsserted
    ) {
      return triples.stream()
                    .filter(t -> (t.getChangeType() == ChangeType.RETRACTED) == getRetracted ||
                      (t.getChangeType() == ChangeType.UNCHANGED) == getUnchanged ||
                      (t.getChangeType() == ChangeType.ASSERTED) == getAsserted)
                    .filter(t -> (t.getSubject().equals(subject)));
    }

    @Override
    public Stream<CursorQuad> getPredicates(
      String subject,
      String predicate,
      Direction direction,
      boolean getRetracted,
      boolean getUnchanged,
      boolean getAsserted
    ) {
      return triples.stream()
                    .filter(t -> (t.getChangeType() == ChangeType.RETRACTED) == getRetracted ||
                      (t.getChangeType() == ChangeType.UNCHANGED) == getUnchanged ||
                      (t.getChangeType() == ChangeType.ASSERTED) == getAsserted)
                    .filter(t -> (t.getSubject().equals(subject)))
                    .filter(t -> (t.getPredicate().equals(predicate)))
                    .filter(t -> (t.getDirection() == direction));
    }
  }
}
