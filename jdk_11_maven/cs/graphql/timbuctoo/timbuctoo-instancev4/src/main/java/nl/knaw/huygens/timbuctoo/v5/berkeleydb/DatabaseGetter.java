package nl.knaw.huygens.timbuctoo.v5.berkeleydb;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper.KeyRetriever;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper.KeyValueConverter;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper.ValueRetriever;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.util.StreamIterator.stream;
import static org.slf4j.LoggerFactory.getLogger;

public class DatabaseGetter<KeyT, ValueT> {
  private final DatabaseEntry key;
  private final DatabaseEntry value;
  private final EntryBinding<KeyT> keyBinder;
  private final EntryBinding<ValueT> valueBinder;
  private final DatabaseFunction initializer;
  private final DatabaseFunction iterator;
  private final Database database;
  private final Map<Cursor, String> cursors;

  private static final Logger LOG = getLogger(DatabaseGetter.class);

  DatabaseGetter(EntryBinding<KeyT> keyBinder, EntryBinding<ValueT> valueBinder, DatabaseFunction initializer,
                 DatabaseFunction iterator, Database database, Map<Cursor, String> cursors, DatabaseEntry key,
                 DatabaseEntry value) {
    this.keyBinder = keyBinder;
    this.valueBinder = valueBinder;
    this.initializer = initializer;
    this.iterator = iterator;
    this.database = database;
    this.cursors = cursors;
    this.key = key;
    this.value = value;
  }

  public Stream<KeyT> getKeys(KeyRetriever<KeyT> keyRetriever) {
    return getItems(() -> Tuple.tuple(keyBinder.entryToObject(key), valueBinder.entryToObject(value)))
      .filter(keyRetriever::filter).map(keyRetriever::get);
  }

  public Stream<ValueT> getValues(ValueRetriever<ValueT> valueRetriever) {
    return getItems(() -> Tuple.tuple(keyBinder.entryToObject(key), valueBinder.entryToObject(value)))
      .filter(valueRetriever::filter).map(valueRetriever::get);
  }

  public <U> Stream<U> getKeysAndValues(KeyValueConverter<KeyT, ValueT, U> keyValueConverter) {
    return getItems(() -> Tuple.tuple(keyBinder.entryToObject(key), valueBinder.entryToObject(value)))
      .filter(keyValueConverter::filter).map(keyValueConverter::convert);
  }

  private <U> Stream<U> getItems(Supplier<U> valueMaker) {
    CursorIterator<U> data = new CursorIterator<>(
      initializer,
      iterator,
      valueMaker,
      ExceptionUtils.getStackTrace(new Throwable())
    );

    return stream(data).onClose(() -> {
      try {
        if (data.cursor != null) {
          data.cursor.close();
          cursors.remove(data.cursor);
        }
      } catch (DatabaseException e) {
        LOG.error("Could not close cursor", e);
      }
    });
  }

  public static <KeyT, ValueT> Builder<KeyT, ValueT> databaseGetter(EntryBinding<KeyT> keyBinder,
                                                                    EntryBinding<ValueT> valueBinder,
                                                                    Database database,
                                                                    Map<Cursor, String> cursors,
                                                                    KeyT keyToIgnore,
                                                                    ValueT valueToIgnore) {
    return new DatabaseGetterBuilderImpl<>(keyBinder, valueBinder, database, cursors, keyToIgnore, valueToIgnore);
  }

  private interface DatabaseFunctionMaker<KeyT, ValueT> {
    DatabaseFunction make(DatabaseEntry keyEntry, DatabaseEntry valueEntry, DatabaseFunction iterator,
                          EntryBinding<KeyT> keyBinder, EntryBinding<ValueT> valueBinder);
  }

  public interface Builder<KeyT, ValueT> {
    /**
     * Get all entries in the database, in sorted order
     */
    DatabaseGetter<KeyT, ValueT> getAll();

