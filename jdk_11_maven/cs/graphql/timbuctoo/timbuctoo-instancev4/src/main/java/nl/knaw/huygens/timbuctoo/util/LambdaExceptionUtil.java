package nl.knaw.huygens.timbuctoo.util;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public final class LambdaExceptionUtil {

  @FunctionalInterface
  public interface Consumer_WithExceptions<T, E extends Exception> {
    void accept(T val) throws E;
  }

  @FunctionalInterface
  public interface BiConsumer_WithExceptions<T, S, E extends Exception> {
    void accept(T val, S val2) throws E;
  }


  @FunctionalInterface
  public interface Function_WithExceptions<T, R, E extends Exception> {
    R apply(T val) throws E;
  }

  @FunctionalInterface
  public interface BiFunction_WithExceptions<T, S, R, E extends Exception> {
    R apply(T val, S other) throws E;
  }


  public static <T, E extends Exception> Consumer<T> rethrowConsumer(Consumer_WithExceptions<T, E> consumer) throws E {
    return t -> {
      try {
        consumer.accept(t);
      } catch (Exception exception) {
        throwActualException(exception);
      }
    };
  }


  public static <T, S, E extends Exception> BiConsumer<T, S> rethrowBiConsumer(
    BiConsumer_WithExceptions<T, S, E> consumer) throws E {
    return (val1, val2) -> {
      try {
        consumer.accept(val1, val2);
      } catch (Exception exception) {
        throwActualException(exception);
      }
    };
  }

  public static <T, R, E extends Exception> Function<T, R> rethrowFunction(Function_WithExceptions<T, R, E> function)
    throws E  {
    return t -> {
      try {
        return function.apply(t);
      } catch (Exception exception) {
        throwActualException(exception);
        return null;
      }
    };
  }

  public static <T, S, R, E extends Exception> BiFunction<T, S, R> rethrowBiFunction(
    BiFunction_WithExceptions<T, S, R, E> function)
    throws E  {
    return (val, other) -> {
      try {
        return function.apply(val, other);
      } catch (Exception exception) {
        throwActualException(exception);
        return null;
      }
    };
  }

  @SuppressWarnings("unchecked")
  private static <E extends Exception> void throwActualException(Exception exception) throws E {
    throw (E) exception;
  }

}
