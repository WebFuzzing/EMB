package nl.knaw.huygens.timbuctoo.v5.rdfio.implementations.rdf4j;

import nl.knaw.huygens.timbuctoo.v5.dataset.exceptions.RdfProcessingParseException;
import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedLog;
import org.eclipse.rdf4j.rio.RDFParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

public class Rdf4jRdfProcessingParseException extends RdfProcessingParseException {

  public Rdf4jRdfProcessingParseException(RDFParseException exception, CachedLog log) {
    super(createMessage(exception, log));
  }

  private static String createMessage(RDFParseException exception, CachedLog log) {
    long lineNumber = exception.getLineNumber();
    // get three lines before the error
    long firstLineOfMessage = Math.max(lineNumber - 4, 0);
    // get three lines before after the error
    long lastLineOfMessage = lineNumber + 3;
    Path file = log.getFile().toPath();
    StringBuilder message = new StringBuilder(exception.getMessage());
    message.append("\n");
    try (Stream<String> lines = Files.lines(file)) {
      Iterator<String> lineIterator = lines.skip(firstLineOfMessage).iterator();

      for (long line = firstLineOfMessage; line < lastLineOfMessage && lineIterator.hasNext(); line++) {
        message.append("  ").append(line + 1).append(": ").append(lineIterator.next()).append("\n");
      }
      return message.toString();
    } catch (IOException e) {
      return exception.getMessage();
    }
  }
}
