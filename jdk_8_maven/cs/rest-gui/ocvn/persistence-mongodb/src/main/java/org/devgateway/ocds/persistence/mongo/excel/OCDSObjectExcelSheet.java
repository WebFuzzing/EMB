package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExportSepareteSheet;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Implementation of a {@link ExcelSheet} interface for an OCDS Object.
 * An OCDS Object can pe exported in several ways:
 *      * in a separate sheet {@link ExcelExportSepareteSheet}
 *      * in an existing sheet on a particular row (the object it's part of a main release phases)
 *      * in an existing row, but exported as a flatten object {@link #writeRowFlattenObject}
 *
 * @author idobre
 * @since 6/7/16
 */
public final class OCDSObjectExcelSheet extends AbstractExcelSheet {
    private static final Logger LOGGER = Logger.getLogger(OCDSObjectExcelSheet.class);

    private final Sheet excelSheet;

    private final String excelSheetName;

    private final Class clazz;

    private Map<String, Object> parentInfo;

    private final String headerPrefix;

    private static final String PARENTSHEET = "parentSheet";

    private static final String PARENTID = "parentID";

    private static final String PARENTROWNUMBER = "parentRowNumber";

    /**
     * Constructor used to print an OCDS Object in a separate excel sheet.
     * The sheet will be created based on object name that is exported
     *
     * @param workbook
     * @param clazz
     */
    OCDSObjectExcelSheet(final Workbook workbook, final Class clazz) {
        super(workbook);

        this.clazz = clazz;

        // get the excel sheet based on what type of object we export (release, award, contract)
        excelSheetName = clazz.getSimpleName().toLowerCase();

        headerPrefix = "";

        // it's possible that this sheet was created by other object
        if (workbook.getSheet(excelSheetName) == null) {
            // create the main excel sheet
            excelSheet = workbook.createSheet(excelSheetName);

            // freeze the header row
            excelSheet.createFreezePane(0, 1);

            // create the first row that is used as a header
            createRow(excelSheet, 0);

            // set a default width - this will increase the performance
            excelSheet.setDefaultColumnWidth(35);
        } else {
            excelSheet = workbook.getSheet(excelSheetName);
        }
    }

    OCDSObjectExcelSheet(final Workbook workbook, final Class clazz, final Map<String, Object> parentInfo) {
        this(workbook, clazz);

        this.parentInfo = parentInfo;
    }

    /**
     * Constructor used to print an OCDS Object in an existing excel sheet.
     * Normally this Object should be a child of one of the main notice phases.
     *
     * @param workbook
     * @param clazz
     * @param excelSheetName
     * @param headerPrefix
     */
    OCDSObjectExcelSheet(final Workbook workbook, final Class clazz,
                         final String excelSheetName, final String headerPrefix) {
        super(workbook);

        this.clazz = clazz;
        this.excelSheetName = excelSheetName;
        this.headerPrefix = headerPrefix;

        // we should already have an excel sheet since we call constructor from another OCDSObjectExcelSheet object
        excelSheet = workbook.getSheet(excelSheetName);
    }

    @Override
    public void writeRow(final Object object, final Row row) {
        final Iterator<Field> fields = OCDSObjectUtil.getFields(clazz);

        // first row should be the parent name with a link
        if (parentInfo != null) {
            String parentCell = (String) parentInfo.get(PARENTSHEET);
            if (parentInfo.get(PARENTID) != null) {
                parentCell += " - " + parentInfo.get(PARENTID);
            }
            if (row.getRowNum() < 2) {
                writeHeaderLabel("Parent", 0);
            }
            writeCellLink(parentCell, row, 0,
                    (String) parentInfo.get(PARENTSHEET), (int) parentInfo.get(PARENTROWNUMBER));
        }

        while (fields.hasNext()) {
            final Field field = fields.next();

            final int fieldType = OCDSObjectUtil.getFieldType(field);
            try {
                switch (fieldType) {
                    case FieldType.BASIC_FIELD:
                        // get the last 'free' cell
                        int coll = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();

                        try {
                            writeCell(object == null ? null
                                    : PropertyUtils.getProperty(object, field.getName()), row, coll);
                            if (row.getRowNum() < 2) {
                                writeHeaderLabel(field, coll);
                            }
                        } catch (NoSuchMethodException e) {
                            // do nothing
                        }

                        break;
                    case FieldType.OCDS_OBJECT_FIELD:
                        final OCDSObjectExcelSheet objectSheet = new OCDSObjectExcelSheet(
                                this.workbook,
                                OCDSObjectUtil.getFieldClass(field),
                                excelSheetName,
                                getHeaderPrefix() + field.getName());

                        if (field.getType().equals(java.util.Set.class)
                                || field.getType().equals(java.util.List.class)) {
                            final List<Object> ocdsFlattenObjects = new ArrayList();
                            if (object != null && PropertyUtils.isReadable(object, field.getName())) {
                                ocdsFlattenObjects.addAll(
                                        (Collection) PropertyUtils.getProperty(object, field.getName()));
                                objectSheet.writeRowFlattenObject(ocdsFlattenObjects, row);
                            }
                        } else {
                            objectSheet.writeRow(object == null ? null
                                    : PropertyUtils.getProperty(object, field.getName()), row);
                        }

                        break;
                    case FieldType.OCDS_OBJECT_SEPARETE_SHEET_FIELD:
                        final Map<String, Object> info = new HashMap();
                        info.put(PARENTSHEET, excelSheetName);
                        info.put(PARENTID, OCDSObjectUtil.getOCDSObjectID(object));
                        info.put(PARENTROWNUMBER, row.getRowNum() + 1);

                        final OCDSObjectExcelSheet objectSheetSepareteSheet = new OCDSObjectExcelSheet(
                                this.workbook, OCDSObjectUtil.getFieldClass(field), info);
                        final List<Object> ocdsObjectsSeparateSheet = new ArrayList();

                        if (object != null && PropertyUtils.getProperty(object, field.getName()) != null) {
                            if (field.getType().equals(java.util.Set.class)
                                    || field.getType().equals(java.util.List.class)) {
                                ocdsObjectsSeparateSheet.addAll(
                                        (Collection) PropertyUtils.getProperty(object, field.getName()));
                            } else {
                                ocdsObjectsSeparateSheet.add(PropertyUtils.getProperty(object, field.getName()));
                            }
                        }
                        int rowNumber = objectSheetSepareteSheet.writeSheetGetLink(ocdsObjectsSeparateSheet);

                        // get the last 'free' cell
                        coll = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
                        if (row.getRowNum() < 2) {
                            writeHeaderLabel(field, coll);
                        }

                        if (rowNumber != -1) {
                            writeCellLink(field.getName(), row, coll,
                                    objectSheetSepareteSheet.getExcelSheetName(), rowNumber);
                        } else {
                            writeCell(null, row, coll);
                        }

                        break;
                    default:
                        LOGGER.error("Undefined field type");
                        break;
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * Function to flat export an array of objects. Example:
     * [
     *      {
     *          x: aaa,
     *          y: bbb,
     *      },
     *      {
     *          x: ccc,
     *          y: ddd,
     *      }
     * ]
     *
     * Will be printed as:
     *      |     x     |     y     |
     *      | aaa ; bbb | ccc ; ddd |
     *
     * @param objects
     * @param row
     */
    public void writeRowFlattenObject(final List<Object> objects, final Row row) {
        final Iterator<Field> fields = OCDSObjectUtil.getFields(clazz);

        while (fields.hasNext()) {
            final Field field = fields.next();

            final int fieldType = OCDSObjectUtil.getFieldType(field);
            try {
                switch (fieldType) {
                    case FieldType.BASIC_FIELD:
                        try {
                            // get the last 'free' cell
                            int coll = row.getLastCellNum() == -1 ? 0 : row.getLastCellNum();
                            StringJoiner flattenValue = new StringJoiner(" | ");
                            for (Object obj : objects) {
                                if (obj != null && PropertyUtils.getProperty(obj, field.getName()) != null) {
                                    flattenValue.add(PropertyUtils.getProperty(obj, field.getName()).toString());
                                }
                            }

                            if (row.getRowNum() < 2) {
                                writeHeaderLabel(field, coll);
                            }
                            writeCell(flattenValue.toString(), row, coll);

                        } catch (NoSuchMethodException e) {
                            // do nothing
                        }

                        break;
                    case FieldType.OCDS_OBJECT_FIELD:
                        if (field.getType().equals(java.util.Set.class)
                                || field.getType().equals(java.util.List.class)) {
                            LOGGER.error("Unsupported operation for field: '" + field.getName()
                                    + "'! You can not flatten an array of objects that contains other array of objects "
                                    + "that need to be printed in other sheet.");
                        } else {
                            final List<Object> newObjects = new ArrayList();
                            for (Object obj : objects) {
                                if (obj != null && PropertyUtils.getProperty(obj, field.getName()) != null
                                        && PropertyUtils.isReadable(obj, field.getName())) {
                                    newObjects.add(PropertyUtils.getProperty(obj, field.getName()));
                                }

                            }

                            final OCDSObjectExcelSheet objectSheet = new OCDSObjectExcelSheet(
                                    this.workbook,
                                    OCDSObjectUtil.getFieldClass(field),
                                    excelSheetName,
                                    getHeaderPrefix() + field.getName());
                            objectSheet.writeRowFlattenObject(newObjects, row);
                        }

                        break;
                    case FieldType.OCDS_OBJECT_SEPARETE_SHEET_FIELD:
                        LOGGER.error("Unsupported operation for field: '" + field.getName()
                                + "'! You can not flatten an array of objects that contains objects "
                                + "that need to be printed in other sheet.");
                        break;
                    default:
                        LOGGER.error("Undefined field type");
                        break;
                }
            }  catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * Write the objects and return the first row index.
     * Used to create a document link between sheets.
     *
     * @param objects
     * @return
     */
    private int writeSheetGetLink(final List<Object> objects) {
        if (objects == null || objects.isEmpty()) {
            return -1;
        }

        // check if this is first time when we created this sheet and skip header row
        // also add 2 since the getLastRowNum() function is 0-based and Excel is 1-based
        int lastRow = excelSheet.getLastRowNum() == 0 ? 2 : (excelSheet.getLastRowNum() + 2);
        this.writeSheet(objects);

        return lastRow;
    }

    @Override
    public void writeSheet(final List<Object> objects) {
        for (Object obj : objects) {

            if (obj != null) {
                int lastRow = excelSheet.getLastRowNum();
                Row row = createRow(excelSheet, ++lastRow);

                writeRow(obj, row);
            }
        }
    }

    private String getHeaderPrefix() {
        if (StringUtils.isEmpty(headerPrefix)) {
            return "";
        } else {
            return headerPrefix + "/";
        }
    }

    /**
     * Functions that check if the header cell is empty, if yes then add the field label
     */
    private void writeHeaderLabel(final Field field, final int coll) {
        final Row headerRow = excelSheet.getRow(0);
        final Cell headerCell = headerRow.getCell(coll);

        if (headerCell == null) {
            writeCell(getHeaderPrefix() + field.getName(), headerRow, coll);
        }
    }

    /**
     * Functions that check if the header cell is empty, if yes then add the label
     */
    private void writeHeaderLabel(final String label, final int coll) {
        final Row headerRow = excelSheet.getRow(0);
        final Cell headerCell = headerRow.getCell(coll);

        if (headerCell == null) {
            writeCell(label, headerRow, coll);
        }
    }

    /**
     * Print an error message in case we have an empty sheet
     */
    public void emptySheet() {
        final Row row;
        if (excelSheet.getRow(0) == null) {
            row = createRow(excelSheet, 0);
        } else {
            row = excelSheet.getRow(0);
        }
        writeCell("No objects returned.", row, 0);
        excelSheet.autoSizeColumn(0);
    }

    private String getExcelSheetName() {
        return excelSheetName;
    }
}
