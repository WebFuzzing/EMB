package nl.knaw.huygens.timbuctoo.bulkupload.loaders;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import nl.knaw.huygens.timbuctoo.bulkupload.InvalidFileException;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.Importer;
import nl.knaw.huygens.timbuctoo.util.Tuple;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * A Loader applies an Importer to the contents of one or more Files.
 * Specific Loader implementations know how to parse a file format and call the
 * required Importer methods.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface Loader {
  /**
   * Loads data from a list of files.
   *
   * @param files    Pairs of (original filename, local File).
   * @param importer Importer that will accept this Loader's data.
   */
  void loadData(List<Tuple<String, File>> files, Importer importer) throws InvalidFileException, IOException;
}
