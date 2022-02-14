package nl.knaw.huygens.timbuctoo.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class AfterSuccessTaskExecutor {
  public static final Logger LOG = LoggerFactory.getLogger(AfterSuccessTaskExecutor.class);
  private final LinkedList<Task> tasks;

  public AfterSuccessTaskExecutor() {
    tasks = new LinkedList<>();
  }

  public void addTask(Task task) {
    tasks.push(task);
  }

  public void executeTasks() {
    while (!tasks.isEmpty()) {
      Task task = tasks.pop();
      try {
        task.execute();
      } catch (Exception e) {
        LOG.error("Could not execute task '{}'", task.getDescription());
      }
    }
  }

  public interface Task {

    void execute() throws Exception;

    String getDescription();
  }
}
