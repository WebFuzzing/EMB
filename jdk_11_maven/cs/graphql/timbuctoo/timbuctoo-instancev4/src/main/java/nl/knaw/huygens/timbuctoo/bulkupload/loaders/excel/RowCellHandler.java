package nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel;

public interface RowCellHandler {
  void start(String name);

  void startRow(int rowNum);

  void cell(short column, String value, String cellStyleStr);

  void endRow(int rowNum);

  void finish();
}
