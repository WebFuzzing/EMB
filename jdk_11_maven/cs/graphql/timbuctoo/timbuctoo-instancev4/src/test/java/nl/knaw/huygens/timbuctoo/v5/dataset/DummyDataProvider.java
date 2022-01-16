package nl.knaw.huygens.timbuctoo.v5.dataset;

import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingFailedException;

public class DummyDataProvider implements DataProvider {

  private RdfProcessor processor;

  @Override
  public void subscribeToRdf(RdfProcessor processor) {
    this.processor = processor;
  }

  public void onQuad(String subject, String predicate, String object,
                     String dataType, String language, String graph) throws RdfProcessingFailedException {
    onQuad(true, subject, predicate, object, dataType, language, graph);
  }

  public void onQuad(boolean isAssertion, String subject, String predicate, String object,
                     String dataType, String language, String graph) throws RdfProcessingFailedException {
    processor.onQuad(isAssertion, subject, predicate, object, dataType, language, graph);
  }

  public void start() throws RdfProcessingFailedException {
    processor.start(0);
  }

  public void finish() throws RdfProcessingFailedException {
    processor.commit();
  }
}
