package nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel;

import nl.knaw.huygens.timbuctoo.bulkupload.InvalidFileException;
import nl.knaw.huygens.timbuctoo.bulkupload.loaders.Loader;
import nl.knaw.huygens.timbuctoo.bulkupload.parsingstatemachine.Importer;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public abstract class XlsxLoader implements Loader {

  @Override
  public void loadData(List<Tuple<String, File>> files, Importer importer) throws InvalidFileException, IOException {
    try {
      RowCellHandler rowCellHandler = makeRowCellHandler(importer);
      SAXParserFactory saxFactory = SAXParserFactory.newInstance();
      saxFactory.setNamespaceAware(true);
      for (Tuple<String, File> file : files) {
        OPCPackage pkg = OPCPackage.open(file.getRight().getPath());
        XSSFReader xssfReader = new XSSFReader(pkg);

        final SharedStringsTable sharedStringsTable = xssfReader.getSharedStringsTable();
        XSSFReader.SheetIterator worksheets = (XSSFReader.SheetIterator) xssfReader.getSheetsData();

        while (worksheets.hasNext()) {
          final InputStream sheet = worksheets.next();

          XMLReader sheetParser = saxFactory.newSAXParser().getXMLReader();
          sheetParser.setContentHandler(new SheetXmlParser(sharedStringsTable, rowCellHandler));

          rowCellHandler.start(worksheets.getSheetName());
          sheetParser.parse(new InputSource(sheet));
          rowCellHandler.finish();
        }
      }
    } catch (SAXException | OpenXML4JException | ParserConfigurationException e) {
      throw new InvalidFileException("Not a valid Excel file", e);
    }
  }

  protected abstract RowCellHandler makeRowCellHandler(Importer importer);
}
