package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Stopwatch;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.dataset.ChangeFetcher;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.OptimizedPatchListener;
import nl.knaw.huygens.timbuctoo.v5.dataset.RdfProcessor;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction.OUT;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.LANGSTRING;
import static org.slf4j.LoggerFactory.getLogger;

public class StoreUpdater implements RdfProcessor {

  private static final Logger LOG = getLogger(StoreUpdater.class);
  private final BdbTripleStore tripleStore;
  private final BdbTypeNameStore typeNameStore;
  private final BdbTruePatchStore truePatchStore;
  private final UpdatedPerPatchStore updatedPerPatchStore;
  private final VersionStore versionStore;
  private int currentversion = -1;
  private Stopwatch stopwatch;
  private long count;
  private long prevCount;
  private List<OptimizedPatchListener> listeners;
  private long prevTime;
  private String logString;
  private ImportStatus importStatus;

  public StoreUpdater(BdbTripleStore tripleStore, BdbTypeNameStore typeNameStore, BdbTruePatchStore truePatchStore,
                      UpdatedPerPatchStore updatedPerPatchStore, List<OptimizedPatchListener> listeners,
                      VersionStore versionStore, ImportStatus importStatus) {
    this.tripleStore = tripleStore;
    this.typeNameStore = typeNameStore;
    this.truePatchStore = truePatchStore;
    this.updatedPerPatchStore = updatedPerPatchStore;
    this.versionStore = versionStore;
    this.listeners = listeners;
    this.importStatus = importStatus;
  }

  private void updateListeners() throws RdfProcessingFailedException {
    // start updating the derivative stores
    logString = "Processed {} subjects ({} subjects/s)";
    for (OptimizedPatchListener listener : listeners) {
      listener.start();
      importStatus.startProgressItem(listener.getClass().getSimpleName());
    }

    count = 0;
    prevCount = 0;
    prevTime = stopwatch.elapsed(TimeUnit.SECONDS);
    try (Stream<String> subjects = updatedPerPatchStore.ofVersion(currentversion)) {
      final ChangeFetcher getQuads = new ChangeFetcherImpl(truePatchStore, tripleStore, currentversion);
      final Iterator<String> iterator = subjects.iterator();
      while (iterator.hasNext()) {
        final boolean needUpdate = notifyUpdate();

        final String subject = iterator.next();
        for (OptimizedPatchListener listener : listeners) {
          if (needUpdate) {
            listener.notifyUpdate();
            importStatus.updateProgressItem(listener.getClass().getSimpleName(), count);
          }
          listener.onChangedSubject(subject, getQuads);
        }
      }
    }
    for (OptimizedPatchListener listener : listeners) {
      listener.finish();
      importStatus.finishProgressItem(listener.getClass().getSimpleName());
    }
  }

  @Override
  public void setPrefix(String prefix, String iri) throws RdfProcessingFailedException {
    notifyUpdate();
    typeNameStore.addPrefix(prefix, iri);
  }

