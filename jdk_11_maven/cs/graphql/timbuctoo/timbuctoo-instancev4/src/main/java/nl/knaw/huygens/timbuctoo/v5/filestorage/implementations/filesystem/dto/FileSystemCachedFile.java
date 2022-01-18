package nl.knaw.huygens.timbuctoo.v5.filestorage.implementations.filesystem.dto;

import nl.knaw.huygens.timbuctoo.v5.filestorage.dto.CachedFile;

import javax.ws.rs.core.MediaType;
import java.io.File;

public class FileSystemCachedFile implements CachedFile {

  private final MediaType mimeType;
  private final String name;
  private final File file;

  public FileSystemCachedFile(MediaType mimeType, String name, File file) {
    this.mimeType = mimeType;
    this.name = name;
    this.file = file;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public File getFile() {
    return this.file;
  }

  @Override
  public MediaType getMimeType() {
    return mimeType;
  }

  @Override
  public void close() throws Exception {

  }
}
