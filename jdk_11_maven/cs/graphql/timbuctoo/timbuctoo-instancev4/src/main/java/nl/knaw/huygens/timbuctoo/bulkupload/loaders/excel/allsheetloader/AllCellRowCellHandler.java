package nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel.allsheetloader;

import nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel.RowCellHandler;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.Importer;
import org.slf4j.Logger;

import static org.slf4j.LoggerFactory.getLogger;

public class AllCellRowCellHandler implements RowCellHandler {
  private final Importer importer;
  private boolean entityStarted;
  private boolean headerRow = false;
  private boolean rowStarted = false;
  private static final Logger LOG = getLogger(AllCellRowCellHandler.class);

  public AllCellRowCellHandler(Importer importer) {
    this.importer = importer;
  }

  @Override
  public void start(String name) {
    entityStarted = false;
    rowStarted = false;
    headerRow = true;
    importer.startCollection(name);
  }

  @Override
  public void startRow(int rowNum) {
    if (rowStarted) {
      LOG.error("call endRow() before calling startRow()");
    }
    rowStarted = true;
  }

  @Override
  public void cell(short column, String value, String cellStyleStr) {
    if (!rowStarted) {
      LOG.error("call startRow() before calling cell()");
    }
    if (headerRow) {
      importer.registerPropertyName(column, value);
    } else {
      if (!entityStarted) {
        entityStarted  = true;
        importer.startEntity();
      }
      importer.setValue(column, value);
    }
  }

  @Override
  public void endRow(int rowNum) {
    if (headerRow) {
      headerRow = false;
    }
    if (entityStarted) {
      entityStarted = false;
      importer.finishEntity();
    }

    rowStarted = false;
  }

  @Override
  public void finish() {
    importer.finishCollection();
  }
}
