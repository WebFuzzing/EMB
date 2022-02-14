package nl.knaw.huygens.timbuctoo.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.io.Resources.getResource;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileHelpers {

  public static Path getFileFromResource(Class sourceClass, String resourcePath) throws IOException {
    Path result = makeTempFilePath(true);
    Files.copy(getResource(sourceClass, resourcePath).openStream(), result, REPLACE_EXISTING);
    return result;
  }

  public static Path makeTempFilePath(boolean exists) throws IOException {
    Path result;
    if (System.getenv().containsKey("TEST_TMPDIR")) {
      result = Files.createTempFile(Paths.get(System.getenv("TEST_TMPDIR")), "", "json");
    } else {
      result = Files.createTempFile("", "json");
    }
    if (!exists) {
      Files.delete(result);
    }
    return result;
  }

  public static Path makeTempDir() throws IOException {
    Path result = makeTempFilePath(false);
    Files.createDirectory(result);
    return result;
  }

}
