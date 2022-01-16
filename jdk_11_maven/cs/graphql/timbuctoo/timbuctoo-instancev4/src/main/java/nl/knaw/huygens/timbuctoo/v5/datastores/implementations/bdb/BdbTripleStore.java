package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.DatabaseGetter;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.QuadStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.berkeleydb.DatabaseGetter.Iterate.BACKWARDS;
import static nl.knaw.huygens.timbuctoo.v5.berkeleydb.DatabaseGetter.Iterate.FORWARDS;

/**
 * This datastore determines the current state of the DataSet.
 */
public class BdbTripleStore implements QuadStore {

  private static final Logger LOG = LoggerFactory.getLogger(BdbTripleStore.class);
  private final BdbWrapper<String, String> bdbWrapper;

  public BdbTripleStore(BdbWrapper<String, String> rdfData)
    throws DataStoreCreationException {
    this.bdbWrapper = rdfData;
  }

  @Override
  public Stream<CursorQuad> getQuads(String subject, String predicate, Direction direction, String cursor) {
    final DatabaseGetter<String, String> getter;
    if (cursor.isEmpty()) {
      getter = bdbWrapper.databaseGetter()
        .key((formatKey(subject, predicate, direction)))
        .dontSkip()
        .forwards();
    } else {
      if (cursor.equals("LAST")) {
        getter = bdbWrapper.databaseGetter()
          .key((formatKey(subject, predicate, direction)))
          .skipToEnd()
          .backwards();
      } else {
        String[] fields = cursor.substring(2).split("\n\n", 2);
        getter = bdbWrapper.databaseGetter()
          .key(fields[0])
          .skipToValue(fields[1])
          .skipOne() //we start after the cursor
          .direction(cursor.startsWith("A\n") ? FORWARDS : BACKWARDS);
      }
    }
    return getter.getKeysAndValues(bdbWrapper.keyValueConverter(this::formatResult));
  }

  @Override
  public Stream<CursorQuad> getQuads(String subject) {
    return bdbWrapper.databaseGetter()
      .partialKey(subject + "\n", (prefix, key) -> key.startsWith(prefix))
      .dontSkip()
      .forwards()
      .getKeysAndValues(bdbWrapper.keyValueConverter(this::formatResult));
  }

  @Override
  public Stream<CursorQuad> getAllQuads() {
    return bdbWrapper.databaseGetter()
      .getAll()
      .getKeysAndValues(bdbWrapper.keyValueConverter(this::formatResult));
  }

  @Override
  public void close() {
    try {
      bdbWrapper.close();
    } catch (Exception e) {
      LOG.error("Exception closing BdbTripleStore", e);
    }
  }

  private CursorQuad formatResult(String key, String value) {
    String cursor = key + "\n\n" + value;
    String[] keyFields = key.split("\n", 3);
    String[] valueFields = value.split("\n", 3);
    return CursorQuad.create(
      keyFields[0],
      keyFields[1],
      Direction.valueOf(keyFields[2]),
      valueFields[2],
      valueFields[0].isEmpty() ? null : valueFields[0],
      valueFields[1].isEmpty() ? null : valueFields[1],
      cursor
    );
  }

  public boolean putQuad(String subject, String predicate, Direction direction, String object, String dataType,
                         String language) throws DatabaseWriteException {
    String value = formatValue(object, dataType, language);
    return bdbWrapper.put(formatKey(subject, predicate, direction), value);
  }

  public boolean deleteQuad(String subject, String predicate, Direction direction, String object, String dataType,
                         String language) throws DatabaseWriteException {
    String value = formatValue(object, dataType, language);
    return bdbWrapper.delete(formatKey(subject, predicate, direction), value);
  }

  public String formatKey(String subject, String predicate, Direction direction) {
    return subject + "\n" + predicate + "\n" + (direction == null ? "" : direction.name());
  }

  public String formatValue(String object, String dataType, String language) {
    return (dataType == null ? "" : dataType) + "\n" + (language == null ? "" : language) + "\n" + object;
  }

  public int compare(CursorQuad leftQ, CursorQuad rightQ) {
    final String leftStr = formatKey(leftQ.getSubject(), leftQ.getPredicate(), leftQ.getDirection()) + "\n" +
      formatValue(leftQ.getObject(), leftQ.getValuetype().orElse(null), leftQ.getLanguage().orElse(null));
    final String rightStr = formatKey(rightQ.getSubject(), rightQ.getPredicate(), rightQ.getDirection()) + "\n" +
      formatValue(rightQ.getObject(), rightQ.getValuetype().orElse(null), rightQ.getLanguage().orElse(null));
    return leftStr.compareTo(rightStr);
  }

  @Override
  public void commit() {
    bdbWrapper.commit();
  }

  @Override
  public boolean isClean() {
    return bdbWrapper.isClean();
  }

  public void start() {
    bdbWrapper.beginTransaction();
  }

  public void empty() {
    bdbWrapper.empty();
  }
}
