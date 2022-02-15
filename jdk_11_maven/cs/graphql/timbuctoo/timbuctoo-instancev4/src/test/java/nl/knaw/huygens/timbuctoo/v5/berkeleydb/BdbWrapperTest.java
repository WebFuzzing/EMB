package nl.knaw.huygens.timbuctoo.v5.berkeleydb;

import com.sleepycat.bind.tuple.TupleBinding;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.StringStringIsCleanHandler;
import nl.knaw.huygens.timbuctoo.v5.dropwizard.BdbNonPersistentEnvironmentCreator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Stream;

import static com.sleepycat.bind.tuple.TupleBinding.getPrimitiveBinding;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class BdbWrapperTest {

  private static final StringStringIsCleanHandler IS_CLEAN_HANDLER = new StringStringIsCleanHandler();
  private BdbNonPersistentEnvironmentCreator creator;
  private BdbWrapper<String, String> database;
  private static final TupleBinding<String> STRING_BINDER = getPrimitiveBinding(String.class);

  @Before
  public void setUp() throws Exception {
    creator = new BdbNonPersistentEnvironmentCreator();
    creator.start();

    database = creator.getDatabase("a", "b", "test", true, STRING_BINDER, STRING_BINDER, IS_CLEAN_HANDLER);

    database.put("aa", "bb");
    database.put("ab", "ac");
    database.put("ab", "bb");
    database.put("ab", "bc");
    database.put("ab", "dd");
    database.put("bb", "bb");
  }

  @After
  public void close() {
    creator.stop();
  }

  @Test
  public void putOverwritesTheValueWhenNoDuplicatesAndTheKeyAlreadyHasAValue() throws Exception {
    BdbWrapper<String, String> db = null;
    try {
      boolean allowDuplicates = false;
      db = creator.getDatabase(
        "user",
        "dsWithoutDuplcates",
        "test",
        allowDuplicates,
        STRING_BINDER,
        STRING_BINDER,
        IS_CLEAN_HANDLER
      );

      db.put("key", "value");
      db.put("key", "other");

      Stream<String> stream = db.databaseGetter().key("key").dontSkip().forwards().getValues(db.valueRetriever());
      List<String> values = stream.collect(toList());
      stream.close();
      assertThat(values, contains("other"));
    } finally {
      if (db != null) {
        db.close();
      }
    }
  }

  @Test
  public void putAddsAValueWhenDuplicatesAllowedAndTheKeyAlreadyHasAValue() throws Exception {
    BdbWrapper<String, String> db = null;
    try {
      boolean allowDuplicates = true;
      db = creator.getDatabase(
        "user",
        "dsWithtDuplcates",
        "test",
        allowDuplicates,
        STRING_BINDER,
        STRING_BINDER,
        IS_CLEAN_HANDLER
      );

      db.put("key", "value");
      db.put("key", "other");

      Stream<String> stream = db.databaseGetter().key("key").dontSkip().forwards().getValues(db.valueRetriever());
      List<String> values = stream.collect(toList());
      stream.close();
      assertThat(values, containsInAnyOrder("other", "value"));
    } finally {
      if (db != null) {
        db.close();
      }
    }
  }

  @Test
  public void putReturnsFalseIfTheKeyAndValueAlreadyExistInTheDatabaseAndDuplicatesAreAllowed() throws Exception {
    BdbWrapper<String, String> db = null;
    try {
      boolean allowDuplicates = true;
      db = creator.getDatabase(
        "user",
        "dsWithtDuplcates",
        "test",
        allowDuplicates,
        STRING_BINDER,
        STRING_BINDER,
        IS_CLEAN_HANDLER
      );

      assertThat(db.put("key", "value"), is(true));
      assertThat(db.put("key", "value"), is(false));
    } finally {
      if (db != null) {
        db.close();
      }
    }
  }

  @Test
  public void getAllItems() throws Exception {
    final Stream<String> stream = database.databaseGetter()
      .getAll()
      .getValues(database.valueRetriever());
    final long count = stream
      .count();
    stream.close();
    assertThat(count, is(6L));
  }

  @Test
  public void getAllItemsWithSameKey() throws Exception {
    final Stream<String> stream = database.databaseGetter()
      .key("ab")
      .dontSkip()
      .forwards()
      .getValues(database.valueRetriever());
    final long count = stream
      .count();
    stream.close();
    assertThat(count, is(4L));
  }

  @Test
  public void getAllItemsWithSamePrefix() throws Exception {
    final Stream<String> stream = database.databaseGetter()
      .partialKey("a", (prefix, key) -> key.startsWith(prefix))
      .dontSkip()
      .forwards()
      .getValues(database.valueRetriever());
    final long count = stream
      .count();
    stream.close();
    assertThat(count, is(5L));
  }

  @Test
  public void getItemBackwards() throws Exception {
    final Stream<String> stream = database.databaseGetter()
      .key("ab")
      .skipToEnd()
      .backwards()
      .getValues(database.valueRetriever());
    final long count = stream
      .count();
    stream.close();
    assertThat(count, is(4L));
  }

  @Test
  public void getItemFromValue() throws Exception {
    final Stream<String> stream = database.databaseGetter()
      .key("ab")
      .skipToValue("bc")
      .forwards()
      .getValues(database.valueRetriever());
    final long count = stream
      .count();
    stream.close();
    assertThat(count, is(2L));
  }

  @Test
  public void getItemFromValueRange() throws Exception {
    final Stream<String> stream = database.databaseGetter()
      .key("ab")
      .skipNearValue("b")
      .onlyValuesMatching((prefix, value) -> {
        return value.startsWith(prefix);
      })
      .forwards()
      .getValues(database.valueRetriever());
    final long count = stream
      .count();
    stream.close();
    assertThat(count, is(2L));
  }

  @Test
  public void isCleanWhenTheDatabaseContainsNoData() throws Exception {
    BdbWrapper emptyDatabase = creator.getDatabase(
      "a",
      "b",
      "empty",
      true,
      STRING_BINDER,
      STRING_BINDER,
      IS_CLEAN_HANDLER
    );

    assertThat(emptyDatabase.isClean(), is(true));
  }

  @Test
  public void isCleanWhenTheChangeIsCommitted() throws Exception {
    database.beginTransaction();
    database.put("ab", "cd");
    database.commit();

    assertThat(database.isClean(), is(true));
  }

  @Test
  public void isNotCleanWhenTheChangeNotIsCommitted() throws Exception {
    database.beginTransaction();
    database.put("ab", "cd");

    assertThat(database.isClean(), is(false));
  }

  @Test
  public void isNotCleanAfterANewTransactionHasBegun() throws Exception {
    database.beginTransaction();
    database.put("ab", "cd");
    database.commit();
    database.beginTransaction();

    assertThat(database.isClean(), is(false));
  }

  @Test
  public void emptyRemovesAllDataFromTheDatabase() {
    database.beginTransaction();
    database.empty();
    database.commit();


    try (Stream<String> keys = database.databaseGetter().getAll().getKeys(database.keyRetriever())) {
      assertThat(keys.count(), is(0L));
    }
  }

  @Test
  public void doesNotReturnIsCleanKeyWhenAllDataIsRetrieved() throws Exception {
    database.beginTransaction();
    database.put("ab", "cd");
    database.commit();

    try (Stream<String> keys = database.databaseGetter().getAll().getKeys(database.keyRetriever())) {
      List<String> collect = keys.collect(toList());

      assertThat(collect, not(hasItem(IS_CLEAN_HANDLER.getKey())));
    }

  }

  @Test
  public void doesNotReturnIsCleanValueWhenAllDataIsRetrieved() throws Exception {
    database.beginTransaction();
    database.put("ab", "cd");
    database.commit();

    try (Stream<String> keys = database.databaseGetter().getAll().getValues(database.valueRetriever())) {
      List<String> collect = keys.collect(toList());

      assertThat(collect, not(hasItem(IS_CLEAN_HANDLER.getValue())));
    }
  }

  @Test
  public void doesNotReturnIsCleanWhenAllDataIsRetrieved() throws Exception {
    database.beginTransaction();
    database.put("ab", "cd");
    database.commit();

    try (Stream<Tuple<String,String>> keys = database.databaseGetter().getAll().getKeysAndValues(
      database.keyValueConverter(Tuple::tuple)
    )) {
      List<Tuple<String,String>> collect = keys.collect(toList());

      assertThat(collect, not(hasItem(Tuple.tuple(IS_CLEAN_HANDLER.getKey(),IS_CLEAN_HANDLER.getValue()))));
    }

  }
}