    /**
     * The resulting getter will only return items that have this exact key
     */
    ScopedBuilder<KeyT, ValueT> key(KeyT key);

    /**
     * The resulting getter will return items for which the isNearEnough function returns true, starting with the
     * item whose key is greater or equal to "key"
     */
    ScopedBuilder<KeyT, ValueT> partialKey(KeyT key, BiFunction<KeyT, KeyT, Boolean> isNearEnough);
  }

  public interface ScopedBuilder<KeyT, ValueT> {
    /**
     * move to the start of the items with the aforementioned key (you probably want to iterate forwards)
     */
    PrimedBuilder<KeyT, ValueT> dontSkip();

    /**
     * start at a specific value, or don't iterate at all if the value is not available
     */
    PrimedBuilder<KeyT, ValueT> skipToValue(ValueT value);

    /**
     * start to the first item that is sorted after the value, or at the value itself
     */
    PartialValueBuilder<KeyT, ValueT> skipNearValue(ValueT value);

    /**
     * move to the end of the items with the aforementioned key (you probably want to iterate backwards)
     */
    PrimedBuilder<KeyT, ValueT> skipToEnd();

  }

  public interface PartialValueBuilder<KeyT, ValueT> {
    /**
     * return all items with the aforementioned key
     */
    PrimedBuilder<KeyT, ValueT> allValues();

    /**
     * return only items whose value fullfills the isNearEnough function
     */
    PrimedBuilder<KeyT, ValueT> onlyValuesMatching(BiFunction<ValueT, ValueT, Boolean> isNearEnough);
  }

  public interface PrimedBuilder<KeyT, ValueT> {

    /**
     * Skip the first item (can be called more then once)
     */
    PrimedBuilder<KeyT, ValueT> skipOne();

    DatabaseGetter<KeyT, ValueT> direction(Iterate direction);

    DatabaseGetter<KeyT, ValueT> forwards();

    DatabaseGetter<KeyT, ValueT> backwards();
  }

  public enum Iterate {
    FORWARDS,
    BACKWARDS
  }

