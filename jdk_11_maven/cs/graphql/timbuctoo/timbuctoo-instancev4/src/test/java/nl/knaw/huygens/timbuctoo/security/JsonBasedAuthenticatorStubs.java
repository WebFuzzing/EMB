package nl.knaw.huygens.timbuctoo.security;

import nl.knaw.huygens.timbuctoo.security.dataaccess.localfile.LocalFileLoginAccess;

import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class JsonBasedAuthenticatorStubs {
  public static JsonBasedAuthenticator backedByFile(Path file) {
    try {
      return new JsonBasedAuthenticator(new LocalFileLoginAccess(file), "SHA-256");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static JsonBasedAuthenticator withAlgorithm(Path file, String algorithm) {
    try {
      return new JsonBasedAuthenticator(new LocalFileLoginAccess(file), algorithm);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException(e);
    }
  }

  public static JsonBasedAuthenticator throwingWithAlgorithm(Path file, String algorithm)
    throws NoSuchAlgorithmException {
    return new JsonBasedAuthenticator(new LocalFileLoginAccess(file), algorithm);
  }

}
