package nl.knaw.huygens.timbuctoo.v5.berkeleydb;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.StatsConfig;
import com.sleepycat.je.Transaction;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean.IsCleanHandler;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.sleepycat.je.OperationStatus.SUCCESS;
import static org.slf4j.LoggerFactory.getLogger;

public class BdbWrapper<KeyT, ValueT> {
  private static final Logger LOG = getLogger(BdbWrapper.class);
  private final Environment dbEnvironment;
  private final Database database;
  private final DatabaseConfig databaseConfig;
  private final EntryBinding<KeyT> keyBinder;
  private final EntryBinding<ValueT> valueBinder;
  private final IsCleanHandler<KeyT, ValueT> isCleanHandler;
  private final DatabaseEntry keyEntry = new DatabaseEntry();
  private final DatabaseEntry valueEntry = new DatabaseEntry();
  private final Map<Cursor, String> cursors = new HashMap<>();
  private Transaction transaction;

  public BdbWrapper(Environment dbEnvironment, Database database, DatabaseConfig databaseConfig,
                    EntryBinding<KeyT> keyBinder, EntryBinding<ValueT> valueBinder,
                    IsCleanHandler<KeyT, ValueT> isCleanHandler) {
    this.dbEnvironment = dbEnvironment;
    this.database = database;
    this.databaseConfig = databaseConfig;
    this.keyBinder = keyBinder;
    this.valueBinder = valueBinder;
    this.isCleanHandler = isCleanHandler;
  }

  public void beginTransaction() {
    if (databaseConfig.getTransactional()) {
      transaction = dbEnvironment.beginTransaction(null, null);
    }

    try (Stream<KeyT> keys = databaseGetter().getAll().getKeys(new NonFilteringKeyRetriever<>())) {
      if (keys.findAny().isPresent()) {
        try {
          boolean success = delete(isCleanHandler.getKey(), isCleanHandler.getValue());
          if (!success) {
            LOG.error("Could not remove 'isClean' property");
          }
        } catch (DatabaseWriteException e) {
          LOG.error("Could not remove 'isClean' property", e);
        }
      }
    }
  }

  public void close() {
    if (transaction != null) {
      transaction.abort();
    }
    for (Map.Entry<Cursor, String> cursor : cursors.entrySet()) {
      cursor.getKey().close();
      LOG.error("Cursor was not closed. It was opened at: \n" + cursor.getValue());
    }

    database.close();
  }

  public DatabaseGetter.Builder<KeyT, ValueT> databaseGetter() {
    return DatabaseGetter.databaseGetter(
      keyBinder,
      valueBinder,
      database,
      cursors,
      isCleanHandler.getKey(),
      isCleanHandler.getValue());
  }

  public boolean isClean() {
    try (Stream<KeyT> keys = databaseGetter().getAll().getKeys(new NonFilteringKeyRetriever<>())) {
      if (!keys.findAny().isPresent()) { // database is empty so it is clean
        return true;
      }
    }

    try (Stream<ValueT> values = databaseGetter().key(isCleanHandler.getKey()).dontSkip().forwards().getValues(
      new NonFilteringValueRetriever<>())) {
      Optional<ValueT> first = values.findFirst();
      if (first.isPresent()) {
        ValueT value = first.get();
        return Objects.equals(value, isCleanHandler.getValue());
      }

    }
    return false;
  }

  public void commit() {
    if (transaction != null) {
      transaction.commit();
    }

    try {
      boolean success = this.put(isCleanHandler.getKey(), isCleanHandler.getValue());
      if (!success) {
        LOG.error("Could not add 'isClean' property");
      }
    } catch (DatabaseWriteException e) {
      LOG.error("Could not add 'isClean' property for database '" + database.getDatabaseName() + "'", e);
    }

    database.sync();
    dbEnvironment.sync(); // needed for better recoverability
  }

  public void replace(KeyT key, ValueT initialValue, Function<ValueT, ValueT> replacer) throws DatabaseWriteException {
    synchronized (keyEntry) {
      try (Cursor cursor = database.openCursor(transaction, CursorConfig.DEFAULT)) {
        keyBinder.objectToEntry(key, keyEntry);
        OperationStatus searchResult = cursor.getSearchKey(keyEntry, valueEntry, LockMode.DEFAULT);
        ValueT newValue = initialValue;
        if (searchResult.equals(OperationStatus.SUCCESS)) {
          newValue = replacer.apply(valueBinder.entryToObject(valueEntry));
        }
        valueBinder.objectToEntry(newValue, valueEntry);
        cursor.putCurrent(valueEntry);
      } catch (Exception e) {
        throw new DatabaseWriteException(e);
      }
    }
  }

  public boolean put(KeyT key, ValueT value) throws DatabaseWriteException {
    synchronized (keyEntry) {
      try {
        keyBinder.objectToEntry(key, keyEntry);
        if (databaseConfig.getSortedDuplicates()) {
          valueBinder.objectToEntry(value, valueEntry);
          OperationStatus operationStatus = database.putNoDupData(transaction, keyEntry, valueEntry);
          // operation status is only SUCCESS if the data was not in the database before
          return operationStatus.equals(OperationStatus.SUCCESS);
        } else {
          try (Cursor cursor = database.openCursor(transaction, CursorConfig.DEFAULT)) {
            OperationStatus searchResult = cursor.getSearchKey(keyEntry, valueEntry, LockMode.DEFAULT);
            if (searchResult == SUCCESS && Objects.equals(value, valueBinder.entryToObject(valueEntry))) {
              return false;
            } else {
              valueBinder.objectToEntry(value, valueEntry);
              database.put(transaction, keyEntry, valueEntry); // returns OperationStatus.SUCCESS or throws an exception
              return true;
            }
          }
        }
      } catch (Exception e) {
        throw new DatabaseWriteException(e);
      }
    }
  }

