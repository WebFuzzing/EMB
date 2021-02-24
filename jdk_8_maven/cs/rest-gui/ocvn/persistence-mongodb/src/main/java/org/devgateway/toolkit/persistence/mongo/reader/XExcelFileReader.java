package org.devgateway.toolkit.persistence.mongo.reader;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * XSSF and XML Stream Reader
 *
 * If memory footprint is an issue, then for XSSF, you can get at the underlying
 * XML data, and process it yourself. This is intended for intermediate
 * developers who are willing to learn a little bit of low level structure of
 * .xlsx files, and who are happy processing XML in java. Its relatively simple
 * to use, but requires a basic understanding of the file structure. The
 * advantage provided is that you can read a XLSX file with a relatively small
 * memory footprint.
 *
 * @author lchen
 *         http://lchenaction.blogspot.com/2013/12/how-to-read-super-large-excel
 *         -and-csv.html
 *
 */
public class XExcelFileReader {
    private int rowNum = 0;
    private OPCPackage opcPkg;
    private ReadOnlySharedStringsTable stringsTable;
    private XMLStreamReader xmlReader;

    /**
     * Reads data from the specified excel file and from the given sheet name
     *
     * @param excelPath
     * @param sheetName
     * @throws Exception
     */
    public XExcelFileReader(final String excelPath, final String sheetName) throws Exception {
        opcPkg = OPCPackage.open(excelPath, PackageAccess.READ);
        this.stringsTable = new ReadOnlySharedStringsTable(opcPkg);

        XSSFReader xssfReader = new XSSFReader(opcPkg);
        XMLInputFactory factory = XMLInputFactory.newInstance();

        InputStream inputStream = null;
        XSSFReader.SheetIterator it = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        while (it.hasNext()) {
            InputStream tempStream = it.next();
            if (!it.getSheetName().equals(sheetName)) {
                continue;
            }
            inputStream = tempStream;
            break;
        }

        if (inputStream == null) {
            opcPkg.close();
            throw new RuntimeException(String.format("No Excel sheet with name %s present.", sheetName));
        }

        xmlReader = factory.createXMLStreamReader(inputStream);

        while (xmlReader.hasNext()) {
            xmlReader.next();
            if (xmlReader.isStartElement()) {
                if (xmlReader.getLocalName().equals("sheetData")) {
                    break;
                }
            }
        }
    }

    public int rowNum() {
        return rowNum;
    }

    public List<String[]> readRows(final int batchSize) throws XMLStreamException {
        String elementName = "row";
        List<String[]> dataRows = new ArrayList<String[]>();
        if (batchSize > 0) {
            while (xmlReader.hasNext()) {
                xmlReader.next();
                if (xmlReader.isStartElement()) {
                    if (xmlReader.getLocalName().equals(elementName)) {
                        rowNum++;
                        dataRows.add(getDataRow());
                        if (dataRows.size() == batchSize) {
                            break;
                        }
                    }
                }
            }
        }
        return dataRows;
    }

    private String[] getDataRow() throws XMLStreamException {
        List<String> rowValues = new ArrayList<String>();
        while (xmlReader.hasNext()) {
            xmlReader.next();
            if (xmlReader.isStartElement()) {
                if (xmlReader.getLocalName().equals("c")) {
                    CellReference cellReference = new CellReference(xmlReader.getAttributeValue(null, "r"));
                    // Fill in the possible blank cells!
                    while (rowValues.size() < cellReference.getCol()) {
                        rowValues.add("");
                    }
                    String cellType = xmlReader.getAttributeValue(null, "t");
                    rowValues.add(getCellValue(cellType));
                }
            } else if (xmlReader.isEndElement() && xmlReader.getLocalName().equals("row")) {
                break;
            }
        }
        return rowValues.toArray(new String[rowValues.size()]);
    }

    private String getCellValue(final String cellType) throws XMLStreamException {
        String value = ""; // by default
        while (xmlReader.hasNext()) {
            xmlReader.next();
            if (xmlReader.isStartElement()) {
                //this is for xlsx without stringsTable, just inline
                if (xmlReader.getLocalName().equals("is")) {
                    if (cellType != null && cellType.equals("inlineStr")) {
                        while (xmlReader.hasNext()) {
                            xmlReader.next();

                            if (xmlReader.isStartElement() && xmlReader.getLocalName().equals("t")) {
                                return xmlReader.getElementText();
                            } else {
                                if (xmlReader.isEndElement() && xmlReader.getLocalName().equals("t")) {
                                    break;
                                }
                            }

                        }
                    }

                }

                //this part is for xlsx with stringsTable
                if (xmlReader.getLocalName().equals("v")) {
                    if (cellType != null && cellType.equals("s")) {
                        int idx = Integer.parseInt(xmlReader.getElementText());
                        return new XSSFRichTextString(stringsTable.getEntryAt(idx)).toString();
                    } else {
                        return xmlReader.getElementText();
                    }
                }
            } else if (xmlReader.isEndElement() && xmlReader.getLocalName().equals("c")) {
                break;
            }
        }
        return value;
    }

    public void close() {
        if (opcPkg != null) {
            try {
                opcPkg.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}