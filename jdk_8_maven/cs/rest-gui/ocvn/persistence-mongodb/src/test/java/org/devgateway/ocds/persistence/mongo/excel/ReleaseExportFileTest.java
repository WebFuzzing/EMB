package org.devgateway.ocds.persistence.mongo.excel;

import org.apache.poi.ss.usermodel.Workbook;
import org.devgateway.ocds.persistence.mongo.ReleasePackage;
import org.devgateway.ocds.persistence.mongo.spring.json2object.JsonToObject;
import org.devgateway.ocds.persistence.mongo.spring.json2object.ReleasePackageJsonToObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * @author idobre
 * @since 6/7/16
 */
public class ReleaseExportFileTest {
    @Test
    public void createWorkbook() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("json/release-excel-export.json").getFile());
        final JsonToObject releasePackageJsonToObject = new ReleasePackageJsonToObject(file, false);

        final ReleasePackage releasePackage = (ReleasePackage) releasePackageJsonToObject.toObject();

        final ExcelFile releaseExcelFile = new ReleaseExportFile(new ArrayList<>(releasePackage.getReleases()));
        final Workbook workbook = releaseExcelFile.createWorkbook();
        // try (FileOutputStream outputStream = new FileOutputStream("/Users/ionut/Downloads/ocds-export.xlsx")) {
        //     workbook.write(outputStream);
        // }

        Assert.assertEquals("Number of sheets", 6, workbook.getNumberOfSheets());

        Assert.assertNotNull("release sheet", workbook.getSheet("release"));
        Assert.assertNotNull("tender sheet", workbook.getSheet("vntender"));
        Assert.assertNotNull("award sheet", workbook.getSheet("vnaward"));
        Assert.assertNotNull("item sheet", workbook.getSheet("vnitem"));

        Assert.assertEquals("release id", "ocds-213czf-000-00001-01-planning",
                workbook.getSheet("release").getRow(3).getCell(0).toString());
        Assert.assertEquals("release id", "ocds-213czf-000-00001-06-implementation",
                workbook.getSheet("release").getRow(5).getCell(0).toString());

        Assert.assertEquals("tender parent", "release - ocds-213czf-000-00001",
                workbook.getSheet("vntender").getRow(1).getCell(0).toString());
        Assert.assertEquals("tender id", "ocds-213czf-000-00001-01-planning",
                workbook.getSheet("vntender").getRow(1).getCell(1).toString());

        Assert.assertEquals("award number of rows", 4, workbook.getSheet("vnaward").getLastRowNum());

        Assert.assertEquals("item number of rows", 7, workbook.getSheet("vnitem").getLastRowNum());
    }
}
