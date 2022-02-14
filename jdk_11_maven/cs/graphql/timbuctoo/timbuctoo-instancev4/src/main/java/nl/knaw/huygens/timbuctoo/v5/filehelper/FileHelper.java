package nl.knaw.huygens.timbuctoo.v5.filehelper;

import java.io.File;

public class FileHelper {
  private final File rootDir;

  /**
   * Creation of the class will also create the root directory.
   */
  public FileHelper(String rootDir) {
    this(new File(rootDir));
  }

  /**
   * Creation of the class will also create the root directory.
   */
  public FileHelper(File rootDir) {
    this.rootDir = rootDir;
    rootDir.mkdir();
  }

  public boolean dataSetExists(String userId, String dataSetId) {
    final File file = new File(new File(rootDir, userId), dataSetId);
    return file.exists();
  }

  /**
   * Creates a File object and creates the paths on the file system as well.
   */
  public File pathInDataSet(String userId, String dataSetId, String pathToCreate) {
    File dataSetPath = dataSetPath(userId, dataSetId);
    File path = createPathToFileSystem(dataSetPath, pathToCreate);
    return path;
  }

  /**
   * Creates the path of the data set but not that of the file.
   */
  public File fileInDataSet(String userId, String dataSetId, String fileName) {
    File dataSetPath = dataSetPath(userId, dataSetId);
    return new File(dataSetPath, fileName);
  }

  public File dataSetPath(String userId, String dataSetId) {
    File userPath = createPathToFileSystem(rootDir, userId);
    return createPathToFileSystem(userPath, dataSetId);
  }

  private File createPathToFileSystem(File parent, String pathToCreate) {
    File path = new File(parent, pathToCreate);
    path.mkdir();
    return path;
  }

}
