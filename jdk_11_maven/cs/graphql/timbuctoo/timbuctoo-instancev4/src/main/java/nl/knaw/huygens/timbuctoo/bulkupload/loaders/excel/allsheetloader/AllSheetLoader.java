package nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel.allsheetloader;

import nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel.RowCellHandler;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel.XlsxLoader;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.Importer;

public class AllSheetLoader extends XlsxLoader {
  @Override
  protected RowCellHandler makeRowCellHandler(Importer importer) {
    return new AllCellRowCellHandler(importer);
  }
}
