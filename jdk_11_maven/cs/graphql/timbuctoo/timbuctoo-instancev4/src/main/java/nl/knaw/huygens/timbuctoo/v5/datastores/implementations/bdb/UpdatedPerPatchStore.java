package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import nl.knaw.huygens.timbuctoo.v5.berkeleydb.BdbWrapper;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.DataStoreCreationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class UpdatedPerPatchStore {

  private static final Logger LOG = LoggerFactory.getLogger(UpdatedPerPatchStore.class);
  private final BdbWrapper<Integer, String> bdbWrapper;

  public UpdatedPerPatchStore(BdbWrapper<Integer, String> bdbWrapper)
    throws DataStoreCreationException {

    this.bdbWrapper = bdbWrapper;
  }

  public void put(int currentversion, String subject) throws DatabaseWriteException {
    bdbWrapper.put(currentversion, subject);
  }

  public void delete(int currentversion, String subject) throws DatabaseWriteException {
    bdbWrapper.delete(currentversion, subject);
  }

  public Stream<String> ofVersion(int version) {
    return bdbWrapper.databaseGetter().key(version).dontSkip().forwards().getValues(bdbWrapper.valueRetriever());
  }

  public Stream<Integer> getVersions() {
    return bdbWrapper.databaseGetter().getAll().getKeys(bdbWrapper.keyRetriever()).distinct();
  }

  public void close() {
    try {
      bdbWrapper.close();
    } catch (Exception e) {
      LOG.error("Exception closing UpdatedPerPatchStore", e);
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
