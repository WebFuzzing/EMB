package nl.knaw.huygens.timbuctoo.security.healthchecks;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.Supplier;

public class DirectoryHealthCheck implements Supplier<Optional<String>> {
  private final Path pathToDirectory;

  public DirectoryHealthCheck(Path pathToDirectory) {
    this.pathToDirectory = pathToDirectory;
  }

  @Override
  public Optional<String> get() {
    if (!Files.isReadable(pathToDirectory)) {
      return Optional.of(String.format("'%s' is not available.", pathToDirectory.toAbsolutePath()));
    }

    if (!Files.isDirectory(pathToDirectory)) {
      return Optional.of(String.format("'%s' is not a directory.", pathToDirectory.toAbsolutePath()));
    }

    if (!Files.isWritable(pathToDirectory)) {
      return Optional.of(String.format("'%s' is not writable.", pathToDirectory.toAbsolutePath()));
    }

    return Optional.empty();
  }
}
