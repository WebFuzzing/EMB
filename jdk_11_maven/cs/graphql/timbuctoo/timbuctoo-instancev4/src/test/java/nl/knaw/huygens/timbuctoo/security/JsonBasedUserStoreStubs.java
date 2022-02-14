package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.dataaccess.localfile.LocalFileUserAccess;

import java.nio.file.Path;

public class JsonBasedUserStoreStubs {
  public static JsonBasedUserStore forFile(Path file) {
    return new JsonBasedUserStore(new LocalFileUserAccess(file));
  }
}