  private void putQuad(String subject, String predicate, Direction direction, String object, String valueType,
                       String language) throws RdfProcessingFailedException {
    try {
      final boolean wasChanged = tripleStore.putQuad(subject, predicate, direction, object, valueType, language);
      if (wasChanged) {
        truePatchStore.put(subject, currentversion, predicate, direction, true, object, valueType, language);
        updatedPerPatchStore.put(currentversion, subject);
      }
    } catch (DatabaseWriteException e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  private void deleteQuad(String subject, String predicate, Direction direction, String object, String valueType,
                          String language) throws RdfProcessingFailedException {
    try {
      final boolean wasChanged = tripleStore.deleteQuad(subject, predicate, direction, object, valueType, language);
      if (wasChanged) {
        truePatchStore.put(subject, currentversion, predicate, direction, false, object, valueType, language);
        updatedPerPatchStore.put(currentversion, subject);
      }
    } catch (DatabaseWriteException e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  @Override
  public void addRelation(String subject, String predicate, String object, String graph)
    throws RdfProcessingFailedException {
    notifyUpdate();
    putQuad(subject, predicate, OUT, object, null, null);
    putQuad(object, predicate, Direction.IN, subject, null, null);
  }

  @Override
  public void addValue(String subject, String predicate, String value, String dataType, String graph)
    throws RdfProcessingFailedException {
    notifyUpdate();
    putQuad(subject, predicate, OUT, value, dataType, null);
  }

  @Override
  public void addLanguageTaggedString(String subject, String predicate, String value, String language,
                                      String graph) throws RdfProcessingFailedException {
    notifyUpdate();
    putQuad(subject, predicate, OUT, value, LANGSTRING, language);
  }

  @Override
  public void delRelation(String subject, String predicate, String object, String graph)
    throws RdfProcessingFailedException {
    notifyUpdate();
    deleteQuad(subject, predicate, OUT, object, null, null);
    deleteQuad(object, predicate, Direction.IN, subject, null, null);
  }

  @Override
  public void delValue(String subject, String predicate, String value, String dataType, String graph)
    throws RdfProcessingFailedException {
    notifyUpdate();
    deleteQuad(subject, predicate, OUT, value, dataType, null);
  }

  @Override
  public void delLanguageTaggedString(String subject, String predicate, String value, String language,
                                      String graph) throws RdfProcessingFailedException {
    notifyUpdate();
    deleteQuad(subject, predicate, OUT, value, LANGSTRING, language);
  }


  @Override
  public void start(int index) throws RdfProcessingFailedException {
    stopwatch = Stopwatch.createStarted();
    currentversion = index;
    startTransactions();
    logString = "Processed {} triples ({} triples/s)";
    count = 0; // reset the count to make sure the right amount of imported triples are logged.
  }

  private void startTransactions() {
    versionStore.start();
    importStatus.addProgressItem(VersionStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    tripleStore.start();
    importStatus.addProgressItem(BdbTripleStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    truePatchStore.start();
    importStatus.addProgressItem(BdbTruePatchStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    typeNameStore.start();
    importStatus.addProgressItem(BdbTypeNameStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    updatedPerPatchStore.start();
    importStatus.addProgressItem(UpdatedPerPatchStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);

    listeners.forEach(listener -> importStatus.addProgressItem(
      listener.getClass().getSimpleName(), ImportStatusLabel.PENDING)
    );
  }

  private boolean notifyUpdate() {
    count++;
    final long curTime = stopwatch.elapsed(TimeUnit.SECONDS);
    if (curTime - prevTime > 5) {
      final long itemsPerSecond = (count - prevCount) / (curTime - prevTime);
      LOG.info(logString, count, itemsPerSecond);
      importStatus.setStatus(String.format(logString.replaceAll("\\{\\}", "%d"),
        count, itemsPerSecond));
      prevCount = count;
      prevTime = curTime;
      updateStoreProgress();
      return true;
    }
    return false;
  }

  private void updateStoreProgress() {
    importStatus.updateProgressItem(VersionStore.class.getSimpleName(), count);
    importStatus.updateProgressItem(BdbTripleStore.class.getSimpleName(), count);
    importStatus.updateProgressItem(BdbTruePatchStore.class.getSimpleName(), count);
    importStatus.updateProgressItem(BdbTypeNameStore.class.getSimpleName(), count);
    importStatus.updateProgressItem(UpdatedPerPatchStore.class.getSimpleName(), count);
  }

  @Override
  public int getCurrentVersion() {
    return currentversion;
  }

  @Override
  public void commit() throws RdfProcessingFailedException {
    try {
      String msg = "processing " + count + " triples took " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds";
      LOG.info(msg);
      importStatus.setStatus(msg);
      importStatus.setStatus(msg);
      stopwatch.reset();
      stopwatch.start();
      versionStore.setVersion(currentversion);
      commitChanges();
      msg = "committing took " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds";
      LOG.info(msg);
      stopwatch.reset();
      stopwatch.start();
      updateListeners();
      msg = "post-processing took " + stopwatch.elapsed(TimeUnit.SECONDS) + " seconds";
      LOG.info(msg);
      importStatus.setStatus(msg);
    } catch (DatabaseWriteException | JsonProcessingException e) {
      throw new RdfProcessingFailedException(e);
    }
  }

  private void commitChanges() throws JsonProcessingException, DatabaseWriteException {
    versionStore.commit();
    importStatus.finishProgressItem(VersionStore.class.getSimpleName());
    typeNameStore.commit();
    importStatus.finishProgressItem(BdbTypeNameStore.class.getSimpleName());
    tripleStore.commit();
    importStatus.finishProgressItem(BdbTripleStore.class.getSimpleName());
    truePatchStore.commit();
    importStatus.finishProgressItem(BdbTruePatchStore.class.getSimpleName());
    updatedPerPatchStore.commit();
    importStatus.finishProgressItem(UpdatedPerPatchStore.class.getSimpleName());
  }

}
