package nl.knaw.huygens.timbuctoo.solr;

import java.io.IOException;

public class NoOpWebhooks implements Webhooks {
  @Override
  public void startIndexingForVre(String vreName) throws IOException {
    //ignore
  }

  @Override
  public void dataSetUpdated(String dataSetId) throws IOException {
    //ignore
  }
}