  public static class DatabaseGetterBuilderImpl<KeyT, ValueT> implements Builder<KeyT, ValueT>,
    ScopedBuilder<KeyT, ValueT>, PrimedBuilder<KeyT, ValueT>, PartialValueBuilder<KeyT, ValueT> {

    private final EntryBinding<KeyT> keyBinder;
    private final EntryBinding<ValueT> valueBinder;
    private final Database database;
    private final Map<Cursor, String> cursors;
    private final KeyT keyToIgnore;
    private final ValueT valueToIgnore;

    private KeyT key = null;
    private BiFunction<KeyT, KeyT, Boolean> keyCheck = null;

    private ValueT startValue = null;
    private boolean partialValue = false;
    private BiFunction<ValueT, ValueT, Boolean> valueCheck = null;

    private boolean startAtEnd = false;

    private int skipCount = 0;

    public DatabaseGetterBuilderImpl(EntryBinding<KeyT> keyBinder, EntryBinding<ValueT> valueBinder, Database database,
                                     Map<Cursor, String> cursors, KeyT keyToIgnore, ValueT valueToIgnore) {
      this.keyBinder = keyBinder;
      this.valueBinder = valueBinder;
      this.database = database;
      this.cursors = cursors;
      this.keyToIgnore = keyToIgnore;
      this.valueToIgnore = valueToIgnore;
    }

    @Override
    public DatabaseGetter<KeyT, ValueT> getAll() {
      DatabaseEntry key = new DatabaseEntry();
      DatabaseEntry value = new DatabaseEntry();

      DatabaseFunction iterator = dbCursor -> dbCursor.getNext(key, value, LockMode.DEFAULT);

      return new DatabaseGetter<>(
        keyBinder,
        valueBinder,
        iterator,
        iterator,
        database,
        cursors,
        key, value);
    }

    @Override
    public ScopedBuilder<KeyT, ValueT> key(KeyT key) {
      this.key = key;
      return this;
    }

    @Override
    public ScopedBuilder<KeyT, ValueT> partialKey(KeyT key, BiFunction<KeyT, KeyT, Boolean> isNearEnough) {
      this.key = key;
      this.keyCheck = isNearEnough;
      return this;
    }

    @Override
    public PrimedBuilder<KeyT, ValueT> skipOne() {
      skipCount++;
      return this;
    }

    @Override
    public DatabaseGetter<KeyT, ValueT> forwards() {
      return direction(Iterate.FORWARDS);
    }

    @Override
    public DatabaseGetter<KeyT, ValueT> backwards() {
      return direction(Iterate.BACKWARDS);
    }

    @Override
    public PrimedBuilder<KeyT, ValueT> dontSkip() {
      return this;
    }

    @Override
    public PrimedBuilder<KeyT, ValueT> skipToValue(ValueT value) {
      this.startValue = value;
      return this;
    }

    @Override
    public PartialValueBuilder<KeyT, ValueT> skipNearValue(ValueT value) {
      this.startValue = value;
      this.partialValue = true;
      return this;
    }

    @Override
    public PrimedBuilder<KeyT, ValueT> skipToEnd() {
      this.startAtEnd = true;
      return this;
    }

    @Override
    public PrimedBuilder<KeyT, ValueT> allValues() {
      return this;
    }

    @Override
    public PrimedBuilder<KeyT, ValueT> onlyValuesMatching(BiFunction<ValueT, ValueT, Boolean> isNearEnough) {
      this.valueCheck = isNearEnough;
      return this;
    }

    @Override
    public DatabaseGetter<KeyT, ValueT> direction(Iterate direction) {
      final DatabaseFunction initializer;
      final DatabaseFunction iterator;
      final Supplier<OperationStatus> check;
      final DatabaseFunction countSkipper;
      final DatabaseFunction initialSkip;
      final DatabaseEntry keyEntry = new DatabaseEntry();
      final DatabaseEntry valueEntry = new DatabaseEntry();
      keyBinder.objectToEntry(key, keyEntry);

      if (startValue == null) {
        if (keyCheck == null) {
          initializer = dbCursor -> dbCursor.getSearchKey(keyEntry, valueEntry, LockMode.DEFAULT);
          check = () -> OperationStatus.SUCCESS;
        } else {
          initializer = dbCursor -> dbCursor.getSearchKeyRange(keyEntry, valueEntry, LockMode.DEFAULT);
          check = () -> {
            if (keyCheck.apply(key, keyBinder.entryToObject(keyEntry))) {
              return OperationStatus.SUCCESS;
            } else {
              return OperationStatus.NOTFOUND;
            }
          };
        }
      } else {
        if (keyCheck != null) {
          throw new UnsupportedOperationException("You can't skip to a partial key and then to value within the " +
            "keys that match that partial key.That would require iterating over all keys and testing them until " +
            "one is reached that doesn't match");
        }
        valueBinder.objectToEntry(startValue, valueEntry);

        if (partialValue) {
          initializer = dbCursor -> dbCursor.getSearchBothRange(keyEntry, valueEntry, LockMode.DEFAULT);
          check = () -> {
            if (valueCheck.apply(startValue, valueBinder.entryToObject(valueEntry))) {
              return OperationStatus.SUCCESS;
            } else {
              return OperationStatus.NOTFOUND;
            }
          };
        } else {
          initializer = dbCursor -> dbCursor.getSearchBoth(keyEntry, valueEntry, LockMode.DEFAULT);
          check = () -> OperationStatus.SUCCESS;
        }
      }

      if (keyCheck == null) {
        if (valueCheck == null) {
          if (direction == Iterate.FORWARDS) {
            iterator = dbCursor -> dbCursor.getNextDup(keyEntry, valueEntry, LockMode.DEFAULT);
          } else {
            iterator = dbCursor -> dbCursor.getPrevDup(keyEntry, valueEntry, LockMode.DEFAULT);
          }
        } else {
          if (direction == Iterate.FORWARDS) {
            iterator = dbCursor -> {
              final OperationStatus result = dbCursor.getNextDup(keyEntry, valueEntry, LockMode.DEFAULT);
              return result == OperationStatus.SUCCESS ? check.get() : result;
            };
          } else {
            iterator = dbCursor -> {
              final OperationStatus result = dbCursor.getPrevDup(keyEntry, valueEntry, LockMode.DEFAULT);
              return result == OperationStatus.SUCCESS ? check.get() : result;
            };
          }
        }
      } else {
        //valueCheck must be null, otherwise we'd have thrown an exception earlier
        if (direction == Iterate.FORWARDS) {
          iterator = dbCursor -> {
            final OperationStatus result = dbCursor.getNext(keyEntry, valueEntry, LockMode.DEFAULT);
            return result == OperationStatus.SUCCESS ? check.get() : result;
          };
        } else {
          iterator = dbCursor -> {
            final OperationStatus result = dbCursor.getPrev(keyEntry, valueEntry, LockMode.DEFAULT);
            return result == OperationStatus.SUCCESS ? check.get() : result;
          };
        }
      }


      if (skipCount > 0) {
        countSkipper = c -> {
          int cur = 0;
          OperationStatus status = OperationStatus.SUCCESS;
          while (cur < skipCount && status == OperationStatus.SUCCESS) {
            cur++;
            status = iterator.apply(c);
          }
          return status;
        };
      } else {
        countSkipper = c -> OperationStatus.SUCCESS;
      }

      if (startAtEnd) {
        initialSkip = dbCursor -> {
          final OperationStatus result;
          if (dbCursor.getNextNoDup(keyEntry, valueEntry, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            result = dbCursor.getPrev(keyEntry, valueEntry, LockMode.DEFAULT);
          } else {
            //go to end
            result = dbCursor.getLast(keyEntry, valueEntry, LockMode.DEFAULT);
          }
          if (result == OperationStatus.SUCCESS) {
            return countSkipper.apply(dbCursor);
          } else {
            return result;
          }
        };
      } else {
        initialSkip = countSkipper;
      }

      return new DatabaseGetter<>(
        keyBinder,
        valueBinder,
        c -> {
          OperationStatus result = initializer.apply(c);
          if (result == OperationStatus.SUCCESS) {
            result = check.get();
            if (result == OperationStatus.SUCCESS) {
              result = initialSkip.apply(c);
            }
          }
          return result;
        },
        iterator,
        database,
        cursors,
        keyEntry,
        valueEntry
      );
    }

  }

  private class CursorIterator<T> implements Iterator<T> {
    private final DatabaseFunction initialLookup;
    private final DatabaseFunction iteration;
    private final Supplier<T> valueMaker;
    private final String stackTrace;
    public Cursor cursor;
    boolean shouldMove;
    OperationStatus status;

    public CursorIterator(DatabaseFunction initialLookup, DatabaseFunction iteration,
                          Supplier<T> valueMaker, String stackTrace) {
      this.initialLookup = initialLookup;
      this.iteration = iteration;
      this.valueMaker = valueMaker;
      this.stackTrace = stackTrace;
      cursor = null;
      shouldMove = true;
      status = null;
    }

    @Override
    public boolean hasNext() {
      if (shouldMove) {
        try {
          if (cursor == null) {
            cursor = database.openCursor(null, null);
            cursors.put(cursor, stackTrace);
            status = initialLookup.apply(cursor);
          } else {
            status = iteration.apply(cursor);
          }
        } catch (DatabaseException | IllegalStateException e) {
          LOG.error("Database exception!", e);
          status = OperationStatus.NOTFOUND;
        }
        shouldMove = false;
      }
      return status == OperationStatus.SUCCESS;
    }

    @Override
    public T next() {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      shouldMove = true;
      return valueMaker.get();
    }
  }

}
