package nl.knaw.huygens.timbuctoo.v5.filestorage.implementations.filesystem.dto;

import com.google.common.base.Charsets;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedLog;
import org.mozilla.universalchardet.UniversalDetector;

import javax.ws.rs.core.MediaType;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipException;

public class FileSystemCachedLog implements CachedLog {
  private final MediaType mimeType;
  private final Charset charset;
  private final String name;
  private final File file;
  private List<Closeable> toClose = new ArrayList<>();

  public FileSystemCachedLog(MediaType mimeType, Optional<Charset> charset, String name, File file) {
    this.mimeType = mimeType;
    if (!charset.isPresent()) {
      try {
        byte[] buf = new byte[4096];
        final UniversalDetector detector;
        try (FileInputStream stream = new FileInputStream(file)) {
          detector = new UniversalDetector(null);

          int readCount;
          while ((readCount = stream.read(buf)) > 0 && !detector.isDone()) {
            detector.handleData(buf, 0, readCount);
          }
        }
        detector.dataEnd();

        Charset encoding = Charsets.UTF_8;
        try {
          String detectedEncoding = detector.getDetectedCharset();
          if (detectedEncoding != null) {
            encoding = Charset.forName(detectedEncoding);
          }
        } catch (UnsupportedCharsetException e) {
          //ignore, we use UTF-8 as a fallback
        }
        charset = Optional.of(encoding);
      } catch (IOException e) {
        charset = Optional.of(Charsets.UTF_8);
      }
    }
    this.charset = charset.orElse(null);
    this.name = name;
    this.file = file;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public File getFile() {
    return file;
  }

  @Override
  public Reader getReader() throws IOException {
    final InputStream inputStream = new FileInputStream(file);

    try {
      final InputStream in = new GZIPInputStream(inputStream);
      toClose.add(in);

      final InputStreamReader inputStreamReader = new InputStreamReader(in, charset);
      toClose.add(inputStreamReader);

      return inputStreamReader;
    } catch (ZipException ze) {
      inputStream.close();

      final InputStream in = new FileInputStream(file);
      toClose.add(in);

      final InputStreamReader inputStreamReader = new InputStreamReader(in, charset);
      toClose.add(inputStreamReader);

      return inputStreamReader;
    }
  }

  @Override
  public MediaType getMimeType() {
    return mimeType;
  }

  @Override
  public void close() throws Exception {
    final ListIterator<Closeable> closeableListIterator = toClose.listIterator();
    while (closeableListIterator.hasNext()) {
      Closeable closeable = closeableListIterator.next();
      closeable.close();
      closeableListIterator.remove();
    }
  }
}
