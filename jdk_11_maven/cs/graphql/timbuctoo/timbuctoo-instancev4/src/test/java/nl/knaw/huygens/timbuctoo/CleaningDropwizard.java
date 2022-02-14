package nl.knaw.huygens.timbuctoo;

import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import nl.knaw.huygens.timbuctoo.server.TimbuctooV4;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import static java.nio.file.Files.walkFileTree;

public class CleaningDropwizard extends DropwizardAppRule<TimbuctooConfiguration> {
  private final Path dataPath;

  public CleaningDropwizard(String configPath, Path dataPath) {
    super(TimbuctooV4.class, configPath);
    this.dataPath = dataPath;
  }

  @Override
  protected void before() throws Exception {
    try {
      if (dataPath.toFile().exists()) {
        walkFileTree(dataPath, new SimpleFileVisitor<Path>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            Files.delete(dir);
            return FileVisitResult.CONTINUE;
          }
        });
      }
      dataPath.toFile().mkdirs();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    super.before();
  }
}
