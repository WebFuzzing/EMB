/* ====================================================================
Based on org.apache.poi.xssf.eventusermodel.XssFSheetHandler
==================================================================== */

package nl.knaw.huygens.timbuctoo.bulkupload.loaders.excel;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static org.apache.poi.xssf.usermodel.XSSFRelation.NS_SPREADSHEETML;

/**
 * This class handles the processing of a sheet#.xml
 *  sheet part of a XSSF .xlsx file, and generates
 *  row and cell events for it.
 */
public class SheetXmlParser extends DefaultHandler {
  private static final POILogger logger = POILogFactory.getLogger(XSSFSheetXMLHandler.class);
  private String debug;
  private String cellStyleStr;


  /**
   * These are the different kinds of cells we support.
   * We keep track of the current one between
   *  the start and end.
   */
  enum XssfDataType {
    BOOLEAN,
    ERROR,
    FORMULA,
    INLINE_STRING,
    SST_STRING,
    NUMBER
  }

  /**
   * Read only access to the shared strings table, for looking
   *  up (most) string cell's contents
   */
  private SharedStringsTable sharedStringsTable;

  /**
   * Where our text is going
   */
  private final RowCellHandler output;

  // Set when V start element is seen
  private boolean valueIsOpen;
  // Set when an Inline String "is" is seen
  private boolean isIsOpen;

  // Set when cell start element is seen;
  // used when cell close element is seen.
  private XssfDataType nextDataType;

  // Used to format numeric cell values.
  private int rowNum;
  // some sheets do not have rowNums, Excel can read them so we should try to handle them correctly as well
  private int nextRowNum;
  private short column;

  // Gathers characters as they are seen.
  private StringBuffer value = new StringBuffer();

  public SheetXmlParser(SharedStringsTable strings, RowCellHandler contentsHandler) {
    this.sharedStringsTable = strings;
    this.output = contentsHandler;
    this.nextDataType = XssfDataType.NUMBER;
  }

  private boolean isTextTag(String name) {
    if ("v".equals(name)) {
      // Easy, normal v text tag
      return true;
    }
    if ("inlineStr".equals(name)) {
      // Easy inline string
      return true;
    }
    if ("t".equals(name) && isIsOpen) {
      // Inline string <is><t>...</t></is> pair
      return true;
    }
    // It isn't a text tag
    return false;
  }

  @Override
  public void startElement(String uri, String localName, String qualifiedName,
                           Attributes attributes) throws SAXException {

    if (uri != null && ! uri.equals(NS_SPREADSHEETML)) {
      return;
    }

    if (isTextTag(localName)) {
      valueIsOpen = true;
      // Clear contents cache
      value.setLength(0);
    } else if ("is".equals(localName)) {
      // Inline string outer tag
      isIsOpen = true;
    } else if ("f".equals(localName)) {
      // Mark us as being a formula if not already
      if (nextDataType == XssfDataType.NUMBER) {
        nextDataType = XssfDataType.FORMULA;
      }
    } else if ("row".equals(localName)) {
      String rowNumStr = attributes.getValue("r");
      if (rowNumStr != null) {
        rowNum = Integer.parseInt(rowNumStr) - 1;
      } else {
        rowNum = nextRowNum;
      }
      output.startRow(rowNum);
    } else if ("c".equals(localName)) { // c => cell

      debug = attributes.getValue("r");
      column = new CellReference(debug).getCol();

      cellStyleStr = attributes.getValue("s");
      String cellType = attributes.getValue("t");
      this.nextDataType = XssfDataType.NUMBER;
      if ("b".equals(cellType)) {
        nextDataType = XssfDataType.BOOLEAN;
      } else if ("e".equals(cellType)) {
        nextDataType = XssfDataType.ERROR;
      } else if ("inlineStr".equals(cellType)) {
        nextDataType = XssfDataType.INLINE_STRING;
      } else if ("s".equals(cellType)) {
        nextDataType = XssfDataType.SST_STRING;
      } else if ("str".equals(cellType)) {
        nextDataType = XssfDataType.FORMULA;
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qualifiedName)
    throws SAXException {

    if (uri != null && ! uri.equals(NS_SPREADSHEETML)) {
      return;
    }

    String thisStr = null;

    // v => contents of a cell
    if (isTextTag(localName)) {
      valueIsOpen = false;

      // Process the value contents as required, now we have it all
      switch (nextDataType) {
        case BOOLEAN:
          char first = value.charAt(0);
          thisStr = first == '0' ? "F" : "T";
          break;

        case ERROR:
          thisStr = "ERROR:" + value.toString();
          break;

        case FORMULA:
          thisStr = value.toString();
          break;

        case INLINE_STRING:
          // TODO: Can these ever have formatting on them?
          XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
          thisStr = rtsi.toString();
          break;

        case SST_STRING:
          String sstIndex = value.toString();
          try {
            int idx = Integer.parseInt(sstIndex);
            XSSFRichTextString rtss = new XSSFRichTextString(sharedStringsTable.getEntryAt(idx));
            thisStr = rtss.toString();
          } catch (NumberFormatException ex) {
            logger.log(POILogger.ERROR, "Failed to parse SST index '" + sstIndex, ex);
          }
          break;

        case NUMBER:
          thisStr = value.toString();
          break;

        default:
          thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
          break;
      }
      output.cell(column, thisStr, cellStyleStr);

    } else if ("is".equals(localName)) {
      isIsOpen = false;
    } else if ("row".equals(localName)) {
      // Finish up the row
      output.endRow(rowNum);

      // some sheets do not have rowNum set in the XML, Excel can read them so we should try to read them as well
      nextRowNum = rowNum + 1;
    }
  }

  /**
   * Captures characters only if a suitable element is open.
   * Originally was just "v"; extended for inlineStr also.
   */
  @Override
  public void characters(char[] ch, int start, int length)
    throws SAXException {
    if (valueIsOpen) {
      value.append(ch, start, length);
    }
  }
}
