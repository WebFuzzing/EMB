package nl.knaw.huygens.timbuctoo.v5.filestorage.dto;

import java.io.IOException;
import java.io.Reader;

/**
 * In interface that represents a log file that is already stored locally and can be repeatedly
 * read from.
 */
public interface CachedLog extends CachedFile {
  Reader getReader() throws IOException;
}