  public boolean delete(KeyT key, ValueT value) throws DatabaseWriteException {
    boolean wasChange = false;
    synchronized (keyEntry) {
      try (Cursor cursor = database.openCursor(transaction, CursorConfig.DEFAULT)) {
        keyBinder.objectToEntry(key, keyEntry);
        valueBinder.objectToEntry(value, valueEntry);
        OperationStatus searchResult = cursor.getSearchBoth(keyEntry, valueEntry, LockMode.DEFAULT);
        if (searchResult.equals(OperationStatus.SUCCESS)) {
          wasChange = cursor.delete() == OperationStatus.SUCCESS;
        }
      } catch (Exception e) {
        throw new DatabaseWriteException(e);
      }
    }
    return wasChange;
  }

  public List<String> dump(String prefix, int start, int count, LockMode lockMode) {
    EntryBinding<String> binder = TupleBinding.getPrimitiveBinding(String.class);
    DatabaseEntry key = new DatabaseEntry();
    binder.objectToEntry(prefix, key);
    DatabaseEntry value = new DatabaseEntry();

    Cursor cursor = database.openCursor(null, null);
    OperationStatus status = cursor.getSearchKeyRange(key, value, LockMode.READ_UNCOMMITTED);
    List<String> result = new ArrayList<>();
    int index = 0;
    while (status == OperationStatus.SUCCESS && index < (start + count)) {
      if (index >= start) {
        result.add(
          binder.entryToObject(key) + " -> " + binder.entryToObject(value)
        );
      }
      index++;
      status = cursor.getNext(key, value, LockMode.READ_UNCOMMITTED);
    }
    cursor.close();
    return result;
  }

  public void empty() {
    synchronized (keyEntry) {
      try (Cursor cursor = database.openCursor(null, null)) {
        while (cursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
          cursor.delete();
        }
      }
    }
  }

  public <U> KeyValueConverter<KeyT, ValueT, U> keyValueConverter(BiFunction<KeyT, ValueT, U> converter) {
    return new CleanFilteringKeyValueConverter<>(converter);
  }

  public KeyRetriever<KeyT> keyRetriever() {
    return new CleanFilteringKeyRetriever<>();
  }

  public ValueRetriever<ValueT> valueRetriever() {
    return new CleanFilteringValueRetriever<>();
  }

  public interface KeyValueConverter<KeyT, ValueT, U> {
    boolean filter(Tuple<KeyT, ValueT> keyValue);

    U convert(Tuple<KeyT, ValueT> keyValue);
  }

  public interface KeyRetriever<KeyT> {

    boolean filter(Tuple<KeyT, ?> keyValue);


    KeyT get(Tuple<KeyT, ?> keyValue);
  }

  public interface ValueRetriever<ValueT> {
    boolean filter(Tuple<?, ValueT> keyValue);


    ValueT get(Tuple<?, ValueT> keyValue);
  }

  private class NonFilteringKeyRetriever<FKeyT> implements KeyRetriever<FKeyT> {
    @Override
    public boolean filter(Tuple<FKeyT, ?> keyValue) {
      return true;
    }

    @Override
    public FKeyT get(Tuple<FKeyT, ?> keyValue) {
      return keyValue.getLeft();
    }
  }

  private class CleanFilteringKeyRetriever<FKeyT> implements KeyRetriever<FKeyT> {
    @Override
    public boolean filter(Tuple<FKeyT, ?> keyValue) {
      return !(isCleanHandler.getKey().equals(keyValue.getLeft()) &&
        isCleanHandler.getValue().equals(keyValue.getRight()));
    }

    @Override
    public FKeyT get(Tuple<FKeyT, ?> keyValue) {
      return keyValue.getLeft();
    }
  }

  private class CleanFilteringKeyValueConverter<FKeyT, FValueT, U> implements KeyValueConverter<FKeyT, FValueT, U> {
    private final BiFunction<FKeyT, FValueT, U> converter;

    public CleanFilteringKeyValueConverter(BiFunction<FKeyT, FValueT, U> converter) {
      this.converter = converter;
    }

    @Override
    public boolean filter(Tuple<FKeyT, FValueT> keyValue) {
      return !(isCleanHandler.getKey().equals(keyValue.getLeft()) &&
        isCleanHandler.getValue().equals(keyValue.getRight()));
    }

    @Override
    public U convert(Tuple<FKeyT, FValueT> keyValue) {
      return converter.apply(keyValue.getLeft(), keyValue.getRight());
    }
  }

  private class CleanFilteringValueRetriever<FValueT> implements ValueRetriever<FValueT> {
    @Override
    public boolean filter(Tuple<?, FValueT> keyValue) {
      return !(isCleanHandler.getKey().equals(keyValue.getLeft()) &&
        isCleanHandler.getValue().equals(keyValue.getRight()));
    }

    @Override
    public FValueT get(Tuple<?, FValueT> keyValue) {
      return keyValue.getRight();
    }
  }

  private class NonFilteringValueRetriever<FValueT> implements ValueRetriever<FValueT> {
    @Override
    public boolean filter(Tuple<?, FValueT> keyValue) {
      return true;
    }

    @Override
    public FValueT get(Tuple<?, FValueT> keyValue) {
      return keyValue.getRight();
    }
  }
}
