package nl.knaw.huygens.timbuctoo.v5.dataset;

import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;

public interface OptimizedPatchListener {

  void start() throws RdfProcessingFailedException;

  void onChangedSubject(String subject, ChangeFetcher changeFetcher) throws RdfProcessingFailedException;

  void notifyUpdate();

  void finish() throws RdfProcessingFailedException;
}
