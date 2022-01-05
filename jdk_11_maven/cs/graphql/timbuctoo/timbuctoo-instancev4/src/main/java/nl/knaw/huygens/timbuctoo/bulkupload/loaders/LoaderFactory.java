package nl.knaw.huygens.timbuctoo.bulkupload.loaders;

import nl.knaw.huygens.timbuctoo.bulkupload.loaders.access.MdbLoader;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.csv.CsvLoader;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.dataperfect.DataPerfectLoader;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel.allsheetloader.AllSheetLoader;

import java.util.Map;
import java.util.Optional;

public class LoaderFactory {
  public static Optional<Loader> createFor(String mimeType, Map<String, String> extraConfig) {
    switch (mimeType) {
      case "text/csv":
        return Optional.of(new CsvLoader(extraConfig));
      case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
        return Optional.of(new AllSheetLoader());
      case "application/x.dataperfect":
        return Optional.of(new DataPerfectLoader());
      case "application/x-msaccess":
        return Optional.of(new MdbLoader());
      default:
        return Optional.empty();
    }
  }
}
