package nl.knaw.huygens.timbuctoo.v5.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Spliterator;
import java.util.stream.Stream;

/**
 * An implementation of an InputStream that takes a Java 8 stream of byte arrays as its input
 * Providing a lazily evaluated stream means it's possible to supply the InputStream with output generated or
 * read from source lazily.
 * For example, you could stream a JDBC recordset record by record from a DB query,
 * transform them and write them to this stream one at a time without loading the whole recordset into memory.
 * WARNING: As you would expect, reading the input stream is a terminal operation for the underlying Java 8 stream,
 * so it can only be read once by any instance of a StreamInputStream.
 * Source: https://gist.github.com/stephenhand/292cdd8bba7a452d83c51c00d9ef113c
 */
public class ByteArrayStreamInputStream extends InputStream {
  private final Stream<byte[]> source;

  private Spliterator<byte[]> spliterator;
  private ByteArrayInputStream currentItemByteStream = null;

  public ByteArrayStreamInputStream(Stream<byte[]> source) {
    this.source = source;
  }

  @Override
  public int read() throws IOException {
    if (spliterator == null) {
      spliterator = source.spliterator();
    }
    try {
      if (currentItemByteStream == null) {
        if (!spliterator.tryAdvance(bytes -> currentItemByteStream = new ByteArrayInputStream(bytes))) {
          source.close();
          return -1;
        }
      }
      int ret = currentItemByteStream.read();
      if (ret == -1) {
        currentItemByteStream = null;
        return read();
      }
      return ret;
    } catch (Throwable t) {
      source.close();
      throw t;
    }
  }

  @Override
  public void close() throws IOException {
    try {
      source.close();
    } finally {
      super.close();
    }
  }
}
