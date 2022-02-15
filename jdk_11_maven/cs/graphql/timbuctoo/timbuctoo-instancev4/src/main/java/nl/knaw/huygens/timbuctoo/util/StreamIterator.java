package nl.knaw.huygens.timbuctoo.util;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StreamIterator {
  public static <S> Stream<S> stream(Iterator<S> input) {
    Iterable<S> it = () -> input;
    return StreamSupport.stream(it.spliterator(), false);
  }

  public static <T, U extends Exception> void iterateAndCloseOrThrow(Stream<T> stream, ThrowingConsumer<T, U> consumer)
    throws U {
    try {
      Iterator<T> iterator = stream.iterator();
      while (iterator.hasNext()) {
        consumer.accept(iterator.next());
      }
    } finally {
      stream.close();
    }
  }

  public static <T> void iterateAndClose(Stream<T> stream, Consumer<T> consumer) {
    try {
      Iterator<T> iterator = stream.iterator();
      while (iterator.hasNext()) {
        consumer.accept(iterator.next());
      }
    } finally {
      stream.close();
    }
  }


  public interface ThrowingConsumer<T, U extends Exception> {
    void accept(T value) throws U;
  }
}
