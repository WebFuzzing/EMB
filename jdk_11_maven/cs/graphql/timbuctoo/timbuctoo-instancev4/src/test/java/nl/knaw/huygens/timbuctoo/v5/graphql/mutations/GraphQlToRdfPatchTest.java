package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.ChangeLog;
import nl.knaw.huygens.timbuctoo.v5.rdfio.RdfPatchSerializer;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.INTEGER;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.RDF_TYPE;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_VOCAB;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.timPredicate;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.timType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;
import static org.mockito.internal.verification.VerificationModeFactory.times;

public class GraphQlToRdfPatchTest {
  private static final String PRED2 = "http://example.org/pred2";
  private static final String PRED1 = "http://example.org/pred1";
  private static final String SUBJECT = "http://example.org/subject";
  private static final String DATA_SET_URI = "http://example.org/dataset";
  private static final String USER_URI = "http://example.org/user";


  private RdfPatchSerializer serializer;
  private ChangeLog changeLog;
  private DataSet dataSet;
  private QuadStore quadStore;

  @Before
  public void setUp() {
    serializer = mock(RdfPatchSerializer.class);
    changeLog = mock(ChangeLog.class);
    dataSet = mock(DataSet.class);
    quadStore = mock(QuadStore.class);

    DataSetMetaData dataSetMetaData = mock(DataSetMetaData.class);
    when(dataSetMetaData.getBaseUri()).thenReturn(DATA_SET_URI);
    when(dataSet.getMetadata()).thenReturn(dataSetMetaData);
    when(dataSet.getQuadStore()).thenReturn(quadStore);
    when(quadStore.getQuads(SUBJECT, timPredicate("latestRevision"), Direction.OUT, ""))
      .thenReturn(Stream.empty());
  }

