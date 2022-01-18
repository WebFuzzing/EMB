package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import co.unruly.matchers.StreamMatchers;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.Change.Value;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.DeleteMutationChangeLog;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;
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

public class DeleteMutationChangeLogTest {
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
  public void getAdditionsReturnsNothing() throws Exception {
    DeleteMutationChangeLog instance = new DeleteMutationChangeLog(SUBJECT, null);

    Stream<Change> additions = instance.getAdditions(dataSet);

    assertThat(additions, StreamMatchers.empty());
  }

  @Test
  public void getDeletionsReturnsDeletions() throws Exception {
    String existingValue1 = "existingValue1";
    String existingValue2 = "existingValue2";
    DeleteMutationChangeLog instance = new DeleteMutationChangeLog(SUBJECT, null);
    valuesInQuadStore(NAMES_PRED, existingValue1, existingValue2);

    List<Change> deletions = instance.getDeletions(dataSet).collect(toList());

    assertThat(deletions.size(), is(1));
    assertThat(deletions, contains(likeChange()
      .valuesIsEmpty()
      .withOldValues(new Value(existingValue1, STRING), new Value(existingValue2, STRING))
    ));
  }

  @Test
  public void getReplacementsReturnsNothing() throws Exception {
    DeleteMutationChangeLog instance = new DeleteMutationChangeLog(SUBJECT, null);

    Stream<Change> replacements = instance.getReplacements(dataSet);

    assertThat(replacements, StreamMatchers.empty());
  }

  private void valuesInQuadStore(String pred, String... oldValues) {
    when(quadStore.getQuads(SUBJECT)).thenAnswer(new Answer<Stream<CursorQuad>>() {
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
