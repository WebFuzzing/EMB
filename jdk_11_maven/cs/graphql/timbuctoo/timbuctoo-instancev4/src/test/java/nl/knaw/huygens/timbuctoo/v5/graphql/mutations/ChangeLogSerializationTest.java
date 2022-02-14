package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.ChangeLog;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.EditMutationChangeLog;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.ChangeMatcher.likeChange;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeLogSerializationTest {
  private static final String GRAPH_QL_STRING = "xsd_string";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String FIELD = "ex_pred";
  private static final String PRED = "http://example.org/pred";

  private DataSet dataSet;

  @Before
  public void setUp() {
    dataSet = mock(DataSet.class);
    QuadStore quadStore = mock(QuadStore.class);
    when(dataSet.getQuadStore()).thenReturn(quadStore);
    TypeNameStore typeNameStore = mock(TypeNameStore.class);
    when(dataSet.getTypeNameStore()).thenReturn(typeNameStore);
    when(typeNameStore.makeUriForPredicate(FIELD)).thenReturn(Optional.of(tuple(PRED, Direction.OUT)));
    when(typeNameStore.makeUri(GRAPH_QL_STRING)).thenReturn(STRING);

  }

  @Test
  public void editMutationChangeLogIsSerializable() throws Exception {
    String addedValue = "newValue";
    Map<Object, Object> additions = Maps.newHashMap();
    additions.put(FIELD, newArrayList(createPropertyInput(addedValue)));
    Map<Object, Object> entity = Maps.newHashMap();
    entity.put("additions", additions);
    EditMutationChangeLog preSerialization = new EditMutationChangeLog("http://example.org/subj", entity);

    String serialized = OBJECT_MAPPER.writeValueAsString(preSerialization);

    List<Change> postAdditions =
      OBJECT_MAPPER.readValue(serialized, ChangeLog.class).getAdditions(dataSet).collect(Collectors.toList());

    assertThat(postAdditions, contains(
      likeChange()
        .withValues(new Change.Value(addedValue, STRING))
    ));
  }

  private Map<Object, Object> createPropertyInput(String value) {
    Map<Object, Object> propertyInput = Maps.newHashMap();
    propertyInput.put("type", GRAPH_QL_STRING);
    propertyInput.put("value", value);
    return propertyInput;
  }
}
