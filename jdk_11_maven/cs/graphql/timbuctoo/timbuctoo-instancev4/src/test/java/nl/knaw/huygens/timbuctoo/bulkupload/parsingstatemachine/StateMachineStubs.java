package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

import nl.knaw.huygens.timbuctoo.bulkupload.savers.Saver;

import static org.mockito.Mockito.mock;

public class StateMachineStubs {
  public static StateMachine dummy() {
    Saver saver = mock(Saver.class);

    return new StateMachine(saver);
  }
}
