package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.google.common.collect.Iterators;
import com.google.common.collect.PeekingIterator;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import org.slf4j.Logger;

import java.util.Iterator;
import java.util.stream.Stream;

import static org.slf4j.LoggerFactory.getLogger;

class RetractionMerger implements Iterator<CursorQuad> {

  private static final Logger LOG = getLogger(RetractionMerger.class);

  private final PeekingIterator<CursorQuad> state;
  private final PeekingIterator<CursorQuad> retractions;
  private final BdbTripleStore tripleStore;
  private final int version;

  public RetractionMerger(Stream<CursorQuad> state, Stream<CursorQuad> retractions, BdbTripleStore tripleStore,
                          int version) {
    this.state = Iterators.peekingIterator(state.iterator());
    this.retractions = Iterators.peekingIterator(retractions.iterator());
    this.tripleStore = tripleStore;
    this.version = version;
  }

  @Override
  public boolean hasNext() {
    final boolean stateHasNext = state.hasNext();
    final boolean retractionsHasNext = retractions.hasNext();
    return stateHasNext || retractionsHasNext;
  }

  /*
  There's currentState/additions and retractions:

  e.g.

  currentState:     retractions:
   S,P1,O           S,P3,O
  +S,P2,O           S,P5,O2
   S,P4,O           S,P8,O2
   S,P5,O
  +S,P6,O
   S,P7,O
  +S,P8,O


  This iterator will peek at the next item of currentState and at the next item of retractions.

  If the currentstate is sorted before the retraction it emits the currentstate, otherwise the retraction

  We als handle the case where a retraction is present in currentState (emit the retraction and move currentstate
  forward as well though that should never happen.
  */
  @Override
  public CursorQuad next() {
    if (state.hasNext() && retractions.hasNext()) {
      CursorQuad leftQ = state.peek();
      CursorQuad rightQ = retractions.peek();
      int compareResult = tripleStore.compare(leftQ, rightQ);
      if (compareResult == 0) {
        //Huh? we have a retraction, but the thing is also part of the state?
        LOG.error("in {} {} was retracted, but it is still part of the store as {}", version, rightQ, leftQ);
        //oh well...
        state.next();
        //back to business
        return retractions.next();
      } else if (compareResult < 0) {
        return state.next();
      } else {
        return retractions.next();
      }
    } else {
      if (state.hasNext()) {
        return state.next();
      } else {
        return retractions.next();
      }
    }
  }
}
