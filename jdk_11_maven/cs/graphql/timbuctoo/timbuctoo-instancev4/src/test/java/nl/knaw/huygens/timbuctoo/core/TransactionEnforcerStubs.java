package nl.knaw.huygens.timbuctoo.core;

import nl.knaw.huygens.timbuctoo.database.tinkerpop.TinkerPopOperationsStubs;
import nl.knaw.huygens.timbuctoo.server.TinkerPopGraphManager;

import static org.mockito.Mockito.mock;

public class TransactionEnforcerStubs {

  public static TransactionEnforcer forGraphWrapper(TinkerPopGraphManager graphManager) {
    return new TransactionEnforcer(
      (afterSuccessTaskExecutor) -> TimbuctooActionsStubs
        .withDataStoreAndAfterSucces(TinkerPopOperationsStubs.forGraphWrapper(graphManager), afterSuccessTaskExecutor)
    );
  }

  public static TransactionEnforcer forDataStoreOperations(DataStoreOperations db) {
    return new TransactionEnforcer(
      (afterSuccessTaskExecutor) -> TimbuctooActionsStubs
        .withDataStoreAndAfterSucces(db, afterSuccessTaskExecutor)
    );
  }

  static TransactionEnforcer withAfterSuccessExecutorAndTimbuctooActions(
    AfterSuccessTaskExecutor afterSuccessTaskExecutor, TimbuctooActions actions) {
    TimbuctooActions.TimbuctooActionsFactory timbuctooActionsFactory = x -> actions;
    return new TransactionEnforcer(timbuctooActionsFactory, afterSuccessTaskExecutor);
  }

  static TransactionEnforcer withAfterSuccessExecutor(AfterSuccessTaskExecutor afterSuccessTaskExecutor) {
    TimbuctooActions actions = mock(TimbuctooActions.class);
    TimbuctooActions.TimbuctooActionsFactory timbuctooActionsFactory = x -> actions;
    return new TransactionEnforcer(timbuctooActionsFactory, afterSuccessTaskExecutor);
  }
}
