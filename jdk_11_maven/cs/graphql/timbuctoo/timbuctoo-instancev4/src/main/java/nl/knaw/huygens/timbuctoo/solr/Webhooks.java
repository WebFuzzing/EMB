package nl.knaw.huygens.timbuctoo.solr;

import java.io.IOException;

public interface Webhooks {

  void startIndexingForVre(String vreName) throws IOException;

  void dataSetUpdated(String dataSetId) throws IOException;
}
