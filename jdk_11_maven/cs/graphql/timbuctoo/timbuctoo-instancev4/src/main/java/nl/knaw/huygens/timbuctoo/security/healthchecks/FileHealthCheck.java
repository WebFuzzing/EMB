package nl.knaw.huygens.timbuctoo.security.healthchecks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

public class FileHealthCheck implements Supplier<Optional<String>> {
  private final Path pathToFile;

  public FileHealthCheck(Path pathToFile) {
    this.pathToFile = pathToFile;
  }

  @Override
  public Optional<String> get() {
    if (!Files.isReadable(pathToFile)) {
      return Optional.of(String.format("'%s' is not available.", pathToFile.toAbsolutePath()));
    }

    if (!Files.isRegularFile(pathToFile)) {
      return Optional.of(String.format("'%s' is not a regular file.", pathToFile.toAbsolutePath()));
    }

    if (!Files.isWritable(pathToFile)) {
      return Optional.of(String.format("'%s' is not writable.", pathToFile.toAbsolutePath()));
    }

    return Optional.empty();

  }
}
