package nl.knaw.huygens.timbuctoo.server.tasks;

import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTruePatchStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.UpdatedPerPatchStore;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.BdbNonPersistentEnvironmentCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MigrateTruePatchStoresTaskTest {
  private BdbNonPersistentEnvironmentCreator creator;
  private BdbTruePatchStore.DatabaseCreator databaseCreator;
  private MigrateTruePatchStoresTask instance;

  @Before
  public void setUp() throws Exception {
    creator = new BdbNonPersistentEnvironmentCreator();
    creator.start();

    databaseCreator = version -> creator.getDatabase(
        "user",
        "dataSet",
        "truePatch" + version,
        true,
        TupleBinding.getPrimitiveBinding(String.class),
        TupleBinding.getPrimitiveBinding(String.class),
        new StringStringIsCleanHandler()
    );

    UpdatedPerPatchStore updatedPerPatchStore = mock(UpdatedPerPatchStore.class);
    when(updatedPerPatchStore.getVersions()).thenReturn(IntStream.of(0, 1, 2).boxed());

    BdbTruePatchStore truePatchStore = new BdbTruePatchStore(databaseCreator, updatedPerPatchStore);

    DataSetRepository dataSetRepository = mock(DataSetRepository.class);
    DataSet dataSet = mock(DataSet.class);
    DataSetMetaData dataSetMetaData = mock(DataSetMetaData.class);

    when(dataSetRepository.getDataSets()).thenReturn(Collections.singletonList(dataSet));
    when(dataSet.getMetadata()).thenReturn(dataSetMetaData);
    when(dataSet.getTruePatchStore()).thenReturn(truePatchStore);
    when(dataSetMetaData.getCombinedId()).thenReturn("user__dataSet");

    instance = new MigrateTruePatchStoresTask(dataSetRepository);
  }

  @After
  public void close() {
    creator.stop();
  }

  @Test
  public void executeMigration() throws Exception {
    BdbWrapper<String, String> originalDatabase = databaseCreator.createDatabase("");

    Tuple<String, String> tuple1V0 = new Tuple<>("s1\n0\n1", "p1\n1\n\n\no1");
    Tuple<String, String> tuple2V0 = new Tuple<>("s2\n0\n1", "p2\n1\n\n\no2");
    Tuple<String, String> tuple3V0 = new Tuple<>("s3\n0\n1", "p3\n1\n\n\no3");

    Tuple<String, String> tuple4V1 = new Tuple<>("s4\n1\n1", "p4\n1\n\n\no4");
    Tuple<String, String> tuple5V1 = new Tuple<>("s5\n1\n1", "p5\n1\n\n\no5");
    Tuple<String, String> tuple6V1 = new Tuple<>("s6\n1\n1", "p6\n1\n\n\no6");

    Tuple<String, String> tuple7V2 = new Tuple<>("s7\n2\n1", "p7\n1\n\n\no7");
    Tuple<String, String> tuple8V2 = new Tuple<>("s8\n2\n1", "p8\n1\n\n\no8");
    Tuple<String, String> tuple9V2 = new Tuple<>("s9\n2\n1", "p9\n1\n\n\no9");

    originalDatabase.put(tuple1V0.getLeft(), tuple1V0.getRight());
    originalDatabase.put(tuple2V0.getLeft(), tuple2V0.getRight());
    originalDatabase.put(tuple3V0.getLeft(), tuple3V0.getRight());

    originalDatabase.put(tuple4V1.getLeft(), tuple4V1.getRight());
    originalDatabase.put(tuple5V1.getLeft(), tuple5V1.getRight());
    originalDatabase.put(tuple6V1.getLeft(), tuple6V1.getRight());

    originalDatabase.put(tuple7V2.getLeft(), tuple7V2.getRight());
    originalDatabase.put(tuple8V2.getLeft(), tuple8V2.getRight());
    originalDatabase.put(tuple9V2.getLeft(), tuple9V2.getRight());

    originalDatabase.commit();
    originalDatabase.close();

    instance.execute(Map.of(), mock(PrintWriter.class));

    originalDatabase = databaseCreator.createDatabase("");
    List<Tuple<String, String>> inOrgDb = getTuples(originalDatabase);
    assertThat(inOrgDb.size(), equalTo(0));

    BdbWrapper<String, String> databaseV0 = databaseCreator.createDatabase("0");
    List<Tuple<String, String>> inDbV0 = getTuples(databaseV0);
    assertThat(inDbV0.size(), equalTo(3));
    assertThat(inDbV0, contains(tuple1V0, tuple2V0, tuple3V0));

    BdbWrapper<String, String> databaseV1 = databaseCreator.createDatabase("1");
    List<Tuple<String, String>> inDbV1 = getTuples(databaseV1);
    assertThat(inDbV1.size(), equalTo(3));
    assertThat(inDbV1, contains(tuple4V1, tuple5V1, tuple6V1));

    BdbWrapper<String, String> databaseV2 = databaseCreator.createDatabase("2");
    List<Tuple<String, String>> inDbV2 = getTuples(databaseV2);
    assertThat(inDbV2.size(), equalTo(3));
    assertThat(inDbV2, contains(tuple7V2, tuple8V2, tuple9V2));

    originalDatabase.close();
    databaseV0.close();
    databaseV1.close();
    databaseV2.close();
  }

  private static List<Tuple<String, String>> getTuples(BdbWrapper<String, String> database) {
    return database
        .databaseGetter().getAll()
        .getKeysAndValues(database.keyValueConverter(Tuple::tuple))
        .collect(Collectors.toList());
  }
}
