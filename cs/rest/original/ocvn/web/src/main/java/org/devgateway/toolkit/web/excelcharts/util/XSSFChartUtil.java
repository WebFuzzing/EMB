package org.devgateway.toolkit.web.excelcharts.util;

import org.apache.poi.ss.usermodel.charts.ChartDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumVal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrData;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrVal;

/**
 * @author idobre
 * @since 8/8/16
 *
 *        Package private class with utility methods. It's based on
 *        org.apache.poi.xssf.usermodel.charts.XSSFChartUtil class
 */
public final class XSSFChartUtil {
    private XSSFChartUtil() {

    }

    /**
     * Builds CTAxDataSource object content from POI ChartDataSource.
     * 
     * @param ctAxDataSource
     *            OOXML data source to build
     * @param dataSource
     *            POI data source to use
     */
    public static void buildAxDataSource(final CTAxDataSource ctAxDataSource, final ChartDataSource<?> dataSource) {
        if (dataSource.isNumeric()) {
            if (dataSource.isReference()) {
                buildNumRef(ctAxDataSource.addNewNumRef(), dataSource);
            } else {
                buildNumLit(ctAxDataSource.addNewNumLit(), dataSource);
            }
        } else {
            if (dataSource.isReference()) {
                buildStrRef(ctAxDataSource.addNewStrRef(), dataSource);
            } else {
                buildStrLit(ctAxDataSource.addNewStrLit(), dataSource);
            }
        }
    }

    /**
     * Builds CTNumDataSource object content from POI ChartDataSource
     * 
     * @param ctNumDataSource
     *            OOXML data source to build
     * @param dataSource
     *            POI data source to use
     */
    public static void buildNumDataSource(final CTNumDataSource ctNumDataSource,
            final ChartDataSource<? extends Number> dataSource) {
        if (dataSource.isReference()) {
            buildNumRef(ctNumDataSource.addNewNumRef(), dataSource);
        } else {
            buildNumLit(ctNumDataSource.addNewNumLit(), dataSource);
        }
    }

    private static void buildNumRef(final CTNumRef ctNumRef, final ChartDataSource<?> dataSource) {
        ctNumRef.setF(dataSource.getFormulaString());
        CTNumData cache = ctNumRef.addNewNumCache();
        fillNumCache(cache, dataSource);
    }

    private static void buildNumLit(final CTNumData ctNumData, final ChartDataSource<?> dataSource) {
        fillNumCache(ctNumData, dataSource);
    }

    private static void buildStrRef(final CTStrRef ctStrRef, final ChartDataSource<?> dataSource) {
        ctStrRef.setF(dataSource.getFormulaString());
        CTStrData cache = ctStrRef.addNewStrCache();
        fillStringCache(cache, dataSource);
    }

    private static void buildStrLit(final CTStrData ctStrData, final ChartDataSource<?> dataSource) {
        fillStringCache(ctStrData, dataSource);
    }

    private static void fillStringCache(final CTStrData cache, final ChartDataSource<?> dataSource) {
        int numOfPoints = dataSource.getPointCount();
        cache.addNewPtCount().setVal(numOfPoints);
        for (int i = 0; i < numOfPoints; ++i) {
            Object value = dataSource.getPointAt(i);
            if (value != null) {
                CTStrVal ctStrVal = cache.addNewPt();
                ctStrVal.setIdx(i);
                ctStrVal.setV(value.toString());
            }
        }
    }

    private static void fillNumCache(final CTNumData cache, final ChartDataSource<?> dataSource) {
        int numOfPoints = dataSource.getPointCount();
        cache.addNewPtCount().setVal(numOfPoints);
        for (int i = 0; i < numOfPoints; ++i) {
            Number value = (Number) dataSource.getPointAt(i);
            if (value != null) {
                CTNumVal ctNumVal = cache.addNewPt();
                ctNumVal.setIdx(i);
                ctNumVal.setV(value.toString());
            }
        }
    }
}
