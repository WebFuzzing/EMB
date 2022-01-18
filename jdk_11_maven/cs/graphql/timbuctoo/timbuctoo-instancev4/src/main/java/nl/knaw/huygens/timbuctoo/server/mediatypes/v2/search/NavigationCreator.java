package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import nl.knaw.huygens.timbuctoo.util.UriHelper;

import javax.ws.rs.core.UriBuilder;
import java.util.UUID;

class NavigationCreator {
  private final UriHelper uriHelper;

  public NavigationCreator(UriHelper uriHelper) {
    this.uriHelper = uriHelper;
  }

  public void next(SearchResponseV2_1 searchResponse, int rows, int currentStart, int numFound, UUID id) {
    int nextStart = currentStart + rows;
    if (numFound > nextStart) {
      searchResponse
        .setNext(createUri(id, nextStart, rows));
    }
  }

  public void prev(SearchResponseV2_1 searchResponse, int rows, int currentStart, int numFound, UUID id) {
    if (currentStart > 0) {
      int prevStart = Math.max(0, currentStart - rows);
      searchResponse.setPrev(createUri(id, prevStart, rows));
    }
  }

  private String createUri(UUID id, int start, int rows) {
    return uriHelper.fromResourceUri(
      UriBuilder.fromUri("/v2.1/search/{id}")
        .queryParam("start", start)
        .queryParam("rows", rows)
        .build(id)
    ).toString();
  }
}
