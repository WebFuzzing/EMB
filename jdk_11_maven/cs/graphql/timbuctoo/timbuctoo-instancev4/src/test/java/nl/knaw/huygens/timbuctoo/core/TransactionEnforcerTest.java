package nl.knaw.huygens.timbuctoo.core;

import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

public class TransactionEnforcerTest {

  @Test
  public void executeAndReturnExecutesTheAfterSuccessTaskExecutorAfterTheTransactionIsCommitted() {
    AfterSuccessTaskExecutor afterSuccessTaskExecutor = mock(AfterSuccessTaskExecutor.class);
    TimbuctooActions actions = mock(TimbuctooActions.class);
    TransactionEnforcer instance =
      TransactionEnforcerStubs.withAfterSuccessExecutorAndTimbuctooActions(afterSuccessTaskExecutor, actions);

    instance.executeAndReturn(timbuctooActions -> TransactionStateAndResult.commitAndReturn(""));

    InOrder inOrder = inOrder(actions, afterSuccessTaskExecutor);
    inOrder.verify(actions).success();
    inOrder.verify(actions).close();
    inOrder.verify(afterSuccessTaskExecutor).executeTasks();
  }

  @Test
  public void executeAndReturnDoesNotExecuteTheAfterSuccessTaskExecutorAfterTheTransactionIsRolledBack() {
    AfterSuccessTaskExecutor afterSuccessTaskExecutor = mock(AfterSuccessTaskExecutor.class);
    TimbuctooActions actions = mock(TimbuctooActions.class);
    TransactionEnforcer instance = TransactionEnforcerStubs
      .withAfterSuccessExecutorAndTimbuctooActions(afterSuccessTaskExecutor, actions);

    instance.executeAndReturn(timbuctooActions -> TransactionStateAndResult.rollbackAndReturn(""));

    verify(actions).rollback();
    verifyNoInteractions(afterSuccessTaskExecutor);
  }


  @Test(expected = RuntimeException.class)
  public void executeAndReturnDoesNotExecuteTheAfterSuccessTaskExecutorWhenAnExceptionIsThrown() {
    AfterSuccessTaskExecutor afterSuccessTaskExecutor = mock(AfterSuccessTaskExecutor.class);
    TransactionEnforcer instance = TransactionEnforcerStubs.withAfterSuccessExecutor(afterSuccessTaskExecutor);

    try {
      instance.executeAndReturn(timbuctooActions -> {
        throw new RuntimeException();
      });
    } finally {
      verifyNoInteractions(afterSuccessTaskExecutor);
    }


  }

}
