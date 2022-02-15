package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.BdbNonPersistentEnvironmentCreator;
import org.junit.Test;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.OUT;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeFetcherImplTest {

  @Test
  public void showsAdditions() throws Exception {
    UpdatedPerPatchStore updatedPerPatchStore = mock(UpdatedPerPatchStore.class);
    when(updatedPerPatchStore.getVersions()).thenReturn(IntStream.of(0, 1).boxed());

    final BdbNonPersistentEnvironmentCreator databaseCreator = new BdbNonPersistentEnvironmentCreator();
    final BdbTripleStore bdbTripleStore = new BdbTripleStore(databaseCreator.getDatabase(
      "a",
      "b",
      "rdfData",
      true,
      TupleBinding.getPrimitiveBinding(String.class),
      TupleBinding.getPrimitiveBinding(String.class),
      new StringStringIsCleanHandler()
    ));
    final BdbTruePatchStore truePatchStore = new BdbTruePatchStore(version -> databaseCreator.getDatabase(
      "a",
      "b",
      "truePatch" + version,
      true,
      TupleBinding.getPrimitiveBinding(String.class),
      TupleBinding.getPrimitiveBinding(String.class),
      new StringStringIsCleanHandler()
    ), updatedPerPatchStore);

    bdbTripleStore.putQuad("subj", "pred", OUT, "obj", null, null);
    truePatchStore.put("subj", 0, "pred", OUT, true, "obj", null, null);

    ChangeFetcherImpl changeFetcher = new ChangeFetcherImpl(truePatchStore, bdbTripleStore, 0);

    try (Stream<CursorQuad> predicates = changeFetcher.getPredicates("subj", "pred", OUT, false, false, true)) {
      assertThat(predicates.count(), is(1L));
    }
    try (Stream<CursorQuad> predicates = changeFetcher.getPredicates("subj", "pred", OUT, true, true, true)) {
      assertThat(predicates.count(), is(1L));
    }
    try (Stream<CursorQuad> predicates = changeFetcher.getPredicates("subj", "pred", OUT, true, false, true)) {
      assertThat(predicates.count(), is(1L));
    }

    bdbTripleStore.putQuad("subj", "pred", OUT, "obj2", null, null);
    truePatchStore.put("subj", 1, "pred", OUT, true, "obj2", null, null);
    changeFetcher = new ChangeFetcherImpl(truePatchStore, bdbTripleStore, 1);

    try (Stream<CursorQuad> predicates = changeFetcher.getPredicates("subj", "pred", OUT, false, false, true)) {
      assertThat(predicates.count(), is(1L));
    }
    try (Stream<CursorQuad> predicates = changeFetcher.getPredicates("subj", "pred", OUT, true, true, true)) {
      assertThat(predicates.count(), is(2L));
    }
    try (Stream<CursorQuad> predicates = changeFetcher.getPredicates("subj", "pred", OUT, true, false, true)) {
      assertThat(predicates.count(), is(1L));
    }
  }
}
