package nl.knaw.huygens.timbuctoo.util;

public class Triple<S,T,U> {
  public final S left;
  private final T middle;
  private final U right;

  public Triple(S left, T middle, U right) {
    this.left = left;
    this.middle = middle;
    this.right = right;
  }

  public S getLeft() {
    return left;
  }

  public T getMiddle() {
    return middle;
  }

  public U getRight() {
    return right;
  }
}
