package nl.knaw.huygens.timbuctoo.v5.filestorage;

import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedLog;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Optional;

public interface LogStorage {
  /**
   * Stores the file somewhere safe and return a token that can be used to retrieve it
   * @param stream The file contents
   * @param fileName The name of the file (does not need to be unique, may be an empty string or null)
   * @param mediaType The mediatype, if available
   * @return a token that can be used to retrieve the file using getFile
   */
  String saveLog(InputStream stream, String fileName, MediaType mediaType, Optional<Charset> charset)
      throws IOException;

  /**
   * Returns a previously stored file
   *
   * <p>A saver should never loose track of a previously saved file
   * @param token the token that the saver returned while saving
   * @return A CachedFile for the file that was stored
   */
  CachedLog getLog(String token) throws IOException;

}
