package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class VersionStore {

  private static final Logger LOG = LoggerFactory.getLogger(VersionStore.class);
  private final BdbWrapper<String, Integer> bdbWrapper;

  public VersionStore(BdbWrapper<String, Integer> bdbWrapper)
    throws DataStoreCreationException {

    this.bdbWrapper = bdbWrapper;
  }

  public int getVersion() {
    try (Stream<Integer> values = bdbWrapper.databaseGetter()
      .getAll()
      .getValues(bdbWrapper.valueRetriever())) {
      return values.findAny().orElse(-1);
    }
  }

  public void setVersion(int version) throws DatabaseWriteException {
    bdbWrapper.put("version", version);
  }

  public void close() {
    try {
      bdbWrapper.close();
    } catch (Exception e) {
      LOG.error("Exception closing VersionStore", e);
    }
  }

  public void commit() {
    bdbWrapper.commit();
  }

  public void start() {
    bdbWrapper.beginTransaction();
  }

  public boolean isClean() {
    return bdbWrapper.isClean();
  }

  public void empty() {
    bdbWrapper.empty();
  }
}
