package nl.knaw.huygens.timbuctoo.core;

import java.util.function.Function;

public class TransactionEnforcer {

  private final TimbuctooActions.TimbuctooActionsFactory timbuctooActionsFactory;
  private final AfterSuccessTaskExecutor afterSuccessTaskExecutor;

  public TransactionEnforcer(TimbuctooActions.TimbuctooActionsFactory timbuctooActionsFactory) {
    this(timbuctooActionsFactory, new AfterSuccessTaskExecutor());
  }

  TransactionEnforcer(TimbuctooActions.TimbuctooActionsFactory timbuctooActionsFactory,
                      AfterSuccessTaskExecutor afterSuccessTaskExecutor) {

    this.timbuctooActionsFactory = timbuctooActionsFactory;
    this.afterSuccessTaskExecutor = afterSuccessTaskExecutor;
  }

  public <T> T executeAndReturn(Function<TimbuctooActions, TransactionStateAndResult<T>> action) {
    boolean success = false;
    TimbuctooActions timbuctooActions = timbuctooActionsFactory.create(afterSuccessTaskExecutor);
    try {
      TransactionStateAndResult<T> result = action.apply(timbuctooActions);
      if (result.wasCommitted()) {
        success = true;
        timbuctooActions.success();
      } else {
        success = false;
        timbuctooActions.rollback();
      }
      return result.getValue();
    } catch (RuntimeException e) {
      success = false;
      timbuctooActions.rollback();
      throw e;
    } finally {
      timbuctooActions.close();
      if (success) {
        afterSuccessTaskExecutor.executeTasks();
      }
    }
  }

  public void execute(Function<TimbuctooActions, TransactionState> action) {
    TimbuctooActions timbuctooActions = timbuctooActionsFactory.create(afterSuccessTaskExecutor);

    try {
      TransactionState result = action.apply(timbuctooActions);
      if (result.wasCommitted()) {
        timbuctooActions.success();
      } else {
        timbuctooActions.rollback();
      }
    } catch (RuntimeException e) {
      timbuctooActions.rollback();
      throw e;
    } finally {
      timbuctooActions.close();
    }
  }

}
