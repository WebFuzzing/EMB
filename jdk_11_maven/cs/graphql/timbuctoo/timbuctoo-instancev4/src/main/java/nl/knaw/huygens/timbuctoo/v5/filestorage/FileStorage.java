package nl.knaw.huygens.timbuctoo.v5.filestorage;

import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedFile;

import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public interface FileStorage {
  /**
   * Stores the file somewhere safe and return a token that can be used to retrieve it
   * @param stream The file contents
   * @param fileName The name of the file (does not need to be unique, may be an empty string or null)
   * @param mediaType The mediatype, if available
   * @return a token that can be used to retrieve the file using getFile
   */
  String saveFile(InputStream stream, String fileName, MediaType mediaType) throws IOException;

  /**
   * Returns a previously stored file
   *
   * <p>A saver should never loose track of a previously saved file
   * @param token the token that the saver returned while saving
   * @return A CachedFile for the file that was stored
   */
  Optional<CachedFile> getFile(String token) throws IOException;

  void clear() throws IOException;

}
