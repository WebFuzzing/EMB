package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.v5.dataset.ChangeFetcher;
import nl.knaw.huygens.timbuctoo.v5.dataset.ImportStatus;
import nl.knaw.huygens.timbuctoo.v5.dataset.OptimizedPatchListener;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.ImportStatusLabel;
import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@Ignore
public class StoreUpdaterTest {

  @Test
  public void startAddsProgressForThePrimaryStores() throws Exception {
    ImportStatus importStatus = mock(ImportStatus.class);
    StoreUpdater instance = createInstance(importStatus, Lists.newArrayList());

    instance.start(0);

    verify(importStatus).addProgressItem(VersionStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    verify(importStatus).addProgressItem(BdbTruePatchStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    verify(importStatus).addProgressItem(BdbTripleStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    verify(importStatus).addProgressItem(BdbTypeNameStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
    verify(importStatus).addProgressItem(UpdatedPerPatchStore.class.getSimpleName(), ImportStatusLabel.IMPORTING);
  }

  @Test
  public void startAddsProgressForDerivedStores() throws Exception {
    ImportStatus importStatus = mock(ImportStatus.class);
    StoreUpdater instance = createInstance(importStatus, Lists.newArrayList(new DerivedStore1(), new DerivedStore2()));

    instance.start(0);

    verify(importStatus).addProgressItem(DerivedStore1.class.getSimpleName(), ImportStatusLabel.PENDING);
    verify(importStatus).addProgressItem(DerivedStore2.class.getSimpleName(), ImportStatusLabel.PENDING);
  }

  @Test
  public void onceEvery5SecondesTheStoreProgressItemsAreUpdated() throws Exception {
    ImportStatus importStatus = mock(ImportStatus.class);
    StoreUpdater instance = createInstance(importStatus, Lists.newArrayList());
    instance.start(0);

    instance.onQuad(true, "", "", "", "", "", "");
    Thread.sleep(6000); // wait 6 seconds to let the second update trigger the notify
    instance.onQuad(true, "", "", "", "", "", "");

    verify(importStatus).updateProgressItem(VersionStore.class.getSimpleName(), 2);
    verify(importStatus).updateProgressItem(BdbTruePatchStore.class.getSimpleName(), 2);
    verify(importStatus).updateProgressItem(BdbTripleStore.class.getSimpleName(), 2);
    verify(importStatus).updateProgressItem(BdbTypeNameStore.class.getSimpleName(), 2);
    verify(importStatus).updateProgressItem(UpdatedPerPatchStore.class.getSimpleName(), 2);
  }

  @Test
  public void commitSetsTheProgressItemsOfThePrimaryStoresToDone() throws Exception {
    ImportStatus importStatus = mock(ImportStatus.class);
    StoreUpdater instance = createInstance(importStatus, Lists.newArrayList());
    instance.start(0);

    instance.commit();

    verify(importStatus).finishProgressItem(VersionStore.class.getSimpleName());
    verify(importStatus).finishProgressItem(BdbTruePatchStore.class.getSimpleName());
    verify(importStatus).finishProgressItem(BdbTripleStore.class.getSimpleName());
    verify(importStatus).finishProgressItem(BdbTypeNameStore.class.getSimpleName());
    verify(importStatus).finishProgressItem(UpdatedPerPatchStore.class.getSimpleName());
  }

  @Test
  public void commitProcessesTheDataForTheDerivedStores() throws Exception {
    ImportStatus importStatus = mock(ImportStatus.class);
    StoreUpdater instance = createInstance(importStatus, Lists.newArrayList(new DerivedStore1(), new DerivedStore2()));

    instance.start(0);
    instance.commit();

    verify(importStatus).startProgressItem(DerivedStore1.class.getSimpleName());
    verify(importStatus).startProgressItem(DerivedStore2.class.getSimpleName());
    verify(importStatus).finishProgressItem(DerivedStore1.class.getSimpleName());
    verify(importStatus).finishProgressItem(DerivedStore2.class.getSimpleName());
  }

  private StoreUpdater createInstance(ImportStatus importStatus, List<OptimizedPatchListener> listeners) {
    VersionStore versionStore = mock(VersionStore.class);
    BdbTypeNameStore bdbTypeNameStore = mock(BdbTypeNameStore.class);
    BdbTripleStore bdbTripleStore = mock(BdbTripleStore.class);
    BdbTruePatchStore bdbTruePatchStore = mock(BdbTruePatchStore.class);
    UpdatedPerPatchStore updatedPerPatchStore = mock(UpdatedPerPatchStore.class);
    return new StoreUpdater(
      bdbTripleStore,
      bdbTypeNameStore,
      bdbTruePatchStore,
      updatedPerPatchStore,
      listeners,
      versionStore,
      importStatus
    );
  }

  private static class DerivedStore1 implements OptimizedPatchListener {

    @Override
    public void start() throws RdfProcessingFailedException {
    }

    @Override
    public void onChangedSubject(String subject, ChangeFetcher changeFetcher) throws RdfProcessingFailedException {
    }

    @Override
    public void notifyUpdate() {
    }

    @Override
    public void finish() throws RdfProcessingFailedException {
    }
  }

  private static class DerivedStore2 implements OptimizedPatchListener {

    @Override
    public void start() throws RdfProcessingFailedException {
    }

    @Override
    public void onChangedSubject(String subject, ChangeFetcher changeFetcher) throws RdfProcessingFailedException {
    }

    @Override
    public void notifyUpdate() {
    }

    @Override
    public void finish() throws RdfProcessingFailedException {
    }
  }

}
