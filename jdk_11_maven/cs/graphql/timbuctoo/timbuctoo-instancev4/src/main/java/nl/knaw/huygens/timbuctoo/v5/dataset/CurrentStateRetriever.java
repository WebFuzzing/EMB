package nl.knaw.huygens.timbuctoo.v5.dataset;

import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.BdbTripleStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;

import java.util.stream.Stream;

public class  CurrentStateRetriever {

  private final BdbTripleStore bdbTripleStore;

  public CurrentStateRetriever(BdbTripleStore bdbTripleStore) {
    this.bdbTripleStore = bdbTripleStore;
  }

  /**
   *
   * @return all the outgoing quads stored in the data set
   */
  public Stream<CursorQuad> retrieveData() {
    return bdbTripleStore.getAllQuads().filter(quad -> quad.getDirection() == Direction.OUT);
  }
}
