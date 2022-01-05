package nl.knaw.huygens.timbuctoo.v5.datastores.rssource;

import com.google.common.collect.Lists;
import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.dataset.ChangesRetriever;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTruePatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.UpdatedPerPatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.BdbNonPersistentEnvironmentCreator;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.junit.Test;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ChangeListBuilderTest {

  @Test
  public void retrieveChangeFilesNamesReturnsCorrectNamesBasedOnSuppliedVersions() {
    ChangeListBuilder changeListBuilder = new ChangeListBuilder("graph");

    Supplier<List<Integer>> versionsSupplier = () -> Lists.newArrayList(1, 2);

    List<String> changeFileNames = changeListBuilder.retrieveChangeFileNames(versionsSupplier);

    assertThat(changeFileNames, contains("changes1.nqud", "changes2.nqud"));
  }

  @Test
  public void retrieveChangesReturnsQuadsForGivenVersionAndSubjects() throws Exception {
    UpdatedPerPatchStore updatedPerPatchStore = mock(UpdatedPerPatchStore.class);
    when(updatedPerPatchStore.getVersions()).thenReturn(IntStream.of(1).boxed());

    BdbNonPersistentEnvironmentCreator dataStoreFactory = new BdbNonPersistentEnvironmentCreator();
    BdbTruePatchStore bdbTruePatchStore = new BdbTruePatchStore(version -> dataStoreFactory.getDatabase(
      "user",
      "dataSet",
      "schema" + version,
      false,
      TupleBinding.getPrimitiveBinding(String.class),
      TupleBinding.getPrimitiveBinding(String.class),
      new StringStringIsCleanHandler()
    ), updatedPerPatchStore);

    int version = 1;
    bdbTruePatchStore.put("s1", version, "p1", Direction.OUT, true, "o1", null, null);
    bdbTruePatchStore.put("s2", version, "p2", Direction.OUT, false, "o2", null, null);
    bdbTruePatchStore.put("s3", version, "p3", Direction.OUT, true, "o3", RdfConstants.STRING, null);
    bdbTruePatchStore.put("s4", version, "p4", Direction.OUT, false, "o4", RdfConstants.STRING, null);
    bdbTruePatchStore.put("s5", version, "p5", Direction.OUT, true, "o5", RdfConstants.LANGSTRING, "en");
    bdbTruePatchStore.put("s6", version, "p6", Direction.OUT, false, "o6", RdfConstants.LANGSTRING, "en");

    ChangeListBuilder changeListBuilder = new ChangeListBuilder("graph");
    ChangesRetriever changesRetriever = new ChangesRetriever(bdbTruePatchStore, null);

    List<String> changes = changeListBuilder.retrieveChanges(changesRetriever, version).collect(Collectors.toList());

    assertThat(changes, containsInAnyOrder(
      "+<s1> <p1> <o1> <graph> .\n",
      "-<s2> <p2> <o2> <graph> .\n",
      "+<s3> <p3> \"o3\"^^<http://www.w3.org/2001/XMLSchema#string> <graph> .\n",
      "-<s4> <p4> \"o4\"^^<http://www.w3.org/2001/XMLSchema#string> <graph> .\n",
      "+<s5> <p5> \"o5\"@en <graph> .\n",
      "-<s6> <p6> \"o6\"@en <graph> .\n"
    ));
  }
}
