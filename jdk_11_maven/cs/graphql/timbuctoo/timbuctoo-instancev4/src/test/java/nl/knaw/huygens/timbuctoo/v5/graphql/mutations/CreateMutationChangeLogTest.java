package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import co.unruly.matchers.StreamMatchers;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.CreateMutationChangeLog;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ChangeMatcher.likeChange;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateMutationChangeLogTest {
  private static final String TYPE_URI = "http://schema.org/Person";
  private static final String SUBJECT = "http://example.org/subject";
  private static final String NAMES_FIELD = "schema_name";
  private static final String NAMES_PRED = "http://schema.org/name";
  private static final String GRAPH_QL_STRING = "xsd_string";
  private DataSet dataSet;
  private TypeNameStore typeNameStore;

  @Before
  public void setUp() throws Exception {
    dataSet = mock(DataSet.class);
    typeNameStore = mock(TypeNameStore.class);
    when(typeNameStore.makeUriForPredicate(NAMES_FIELD)).thenReturn(Optional.of(tuple(NAMES_PRED, Direction.OUT)));
    when(typeNameStore.makeUri(GRAPH_QL_STRING)).thenReturn(STRING);
    when(dataSet.getTypeNameStore()).thenReturn(typeNameStore);
  }

  @Test
  public void getAdditionsReturnsCreations() throws Exception {
    String addedValue = "newValue";
    Map<Object, Object> creations = Maps.newHashMap();
    creations.put(NAMES_FIELD, createPropertyInput(addedValue));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("creations", creations);
    CreateMutationChangeLog instance = new CreateMutationChangeLog(SUBJECT, TYPE_URI, entity);

    List<Change> adds = instance.getAdditions(dataSet).collect(toList());

    assertThat(adds.size(), is(2));
    assertThat(adds.get(0), is(likeChange()
      .withValues(new Value(TYPE_URI, null))
      .oldValuesIsEmpty()
    ));
    assertThat(adds.get(1), is(likeChange()
      .withValues(new Value(addedValue, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getAdditionsForListReturnsCreations() throws Exception {
    String addedValue1 = "newValue1";
    String addedValue2 = "newValue2";
    Map<Object, Object> creations = Maps.newHashMap();
    creations.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue1), createPropertyInput(addedValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("creations", creations);
    CreateMutationChangeLog instance = new CreateMutationChangeLog(SUBJECT, TYPE_URI, entity);

    List<Change> adds = instance.getAdditions(dataSet).collect(toList());

    assertThat(adds.size(), is(2));
    assertThat(adds.get(0), is(likeChange()
      .withValues(new Value(TYPE_URI, null))
      .oldValuesIsEmpty()
    ));
    assertThat(adds.get(1), is(likeChange()
      .withValues(new Value(addedValue1, STRING), new Value(addedValue2, STRING))
      .oldValuesIsEmpty()
    ));
  }

  @Test
  public void getDeletionsReturnsNothing() throws Exception {
    String addedValue1 = "newValue1";
    String addedValue2 = "newValue2";
    Map<Object, Object> creations = Maps.newHashMap();
    creations.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue1), createPropertyInput(addedValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("creations", creations);
    CreateMutationChangeLog instance = new CreateMutationChangeLog(SUBJECT, TYPE_URI, entity);

    Stream<Change> deletes = instance.getDeletions(dataSet);

    assertThat(deletes, StreamMatchers.empty());
  }

  @Test
  public void getReplacementsReturnsNothing() throws Exception {
    String addedValue1 = "newValue1";
    String addedValue2 = "newValue2";
    Map<Object, Object> creations = Maps.newHashMap();
    creations.put(NAMES_FIELD, newArrayList(createPropertyInput(addedValue1), createPropertyInput(addedValue2)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("creations", creations);
    CreateMutationChangeLog instance = new CreateMutationChangeLog(SUBJECT, TYPE_URI, entity);

    Stream<Change> replacements = instance.getReplacements(dataSet);

    assertThat(replacements, StreamMatchers.empty());
  }

  private Map<Object, Object> createPropertyInput(String value) {
    Map<Object, Object> propertyInput = Maps.newHashMap();
    propertyInput.put("type", GRAPH_QL_STRING);
    propertyInput.put("value", value);
    return propertyInput;
  }
}
