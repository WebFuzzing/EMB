package nl.knaw.huygens.timbuctoo.core;

import nl.knaw.huygens.timbuctoo.core.AfterSuccessTaskExecutor;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class AfterSuccessTaskExecutorTest {
  @Test
  public void executesTheAddedTasks() throws Exception {
    AfterSuccessTaskExecutor instance = new AfterSuccessTaskExecutor();
    AfterSuccessTaskExecutor.Task task1 = mock(AfterSuccessTaskExecutor.Task.class);
    AfterSuccessTaskExecutor.Task task2 = mock(AfterSuccessTaskExecutor.Task.class);
    instance.addTask(task1);
    instance.addTask(task2);

    instance.executeTasks();

    verify(task1).execute();
    verify(task2).execute();

  }
}
