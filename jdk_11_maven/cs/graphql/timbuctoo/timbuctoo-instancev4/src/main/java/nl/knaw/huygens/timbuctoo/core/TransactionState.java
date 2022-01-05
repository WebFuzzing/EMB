package nl.knaw.huygens.timbuctoo.core;

public class TransactionState {
  private final boolean wasCommitted;

  private TransactionState(boolean wasCommitted) {
    this.wasCommitted = wasCommitted;
  }

  public static TransactionState commit() {
    return new TransactionState(true);
  }

  public static TransactionState rollback() {
    return new TransactionState(false);
  }

  public boolean wasCommitted() {
    return wasCommitted;
  }

}
