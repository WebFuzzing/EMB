package nl.knaw.huygens.timbuctoo.bulkupload.loaders.dataperfect;

import com.google.common.io.PatternFilenameFilter;
import nl.knaw.dans.common.dataperfect.DataPerfectLibException;
import nl.knaw.dans.common.dataperfect.Database;
import nl.knaw.dans.common.dataperfect.Field;
import nl.knaw.dans.common.dataperfect.NoSuchRecordFieldException;
import nl.knaw.dans.common.dataperfect.Panel;
import nl.knaw.dans.common.dataperfect.Record;
import nl.knaw.huygens.timbuctoo.bulkupload.InvalidFileException;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.Loader;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.Importer;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import static com.google.common.io.Files.createTempDir;
import static org.slf4j.LoggerFactory.getLogger;

public class DataPerfectLoader implements Loader {

  private static final Logger LOG = getLogger(DataPerfectLoader.class);
  private final Map<String, Integer> genericNameCounters = new HashMap<>();

  @Override
  public void loadData(List<Tuple<String, File>> files, Importer importer) throws InvalidFileException, IOException {
    Database dbPerfectdb = null;
    try {
      File output = writeZipToTempDir(files.get(0).getRight());

      File dirWithDb = unwrapToplevelDirIfNeeded(output);
      File structureFile = getStructureFile(dirWithDb);

      dbPerfectdb = new Database(structureFile);
      importDbPerfect(importer, dbPerfectdb);
      delete(output);
    } catch (DataPerfectLibException e) {
      throw new InvalidFileException("Not a valid dataperfect file", e);
    } finally {
      try {
        if (dbPerfectdb != null) {
          dbPerfectdb.close();
        }
      } catch (IOException e) {
        LOG.error("Something went wrong while closing the resources in the data import", e);
      }
    }
  }

  private void delete(File file) throws IOException {
    if (file.isDirectory()) {
      for (File c : file.listFiles()) {
        delete(c);
      }
    }
    file.delete();
  }


  private void importDbPerfect(Importer importer, Database dbPerfectdb)
    throws IOException, DataPerfectLibException {
    dbPerfectdb.open();
    for (Panel panel : dbPerfectdb.getPanels()) {
      importer.startCollection(orGeneric(panel.getName(), "sheet"));

      final List<Field> fields = panel.getFields();
      for (Field field : fields) {
        importer.registerPropertyName(field.getNumber(), orGeneric(field.getName(), "column"));
      }

      Iterator<Record> recordIterator = panel.recordIterator();
      while (recordIterator.hasNext()) {
        Record cur = recordIterator.next();
        importer.startEntity();
        for (Field field : fields) {
          try {
            String value = cur.getValueAsString(field.getNumber());
            importer.setValue(field.getNumber(), value);
          } catch (NoSuchRecordFieldException e) {
            LOG.error("Not all records contain all fields apparently", e);
          }
        }
        importer.finishEntity();
      }
      importer.finishCollection();
    }
  }

  private String orGeneric(String name, String prefix) {
    int num = genericNameCounters.compute(prefix, (key, value) -> value == null ? 1 : value + 1);
    if (name == null || name.length() == 0) {
      name = prefix + "-" + num;
    }
    return name;
  }

  private File getStructureFile(File dirWithDb) throws InvalidFileException {
    //Look for the STR file
    File[] dbfiles = dirWithDb.listFiles(new PatternFilenameFilter(Pattern.compile(".*\\.STR")));
    if (dbfiles == null || dbfiles.length != 1) {
      throw new InvalidFileException("I expected one .STR file in the zip");
    }
    return dbfiles[0];
  }

  private File unwrapToplevelDirIfNeeded(File output) {
    File dirWithDb;
    //often a zip file contains 1 directory with the actual contents
    File[] files = output.listFiles();
    if (files != null && files.length == 1 && files[0].isDirectory()) {
      dirWithDb = files[0];
    } else {
      dirWithDb = output;
    }
    return dirWithDb;
  }

  private File writeZipToTempDir(File source) throws IOException, InvalidFileException {
    File output = createTempDir();
    try (ZipInputStream zis = new ZipInputStream(new FileInputStream(source))) {
      ZipEntry ze;
      byte[] buffer = new byte[1024];
      try {
        while ((ze = zis.getNextEntry()) != null) {
          String fileName = ze.getName();
          File newFile = new File(output.getAbsolutePath() + File.separator + fileName);

          if (ze.isDirectory()) {
            newFile.mkdirs();
          } else {
            //create all non exists folders
            //else you will hit FileNotFoundException for compressed folder
            new File(newFile.getParent()).mkdirs();

            try (FileOutputStream fos = new FileOutputStream(newFile)) {
              int len;
              while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
              }
            }
          }
        }
      } catch (ZipException e) {
        throw new InvalidFileException("The dataperfect archive should be sent as a ZIP encoded directory");
      }
    }
    return output;
  }
}
