package nl.knaw.huygens.timbuctoo.server;

import nl.knaw.huygens.timbuctoo.util.Timeout;

public interface SearchConfig {
  Timeout getSearchResultAvailabilityTimeout();
}
