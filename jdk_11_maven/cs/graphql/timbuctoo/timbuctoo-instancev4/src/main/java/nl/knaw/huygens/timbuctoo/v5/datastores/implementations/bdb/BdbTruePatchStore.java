package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.BdbDbCreationException;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.ChangeType;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.IN;
import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.OUT;

public class BdbTruePatchStore {

  private static final Logger LOG = LoggerFactory.getLogger(BdbTruePatchStore.class);
  private final HashMap<Integer, BdbWrapper<String, String>> bdbWrappers;
  private final DatabaseCreator databaseCreator;

  public BdbTruePatchStore(DatabaseCreator databaseCreator,
                           UpdatedPerPatchStore updatedPerPatchStore) throws DataStoreCreationException {
    this.databaseCreator = databaseCreator;
    bdbWrappers = new HashMap<>();
    try (final Stream<Integer> versions = updatedPerPatchStore.getVersions()) {
      for (Integer version : versions.collect(Collectors.toList())) {
        bdbWrappers.put(version, this.databaseCreator.createDatabase(String.valueOf(version)));
      }
    } catch (BdbDbCreationException e) {
      throw new DataStoreCreationException(e);
    }
  }

  public BdbWrapper<String, String> getOrCreateBdbWrapper(int version) throws BdbDbCreationException {
    if (bdbWrappers.containsKey(version)) {
      return bdbWrappers.get(version);
    }

    BdbWrapper<String, String> bdbWrapper = databaseCreator.createDatabase(String.valueOf(version));
    bdbWrappers.put(version, bdbWrapper);
    return bdbWrapper;
  }

  public void put(String subject, int currentversion, String predicate, Direction direction, boolean isAssertion,
                  String object, String valueType, String language) throws DatabaseWriteException {
    //if we assert something and then retract it in the same patch, it's as if it never happened at all
    //so we delete the inversion
    final String dirStr = direction == OUT ? "1" : "0";
    try {
      getOrCreateBdbWrapper(currentversion).delete(
        subject + "\n" + currentversion + "\n" + (!isAssertion ? 1 : 0),
        predicate + "\n" +
          dirStr + "\n" +
          (valueType == null ? "" : valueType) + "\n" +
          (language == null ? "" : language) + "\n" +
          object
      );

      getOrCreateBdbWrapper(currentversion).put(
          subject + "\n" + currentversion + "\n" + (isAssertion ? 1 : 0),
          predicate + "\n" +
              dirStr + "\n" +
              (valueType == null ? "" : valueType) + "\n" +
              (language == null ? "" : language) + "\n" +
              object
      );
    } catch (BdbDbCreationException e) {
      throw new DatabaseWriteException(e);
    }
  }

  public Stream<CursorQuad> getChangesOfVersion(int version, boolean assertions) {
    // FIXME partialKey does not work well with endsWidth, it stops the iterator with the first match
    // See issue T141 on https://github.com/knaw-huc/backlogs/blob/master/structured-data.txt
    if (bdbWrappers.containsKey(version)) {
      final BdbWrapper<String, String> bdbWrapper = bdbWrappers.get(version);
      return bdbWrapper.databaseGetter()
                       .getAll()
                       // .partialKey("\n" + version + "\n" + (assertions ? "1" : "0"), (pf,
                       // key) -> key.endsWith(pf))
                       // .dontSkip()
                       // .forwards()
                       .getKeysAndValues(bdbWrapper.keyValueConverter(Tuple::tuple))
                       .filter(kv -> kv.getLeft().endsWith(version + "\n" + (assertions ? "1" : "0")))
                       .map((value) -> makeCursorQuad(value.getLeft().split("\n")[0], assertions,value.getRight()));
    }
    return Stream.empty();
  }

  public Stream<CursorQuad> getChanges(String subject, int version, boolean assertions) {
    if (bdbWrappers.containsKey(version)) {
      final BdbWrapper<String, String> bdbWrapper = bdbWrappers.get(version);
      return bdbWrapper.databaseGetter()
                       .key(subject + "\n" + version + "\n" + (assertions ? "1" : "0"))
                       .dontSkip()
                       .forwards()
                       .getValues(bdbWrapper.valueRetriever())
                       .map(v -> makeCursorQuad(subject, assertions, v));
    }
    return Stream.empty();
  }

  public Stream<CursorQuad> getChanges(String subject, String predicate, Direction direction, int version,
                                       boolean assertions) {
    if (bdbWrappers.containsKey(version)) {
      final BdbWrapper<String, String> bdbWrapper = bdbWrappers.get(version);
      return bdbWrapper.databaseGetter()
                       .key(subject + "\n" + version + "\n" + (assertions ? "1" : "0"))
                       .skipNearValue(predicate + "\n" + (direction == OUT ? "1" : "0") + "\n")
                       .onlyValuesMatching((prefix, value) -> value.startsWith(prefix))
                       .forwards()
                       .getValues(bdbWrapper.valueRetriever())
                       .map(v -> makeCursorQuad(subject, assertions, v));
    }

    return Stream.empty();
  }

  public CursorQuad makeCursorQuad(String subject, boolean assertions, String value) {
    String[] parts = value.split("\n", 5);
    Direction direction = parts[1].charAt(0) == '1' ? OUT : IN;
    ChangeType changeType = assertions ? ChangeType.ASSERTED :  ChangeType.RETRACTED;
    return CursorQuad.create(
      subject,
      parts[0],
      direction,
      changeType,
      parts[4],
      parts[2].isEmpty() ? null : parts[2],
      parts[3].isEmpty() ? null : parts[3],
      ""
    );
  }

  public void close() {
    try {
      for (BdbWrapper<String, String> bdbWrapper : bdbWrappers.values()) {
        bdbWrapper.close();
      }
    } catch (Exception e) {
      LOG.error("Exception closing BdbTruePatchStore", e);
    }
  }

  public void commit() {
    bdbWrappers.values().forEach(BdbWrapper::commit);
  }

  public void start() {
    bdbWrappers.values().forEach(BdbWrapper::beginTransaction);
  }

  public boolean isClean() {
    return bdbWrappers.values().stream().allMatch(BdbWrapper::isClean);
  }

  public void empty() {
    bdbWrappers.values().forEach(BdbWrapper::empty);
  }

  public void migrate() {
    try {
      final BdbWrapper<String, String> database = databaseCreator.createDatabase("");
      database.databaseGetter().getAll().getKeysAndValues(database.keyValueConverter(Tuple::tuple)).forEach(tuple -> {
        try {
          final String[] split = tuple.getLeft().split("\n");
          final String version = split[split.length - 2];

          bdbWrappers.get(Integer.parseInt(version)).put(tuple.getLeft(), tuple.getRight());
        } catch (DatabaseWriteException e) {
          e.printStackTrace();
        }
      });

      bdbWrappers.values().forEach(BdbWrapper::commit);

      database.empty();
      database.close();
    } catch (BdbDbCreationException e) {
      throw new RuntimeException(e);
    }
  }

  public interface DatabaseCreator {
    BdbWrapper<String, String> createDatabase(String version) throws BdbDbCreationException;
  }
}
