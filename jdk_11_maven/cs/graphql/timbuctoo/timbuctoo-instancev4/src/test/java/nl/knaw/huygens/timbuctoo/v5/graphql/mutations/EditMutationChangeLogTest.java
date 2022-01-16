package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import co.unruly.matchers.StreamMatchers;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.EditMutationChangeLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ChangeMatcher.likeChange;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EditMutationChangeLogTest {
  private static final String SUBJECT = "http://example.org/subject";
  private static final String NAMES_FIELD = "schema_name";
  private static final String NAMES_PRED = "http://schema.org/name";
  private static final String GRAPH_QL_STRING = "xsd_string";
  private DataSet dataSet;
  private QuadStore quadStore;
  private TypeNameStore typeNameStore;

  @Before
  public void setUp() throws Exception {
    dataSet = mock(DataSet.class);
    quadStore = mock(QuadStore.class);
    typeNameStore = mock(TypeNameStore.class);
    when(typeNameStore.makeUriForPredicate(NAMES_FIELD)).thenReturn(Optional.of(tuple(NAMES_PRED, Direction.OUT)));
    when(typeNameStore.makeUri(GRAPH_QL_STRING)).thenReturn(STRING);
    when(dataSet.getQuadStore()).thenReturn(quadStore);
    when(dataSet.getTypeNameStore()).thenReturn(typeNameStore);
  }

  @Test
  public void getAdditionsReturnsAdditions() throws Exception {
    String addedValue = "newValue";
    Map<Object, Object> additions = Maps.newHashMap();
    additions.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("additions", additions);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    List<Change> adds = instance.getAdditions(dataSet).collect(toList());

    assertThat(adds.size(), is(1));
    assertThat(adds, contains(likeChange()
      .withValues(new Value(addedValue, STRING))
      .oldValuesIsEmpty()
    ));
  }

  // The addition is captured, even though it already exists.
  // But this 'addition' is filtered out when the data will be persisted
  @Test
  public void getAdditionsDoesNotIgnoreAdditionsForSameValue() throws Exception {
    String existingValue = "value";
    Map<Object, Object> additions = Maps.newHashMap();
    additions.put(NAMES_FIELD, newArrayList(createPropertyInput(existingValue)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("additions", additions);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    valuesInQuadStore(NAMES_PRED, existingValue);

    List<Change> adds = instance.getAdditions(dataSet).collect(toList());

    assertThat(adds.size(), is(1));
    assertThat(adds, contains(likeChange()
      .withValues(new Value(existingValue, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getAdditionsReturnsReplacements() throws Exception {
    String addedValue = "newValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, createPropertyInput(addedValue));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    List<Change> adds = instance.getAdditions(dataSet).collect(toList());

    assertThat(adds.size(), is(1));
    assertThat(adds, contains(likeChange()
      .withValues(new Value(addedValue, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getAdditionsForListReturnsReplacements() throws Exception {
    String addedValue1 = "newValue1";
    String addedValue2 = "newValue2";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue1), createPropertyInput(addedValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    List<Change> adds = instance.getAdditions(dataSet).collect(toList());

    assertThat(adds.size(), is(1));
    assertThat(adds, contains(likeChange()
      .withValues(new Value(addedValue1, STRING), new Value(addedValue2, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getAdditionsIgnoresReplacementsWithOldValue() throws Exception {
    String addedValue = "newValue";
    String oldValue = "oldValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, createPropertyInput(addedValue));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    valuesInQuadStore(NAMES_PRED, oldValue);

    Stream<Change> adds = instance.getAdditions(dataSet);

    assertThat(adds, StreamMatchers.empty());
  }

  @Test
  public void getAdditionsForListIgnoresReplacementsWithOldValue() throws Exception {
    String addedValue1 = "newValue1";
    String addedValue2 = "newValue2";
    String oldValue = "oldValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue1), createPropertyInput(addedValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, oldValue);

    Stream<Change> adds = instance.getAdditions(dataSet);

    assertThat(adds, StreamMatchers.empty());
  }

  @Test
  public void getAdditionsIgnoresReplacementsWithNoValue() throws Exception {
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, null);
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    Stream<Change> adds = instance.getAdditions(dataSet);

    assertThat(adds, StreamMatchers.empty());
  }

  @Test
  public void getAdditionsForListIgnoresReplacementsWithNoValue() throws Exception {
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, newArrayList());
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    Stream<Change> adds = instance.getAdditions(dataSet);

    assertThat(adds, StreamMatchers.empty());
  }

  @Test
  public void getDeletionsReturnsDeletions() throws Exception {
    String deletedValue = "deletedValue";
    Map<Object, Object> deletions = Maps.newHashMap();
    deletions.put(NAMES_FIELD, newArrayList(createPropertyInput(deletedValue)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("deletions", deletions);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, deletedValue);

    List<Change> deletes = instance.getDeletions(dataSet).collect(toList());

    assertThat(deletes.size(), is(1));
    assertThat(deletes, contains(likeChange()
      .valuesIsEmpty()
      .withOldValues(new Value(deletedValue, STRING))
    ));
  }

  @Test
  public void getDeletionsForListReturnsDeletions() throws Exception {
    String existingValue1 = "existingValue1";
    String existingValue2 = "existingValue2";
    Map<Object, Object> deletions = Maps.newHashMap();
    deletions.put(NAMES_FIELD, newArrayList(createPropertyInput(existingValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("deletions", deletions);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, existingValue1, existingValue2);

    List<Change> deletes = instance.getDeletions(dataSet).collect(toList());

    assertThat(deletes.size(), is(1));
    assertThat(deletes, contains(likeChange()
      .valuesIsEmpty()
      .withOldValues(new Value(existingValue2, STRING))
    ));
  }

  @Test
  public void getDeletionsReturnsReplacements() throws Exception {
    String existingValue = "existingValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, null);
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, existingValue);

    List<Change> deletes = instance.getDeletions(dataSet).collect(toList());

    assertThat(deletes.size(), is(1));
    assertThat(deletes, contains(likeChange()
      .valuesIsEmpty()
      .withOldValues(new Value(existingValue, STRING))
    ));
  }

  @Test
  public void getDeletionsForListReturnsReplacements() throws Exception {
    String existingValue1 = "existingValue1";
    String existingValue2 = "existingValue2";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, newArrayList());
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, existingValue1, existingValue2);

    List<Change> deletes = instance.getDeletions(dataSet).collect(toList());

    assertThat(deletes.size(), is(1));
    assertThat(deletes, contains(likeChange()
      .valuesIsEmpty()
      .withOldValues(new Value(existingValue1, STRING), new Value(existingValue2, STRING))
    ));
  }

  @Test
  public void getDeletionsIgnoresReplacementsWithValue() throws Exception {
    String addedValue = "newValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, createPropertyInput(addedValue));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, addedValue);

    Stream<Change> deletes = instance.getDeletions(dataSet);

    assertThat(deletes, StreamMatchers.empty());
  }

  @Test
  public void getDeletionsForListIgnoresReplacementsWithValue() throws Exception {
    String addedValue = "newValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, addedValue);

    Stream<Change> deletes = instance.getDeletions(dataSet);

    assertThat(deletes, StreamMatchers.empty());
  }

  @Test
  public void getDeletionsIgnoresReplacementsWithoutValue() throws Exception {
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, null);
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    Stream<Change> deletes = instance.getDeletions(dataSet);

    assertThat(deletes, StreamMatchers.empty());
  }

  @Test
  public void getReplacementsForListReturnsReplacementsWithOldValues() throws Exception {
    String addedValue1 = "newValue1";
    String addedValue2 = "newValue2";
    String oldValue = "oldValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue1), createPropertyInput(addedValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, oldValue);

    List<Change> reps = instance.getReplacements(dataSet).collect(toList());

    assertThat(reps.size(), is(1));
    assertThat(reps, contains(likeChange()
      .withValues(new Value(addedValue1, STRING), new Value(addedValue2, STRING))
      .withOldValues(new Value(oldValue, STRING))
    ));
  }

  @Test
  public void getReplacementsForSingleValueReturnsReplacementsWithOldValues() throws Exception {
    String addedValue1 = "newValue1";
    String oldValue = "oldValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, createPropertyInput(addedValue1));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, oldValue);

    List<Change> reps = instance.getReplacements(dataSet).collect(toList());

    assertThat(reps.size(), is(1));
    assertThat(reps, contains(likeChange()
      .withValues(new Value(addedValue1, STRING))
      .withOldValues(new Value(oldValue, STRING))
    ));
  }

  @Test
  public void getReplacementsIgnoresReplacementsWithoutValues() throws Exception {
    String addedValue1 = "newValue1";
    String addedValue2 = "newValue2";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue1), createPropertyInput(addedValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);

    Stream<Change> reps = instance.getReplacements(dataSet);

    assertThat(reps, StreamMatchers.empty());
  }

  @Test
  public void getReplacementsIgnoresReplacementsWithEmptyValues() throws Exception {
    String oldValue = "oldValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, null);
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, oldValue);

    Stream<Change> reps = instance.getReplacements(dataSet);

    assertThat(reps, StreamMatchers.empty());
  }

  @Test(expected = IllegalArgumentException.class)
  public void getReplacementsThrowsAnExceptionWhenTheReplacementHasAnUnsupportedValue() throws Exception {
    String addedValue1 = "newValue1";
    String oldValue = "oldValue";
    Map<Object, Object> replacements = Maps.newHashMap();
    replacements.put(NAMES_FIELD, addedValue1);
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("replacements", replacements);
    EditMutationChangeLog instance = new EditMutationChangeLog(SUBJECT, entity);
    valuesInQuadStore(NAMES_PRED, oldValue);

    instance.getReplacements(dataSet).collect(Collectors.toList()); // collect to trigger right exception
  }

  private Map<Object, Object> createPropertyInput(String value) {
    Map<Object, Object> propertyInput = Maps.newHashMap();
    propertyInput.put("type", GRAPH_QL_STRING);
    propertyInput.put("value", value);
    return propertyInput;
  }

  private void valuesInQuadStore(String pred, String... oldValues) {
    when(quadStore.getQuads(SUBJECT, pred, Direction.OUT, "")).thenAnswer(new Answer<Stream<CursorQuad>>() {
      @Override
      public Stream<CursorQuad> answer(InvocationOnMock invocation) {
        List<CursorQuad> quads = newArrayList();
        for (String oldValue : oldValues) {
          quads.add(CursorQuad.create(SUBJECT, pred, Direction.OUT, oldValue, STRING, null, ""));
        }
        return quads.stream();
      }
    });
  }
}
