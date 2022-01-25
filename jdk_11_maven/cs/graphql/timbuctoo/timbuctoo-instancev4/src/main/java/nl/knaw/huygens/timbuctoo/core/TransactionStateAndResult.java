package nl.knaw.huygens.timbuctoo.core;

public class TransactionStateAndResult<T> {
  private final T value;
  private final boolean wasCommitted;

  private TransactionStateAndResult(T value, boolean wasCommitted) {
    this.value = value;
    this.wasCommitted = wasCommitted;
  }

  public static <T> TransactionStateAndResult<T> commitAndReturn(T value) {
    return new TransactionStateAndResult<T>(value, true);
  }

  public static <T> TransactionStateAndResult<T> rollbackAndReturn(T value) {
    return new TransactionStateAndResult<T>(value, false);
  }

  public boolean wasCommitted() {
    return wasCommitted;
  }

  public T getValue() {
    return value;
  }
}
