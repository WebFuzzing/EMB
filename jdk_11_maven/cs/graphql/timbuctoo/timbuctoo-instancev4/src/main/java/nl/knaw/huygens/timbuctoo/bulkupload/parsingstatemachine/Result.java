package nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine;

import java.util.function.Consumer;

public class Result {
  private String failure;
  private boolean ignored;

  private Result(String failure, boolean ignored) {
    this.failure = failure;
    this.ignored = ignored;
  }

  public static Result failure(String message) {
    return new Result(message, false);
  }

  public static Result success() {
    return new Result(null, false);
  }

  public static Result ignored() {
    return new Result(null, true);
  }


  public void handle(Runnable onSuccess, Consumer<String> onFailure, Runnable onIgnored) {
    if (ignored) {
      onIgnored.run();
    } else if (failure != null) {
      onFailure.accept(failure);
    } else {
      onSuccess.run();
    }
  }

  public void handle(Consumer<String> onFailure) {
    if (!ignored && failure != null) {
      onFailure.accept(failure);
    }
  }


  public Result and(Result extraResult) {
    if (failure != null) {
      if (extraResult.failure != null) {
        failure = failure + extraResult.failure;
      }
      //else do nothing; if extraResult was success or ignored doesn't matter it's a failure now
    } else {
      if (extraResult.failure != null) {
        failure = extraResult.failure;
      } else {
        ignored = ignored && extraResult.ignored; //if either is success it's no longer ignored
      }
    }
    return this;
  }

  @Override
  public String toString() {
    if (ignored) {
      return "IGNORED";
    } else if (failure == null) {
      return "SUCCESS";
    } else {
      return failure;
    }
  }
}
