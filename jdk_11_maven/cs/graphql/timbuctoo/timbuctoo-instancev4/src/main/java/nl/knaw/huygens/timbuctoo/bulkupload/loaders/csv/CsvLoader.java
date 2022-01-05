package nl.knaw.huygens.timbuctoo.bulkupload.loaders.csv;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import nl.knaw.huygens.timbuctoo.bulkupload.InvalidFileException;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.Loader;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.Importer;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.QuoteMode;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * A CsvLoader parses CSV files and treats each file as a separate collection.
 * The default format is Excel's CSV format, with a header row.
 */
public class CsvLoader implements Loader {
  private final CSVFormat format;
  @JsonProperty("config")
  private final Map<String, String> config; // needed for serialization

  @JsonCreator
  public CsvLoader(@JsonProperty("config") Map<String, String> config) {
    CSVFormat format = CSVFormat.EXCEL;
    this.config = config;
    if (config.containsKey("delimiter")) {
      format = format.withDelimiter(onlyChar(config, "delimiter"));
    }
    if (config.containsKey("quoteChar")) {
      format = format.withQuote(onlyChar(config, "quoteChar"));
    }
    if (config.containsKey("quoteMode")) {
      format = format.withQuoteMode(QuoteMode.valueOf(config.get("quoteMode")));
    }
    if (config.containsKey("commentStart")) {
      format = format.withCommentMarker(onlyChar(config, "commentStart"));
    }
    if (config.containsKey("escape")) {
      format = format.withEscape(onlyChar(config, "escape"));
    }
    if (config.containsKey("ignoreSurroundingSpaces")) {
      format = format.withIgnoreSurroundingSpaces(config.get("ignoreSurroundingSpaces").equals("true"));
    }
    if (config.containsKey("ignoreEmptyLines")) {
      format = format.withIgnoreEmptyLines(config.get("ignoreEmptyLines").equals("true"));
    }
    if (config.containsKey("recordSeparator")) {
      format = format.withRecordSeparator(config.get("recordSeparator"));
    }
    if (config.containsKey("nullString")) {
      format = format.withNullString(config.get("nullString"));
    }
    if (config.containsKey("trim")) {
      format = format.withTrim(config.get("trim").equals("true"));
    }
    if (config.containsKey("trailingDelimiter")) {
      format = format.withTrailingDelimiter(config.get("trailingDelimiter").equals("true"));
    }
    this.format = format
      .withAllowMissingColumnNames()
      .withHeader();
  }

  private static char onlyChar(Map<String, String> config, String key) {
    String value = config.get(key);
    if (value.length() != 1) {
      throw new IllegalArgumentException(String.format("expected a single character for %s, got %s", key, value));
    }
    return value.charAt(0);
  }

  @Override
  public void loadData(List<Tuple<String, File>> files, Importer importer) throws InvalidFileException, IOException {
    for (Tuple<String, File> file : files) {
      CSVParser parser = format.parse(new FileReader(file.getRight()));

      String filename = file.getLeft();
      //remove well-known extensions
      if (filename.endsWith(".csv") || filename.endsWith(".tsv") || filename.endsWith(".txt")) {
        filename = filename.substring(0, filename.length() - 4);
      }
      importer.startCollection(filename);

      parser.getHeaderMap().forEach((name, column) -> importer.registerPropertyName(column, name));

      parser.forEach(row -> {
        importer.startEntity();
        for (int i = 0; i < row.size(); i++) {
          importer.setValue(i, row.get(i));
        }
        importer.finishEntity();
      });

      importer.finishCollection();
    }
  }
}