  @Test
  public void addsQuadsForEachAdditionsToEntity() throws Exception {
    String addedValue = "newValue";
    String addedValue2 = "newValue2";
    addAdditionsToChangeLog(
      new Change(SUBJECT, PRED1, newArrayList(new Value(addedValue, STRING)), null),
      new Change(SUBJECT, PRED2, newArrayList(new Value(addedValue2, STRING)), null));
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue, STRING, null, null);
    verify(serializer).addDelQuad(true, SUBJECT, PRED2, addedValue2, STRING, null, null);
  }

  @Test
  public void addsQuadsForEachValueInAnAddition() throws Exception {
    String addedValue = "newValue";
    String addedValue2 = "newValue2";
    addAdditionsToChangeLog(
      new Change(SUBJECT, PRED1, newArrayList(new Value(addedValue, STRING), new Value(addedValue2, STRING)), null)
    );
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue, STRING, null, null);
    verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue2, STRING, null, null);
  }

  @Test
  public void addsTheNewValuesForReplacement() throws Exception {
    String addedValue = "newValue";
    String addedValue2 = "newValue2";
    String oldValue = "oldValue";
    addReplacementsToChangeLog(new Replacement(
      SUBJECT,
      PRED1,
      newArrayList(new Value(addedValue, STRING), new Value(addedValue2, STRING)),
      newArrayList(new Value(oldValue, STRING))
    ));
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    InOrder inOrder = inOrder(serializer);
    inOrder.verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue2, STRING, null, null);
  }

  @Test
  public void addsAndRemovesValuesOfAllReplacements() throws Exception {
    String addedValue = "newValue";
    String addedValue2 = "newValue2";
    String oldValue = "oldValue";
    String oldValue2 = "oldValue2";
    addReplacementsToChangeLog(
      new Replacement(
        SUBJECT,
        PRED1,
        newArrayList(new Value(addedValue, STRING)),
        newArrayList(new Value(oldValue, STRING))
      ),
      new Replacement(
        SUBJECT,
        PRED2,
        newArrayList(new Value(addedValue2, STRING)),
        newArrayList(new Value(oldValue2, STRING))
      )
    );
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    InOrder inOrder = inOrder(serializer);
    inOrder.verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(false, SUBJECT, PRED2, oldValue2, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(true, SUBJECT, PRED2, addedValue2, STRING, null, null);
  }

  @Test
  public void removesAllOldValuesOfReplacements() throws Exception {
    String oldValue = "oldValue";
    String oldValue2 = "oldValue2";
    String addedValue = "newValue";
    String addedValue2 = "newValue2";
    addReplacementsToChangeLog(new Replacement(
      SUBJECT,
      PRED1,
      newArrayList(new Value(addedValue, STRING), new Value(addedValue2, STRING)),
      newArrayList(new Value(oldValue, STRING), new Value(oldValue2, STRING))
    ));
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    InOrder inOrder = inOrder(serializer);
    inOrder.verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue2, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue2, STRING, null, null);
  }

  @Test
  public void deletesAllValuesOfTheDeletions() throws Exception {
    String oldValue = "oldValue";
    String oldValue2 = "oldValue2";
    addDeletionsToChangeLog(new Deletion(
      SUBJECT,
      PRED1,
      newArrayList(new Value(oldValue, STRING), new Value(oldValue2, STRING))
    ));
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue, STRING, null, null);
    verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue2, STRING, null, null);
  }

  @Test
  public void removesOldValuesOfAllDeletions() throws Exception {
    String oldValue = "oldValue";
    String oldValue2 = "oldValue2";
    addDeletionsToChangeLog(
      new Deletion(
        SUBJECT,
        PRED1,
        newArrayList(new Value(oldValue, STRING))
      ),
      new Deletion(
        SUBJECT,
        PRED2,
        newArrayList(new Value(oldValue2, STRING))
      )
    );
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue, STRING, null, null);
    verify(serializer).addDelQuad(false, SUBJECT, PRED2, oldValue2, STRING, null, null);
  }

  @Test
  public void deletionsAreExecutedBeforeAdditions() throws Exception {
    String addedValue = "newValue";
    String oldValue = "oldValue";
    addAdditionsToChangeLog(
      new Change(
        SUBJECT,
        PRED1,
        newArrayList(new Value(addedValue, STRING)), null)
    );
    addDeletionsToChangeLog(
      new Deletion(
        SUBJECT,
        PRED1,
        newArrayList(new Value(oldValue, STRING))
      )
    );
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    InOrder inOrder = inOrder(serializer);
    inOrder.verify(serializer).addDelQuad(false, SUBJECT, PRED1, oldValue, STRING, null, null);
    inOrder.verify(serializer).addDelQuad(true, SUBJECT, PRED1, addedValue, STRING, null, null);
  }

  // add revision
  @Test
  public void addsRevisionOfTheUpdatedEntity() throws Exception {
    final int prevVersion = -1;
    final int newVersion = 0;

    final String prevRevision = SUBJECT + "/" + prevVersion;
    final String newRevision = SUBJECT + "/" + newVersion;
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(true, SUBJECT, timPredicate("latestRevision"), newRevision, null, null, null);
    verify(serializer)
      .addDelQuad(true, newRevision, "http://www.w3.org/ns/prov#specializationOf", SUBJECT, null, null, null);
    verify(serializer).addDelQuad(
      true,
      newRevision,
      timPredicate("version"),
      String.valueOf(newVersion),
      RdfConstants.INTEGER,
      null,
      null
    );
    verify(serializer).addDelQuad(false, SUBJECT, timPredicate("latestRevision"), prevRevision, null, null, null);
  }

  @Test
  public void updatesVersionBasedOnPreviousVersion() throws Exception {
    final int prevVersion = 4;
    final int newVersion = 5;

    final String prevRevision = SUBJECT + "/" + prevVersion;
    final String newRevision = SUBJECT + "/" + newVersion;
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    when(quadStore.getQuads(SUBJECT, timPredicate("latestRevision"), Direction.OUT, ""))
      .thenAnswer(new Answer<Stream<CursorQuad>>() {
        @Override
        public Stream<CursorQuad> answer(InvocationOnMock invocation) {
          List<CursorQuad> quads = newArrayList();
          quads.add(
            CursorQuad.create(SUBJECT, timPredicate("latestRevision"), Direction.OUT, prevRevision, STRING, null, ""));
          return quads.stream();
        }
      });

    when(quadStore.getQuads(prevRevision, timPredicate("version"), Direction.OUT, ""))
      .thenAnswer(new Answer<Stream<CursorQuad>>() {
        @Override
        public Stream<CursorQuad> answer(InvocationOnMock invocation) {
          List<CursorQuad> quads = newArrayList();
          quads.add(CursorQuad
            .create(prevRevision, timPredicate("version"), Direction.OUT, String.valueOf(prevVersion), INTEGER, null,
              ""));
          return quads.stream();
        }
      });

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer)
      .addDelQuad(true, newRevision, timPredicate("version"), String.valueOf(newVersion), RdfConstants.INTEGER, null,
        null);
  }

  // add provenance
  @Test
  public void provenanceIsAddedToTheLatestRevision() throws Exception {
    final int newVersion = 0;

    final String newRevision = SUBJECT + "/" + newVersion;
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/activity"),
      eq("http://www.w3.org/ns/prov#generated"),
      eq(newRevision),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/activity"),
      eq(RDF_TYPE),
      eq("http://www.w3.org/ns/prov#Activity"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/activity"),
      eq("http://www.w3.org/ns/prov#associatedWith"),
      eq(USER_URI),
      isNull(),
      isNull(),
      isNull())
    ;
    verify(serializer).addDelQuad(true, USER_URI, RDF_TYPE, "http://www.w3.org/ns/prov#Agent", null, null, null);
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/activity"),
      eq("http://www.w3.org/ns/prov#qualifiedAssociation"),
      startsWith(DATA_SET_URI + "/association"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/association"),
      eq(RDF_TYPE),
      eq("http://www.w3.org/ns/prov#Association"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/association"),
      eq("http://www.w3.org/ns/prov#agent"),
      eq(USER_URI),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/association"),
      eq("http://www.w3.org/ns/prov#hadPlan"),
      startsWith(DATA_SET_URI + "/plan"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/plan"),
      eq(RDF_TYPE),
      eq("http://www.w3.org/ns/prov#Plan"),
      isNull(),
      isNull(),
      isNull()
    );
  }

  @Test
  public void addsAdditionsToPlan() throws Exception {
    String addedValue = "newValue";
    String addedValue2 = "newValue2";
    addAdditionsToChangeLog(
      new Change(SUBJECT, PRED1, newArrayList(new Value(addedValue, STRING), new Value(addedValue2, STRING)), null)
    );

    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/plan"),
      eq(timPredicate("additions")),
      startsWith(DATA_SET_URI + "/additions"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/additions"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "Additions"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/additions"),
      eq(timPredicate("hasAddition")),
      startsWith(DATA_SET_URI + "/addition"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/addition"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "Addition"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/addition"),
      eq(timPredicate("hasKey")),
      eq(PRED1),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/addition"),
      eq(timPredicate("hasValue")),
      startsWith(DATA_SET_URI + "/value"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(RDF_TYPE),
      eq(timType("Value")),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("type")),
      eq(STRING),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("rawValue")),
      eq(addedValue),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("rawValue")),
      eq(addedValue2),
      eq(STRING),
      isNull(),
      isNull()
    );
    // TODO find a better way to test
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("nextValue")),
      startsWith(DATA_SET_URI + "/value"),
      isNull(),
      isNull(),
      isNull()
    );
  }

  @Test
  public void addsDeletionsToPlan() throws Exception {
    String oldValue = "oldValue";
    String oldValue2 = "oldValue2";
    addDeletionsToChangeLog(new Deletion(
      SUBJECT,
      PRED1,
      newArrayList(new Value(oldValue, STRING), new Value(oldValue2, STRING))
    ));
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/plan"),
      eq(timPredicate("deletions")),
      startsWith(DATA_SET_URI + "/deletions"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/deletions"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "Deletions"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/deletions"),
      eq(timPredicate("hasDeletion")),
      startsWith(DATA_SET_URI + "/deletion"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/deletion"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "Deletion"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/deletion"),
      eq(timPredicate("hasKey")),
      eq(PRED1),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/deletion"),
      eq(timPredicate("hasValue")),
      startsWith(DATA_SET_URI + "/value"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(RDF_TYPE),
      eq(timType("Value")),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("type")),
      eq(STRING),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("rawValue")),
      eq(oldValue),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("rawValue")),
      eq(oldValue2),
      eq(STRING),
      isNull(),
      isNull()
    );
    // TODO find a better way to test
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("nextValue")),
      startsWith(DATA_SET_URI + "/value"),
      isNull(),
      isNull(),
      isNull()
    );
  }

  @Test
  public void addsReplacementsToPlan() throws Exception {
    String newValue = "newValue";
    String newValue2 = "newValue2";
    String oldValue = "oldValue1";
    String oldValue2 = "oldValue2";

    addReplacementsToChangeLog(
      new Replacement(
        SUBJECT,
        PRED1,
        newArrayList(new Value(newValue, STRING), new Value(newValue2, STRING)),
        newArrayList(new Value(oldValue, STRING), new Value(oldValue2, STRING))
      ));

    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/plan"),
      eq(timPredicate("replacements")),
      startsWith(DATA_SET_URI + "/replacements"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/replacements"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "Replacements"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/replacements"),
      eq(timPredicate("hasReplacement")),
      startsWith(DATA_SET_URI + "/replacement"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/replacement"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "Replacement"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/replacement"),
      eq(timPredicate("hasKey")),
      eq(PRED1),
      isNull(),
      isNull(),
      isNull()
    );
    // new values
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/replacement"),
      eq(timPredicate("hasValue")),
      startsWith(DATA_SET_URI + "/value"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("type")),
      eq(STRING),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "Value"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("rawValue")),
      eq(newValue),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("rawValue")),
      eq(newValue2),
      eq(STRING),
      isNull(),
      isNull()
    );
    // TODO find a better way to test
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/value"),
      eq(timPredicate("nextValue")),
      startsWith(DATA_SET_URI + "/value"),
      isNull(),
      isNull(),
      isNull()
    );
    // old values
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/replacement"),
      eq(timPredicate("hadValue")),
      startsWith(DATA_SET_URI + "/oldValue"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/oldValue"),
      eq(timPredicate("type")),
      eq(STRING),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer, times(2)).addDelQuad( // one for each value
      eq(true),
      startsWith(DATA_SET_URI + "/oldValue"),
      eq(RDF_TYPE),
      eq(TIM_VOCAB + "OldValue"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/oldValue"),
      eq(timPredicate("rawValue")),
      eq(oldValue),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/oldValue"),
      eq(timPredicate("rawValue")),
      eq(oldValue2),
      eq(STRING),
      isNull(),
      isNull()
    );
    // TODO find a better way to test
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/oldValue"),
      eq(timPredicate("nextOldValue")),
      startsWith(DATA_SET_URI + "/oldValue"),
      isNull(),
      isNull(),
      isNull()
    );
  }

  @Test
  public void addsCustomProvenanceToPlan() throws Exception {
    String value1 = "value1";
    String value2 = "value2";
    addProvenanceToChangeLog(
      new Provenance(PRED1, newArrayList(new Value(value1, STRING))),
      new Provenance(PRED2, newArrayList(new Value(value2, STRING)))
    );
    GraphQlToRdfPatch instance = new GraphQlToRdfPatch(SUBJECT, USER_URI, changeLog);

    instance.sendQuads(serializer, s -> {
    }, dataSet);

    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/plan"),
      eq(timPredicate("hasCustomProv")),
      startsWith(DATA_SET_URI + "/customProv"),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/customProv"),
      eq(RDF_TYPE),
      eq(timType("CustomProv")),
      isNull(),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(SUBJECT),
      eq(PRED1),
      eq(value1),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(SUBJECT),
      eq(PRED2),
      eq(value2),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/customProv"),
      eq(PRED1),
      eq(value1),
      eq(STRING),
      isNull(),
      isNull()
    );
    verify(serializer).addDelQuad(
      eq(true),
      startsWith(DATA_SET_URI + "/customProv"),
      eq(PRED2),
      eq(value2),
      eq(STRING),
      isNull(),
      isNull()
    );
  }

  private void addAdditionsToChangeLog(Change... changes) {
    when(changeLog.getAdditions(any(DataSet.class))).thenAnswer(new Answer<Stream<Change>>() {
      @Override
      public Stream<Change> answer(InvocationOnMock invocation) {
        return newArrayList(changes).stream();
      }
    });
  }

  private void addDeletionsToChangeLog(Deletion... deletions) {
    when(changeLog.getDeletions(any(DataSet.class))).thenAnswer(new Answer<Stream<Change>>() {
      @Override
      public Stream<Change> answer(InvocationOnMock invocation) {
        return newArrayList(deletions).stream().map(deletion -> deletion.toChange());
      }
    });
  }

  private static class Deletion {
    private final String subject;
    private final String predicate;
    private final List<Value> valuesToDelete;

    Deletion(String subject, String predicate, List<Value> valuesToDelete) {

      this.subject = subject;
      this.predicate = predicate;
      this.valuesToDelete = valuesToDelete;
    }


    private Change toChange() {
      return new Change(subject, predicate, newArrayList(), valuesToDelete.stream());
    }
  }

  private void addReplacementsToChangeLog(Replacement... replacements) {
    when(changeLog.getReplacements(any(DataSet.class))).thenAnswer(new Answer<Stream<Change>>() {
      @Override
      public Stream<Change> answer(InvocationOnMock invocation) {
        return newArrayList(replacements).stream().map(replacement -> replacement.toChange());
      }
    });
  }

  private static class Replacement {
    private final String subject;
    private final String predicate;
    private final List<Value> newValues;
    private final List<Value> oldValues;

    Replacement(String subject, String predicate, List<Value> newValues, List<Value> oldValues) {

      this.subject = subject;
      this.predicate = predicate;
      this.newValues = newValues;
      this.oldValues = oldValues;
    }

    private Change toChange() {
      return new Change(subject, predicate, newValues, oldValues.stream());
    }
  }

  private void addProvenanceToChangeLog(Provenance... provenances) {
    when(changeLog.getProvenance(any(DataSet.class), any(String.class))).thenAnswer(
      (Answer<Stream<Change>>) invocation -> newArrayList(provenances).stream().flatMap(
        provenance -> Arrays.stream(invocation.getArguments()).skip(1)
                            .map(arg -> provenance.toChange(arg.toString()))));
  }

  private static class Provenance {
    private final String predicate;
    private final List<Value> values;

    Provenance(String predicate, List<Value> values) {
      this.predicate = predicate;
      this.values = values;
    }

    private Change toChange(String subject) {
      return new Change(subject, predicate, values, Stream.empty());
    }
  }
}
