package org.devgateway.ocds.web.rest.controller.test;

import com.mongodb.DBObject;
import java.io.IOException;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.ExcelImportService;
import org.devgateway.ocds.web.rest.controller.AverageNumberOfTenderersController;
import org.devgateway.ocds.web.rest.controller.AverageTenderAndAwardPeriodsController;
import org.devgateway.ocds.web.rest.controller.CostEffectivenessVisualsController;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.selector.ProcuringEntitySearchController;
import org.devgateway.ocvn.persistence.mongo.dao.ImportFileTypes;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;

/**
 * @author mpostelnicu
 *
 */
public class VNImportAndEndpointsTest extends AbstractWebTest {

    public static final String PROTOTYPE_DB_TEST_FILE="/testImport/test_egp_Jun21_Import.xlsx";
    public static final String LOCATION_TEST_FILE="/testImport/test_Location_Table_Geocoded.xlsx";
    public static final String ORGS_TEST_FILE="/testImport/test_UM_PUBINSTITU_SUPPLIERS_DQA.xlsx";
    public static final String CITY_DEPT_GROUP_TEST_FILE="/testImport/test_city_department_group.xlsx";

    @Autowired
    private ExcelImportService vnExcelImportService;

    @Autowired
    private CostEffectivenessVisualsController costEffectivenessVisualsController;

    @Autowired
    private AverageNumberOfTenderersController averageNumberOfTenderersController;

    @Autowired
    private AverageTenderAndAwardPeriodsController averageTenderAndAwardPeriodsController;

    @Autowired
    private ProcuringEntitySearchController procuringEntitySearchController;

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private CacheManager cacheManager;

    public byte[] loadResourceStreamAsByteArray(String name) throws IOException {
        return IOUtils.toByteArray(getClass().getResourceAsStream(name));
    }

    @Before
    public void importTestData() throws IOException, InterruptedException {
        releaseRepository.deleteAll();

        // clean the cache (we need this especially for endpoints cache)
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(c -> cacheManager.getCache(c).clear());
        }

        vnExcelImportService.importAllSheets(ImportFileTypes.ALL_FILE_TYPES,
                loadResourceStreamAsByteArray(PROTOTYPE_DB_TEST_FILE),
                loadResourceStreamAsByteArray(LOCATION_TEST_FILE),
                loadResourceStreamAsByteArray(ORGS_TEST_FILE),
                loadResourceStreamAsByteArray(CITY_DEPT_GROUP_TEST_FILE),
                true, false, false);
    }

    @After
    public void tearDown() {
        // be sure to clean up the release collection
        releaseRepository.deleteAll();
    }

    @Test
    public void testCostEffectivenessAwardAmount() {
        List<DBObject> costEffectivenessAwardAmount = costEffectivenessVisualsController
                .costEffectivenessAwardAmount(new YearFilterPagingRequest());
        DBObject root = costEffectivenessAwardAmount.get(0);
        int year = (int) root.get(CostEffectivenessVisualsController.Keys.YEAR);
        Assert.assertEquals(2012, year);

        double totalAwardAmount = (double) root.get("totalAwardAmount");
        Assert.assertEquals(1000, totalAwardAmount, 0);

    }

    @Test
    public void testCostEffectivenessTenderAmount() {
        List<DBObject> costEffectivenessTenderAmount = costEffectivenessVisualsController
                .costEffectivenessTenderAmount(new GroupingFilterPagingRequest());
        DBObject root = costEffectivenessTenderAmount.get(0);
        int year = (int) root.get(CostEffectivenessVisualsController.Keys.YEAR);
        Assert.assertEquals(2012, year);

        double totalAwardAmount = (double) root.get("totalTenderAmount");
        Assert.assertEquals(1000, totalAwardAmount, 0);

    }

    @Test
    public void testAverageNumberOfTenderersController() {
        List<DBObject> averageNumberOfTenderers = averageNumberOfTenderersController.
                averageNumberOfTenderers(new YearFilterPagingRequest());

        DBObject root = averageNumberOfTenderers.get(0);
        int year = (int) root.get("year");
        Assert.assertEquals(2012, year);

        double averageNoTenderers = (double) root.get("averageNoTenderers");
        Assert.assertEquals(2, averageNoTenderers, 0);
        

        root = averageNumberOfTenderers.get(1);
        year = (int) root.get("year");
        Assert.assertEquals(2013, year);

        averageNoTenderers = (double) root.get("averageNoTenderers");
        Assert.assertEquals(2, averageNoTenderers, 0);
    }

    @Test
    public void testAverageAwardPeriod() {
        List<DBObject> averageAwardPeriod = averageTenderAndAwardPeriodsController
                .averageAwardPeriod(new YearFilterPagingRequest());

        DBObject root = averageAwardPeriod.get(0);
        int year = (int) root.get(AverageTenderAndAwardPeriodsController.Keys.YEAR);
        Assert.assertEquals(2014, year);

        double n = (double) root.get("averageAwardDays");
        Assert.assertEquals(536, n, 0);
    }

    @Test
    public void testAverageTenderPeriod() {
        List<DBObject> averageTenderPeriod = averageTenderAndAwardPeriodsController
                .averageTenderPeriod(new YearFilterPagingRequest());

        DBObject root = averageTenderPeriod.get(0);
        int year = (int) root.get(AverageTenderAndAwardPeriodsController.Keys.YEAR);
        Assert.assertEquals(2012, year);

        double n = (double) root.get("averageTenderDays");
        Assert.assertEquals(15, n, 0);

        root = averageTenderPeriod.get(1);
        year = (int) root.get(AverageTenderAndAwardPeriodsController.Keys.YEAR);
        Assert.assertEquals(2013, year);

        n = (double) root.get("averageTenderDays");
        Assert.assertEquals(15, n, 0);
    }

    @Test
    public void testQualityAverageTenderPeriod() {
        List<DBObject> qAverageTenderPeriod = averageTenderAndAwardPeriodsController
                .qualityAverageTenderPeriod(new DefaultFilterPagingRequest());

        DBObject root = qAverageTenderPeriod.get(0);

        int totalTenderWithStartEndDates = (int) root.get("totalTenderWithStartEndDates");
        Assert.assertEquals(2, totalTenderWithStartEndDates);

        int totalTenders = (int) root.get("totalTenders");
        Assert.assertEquals(2, totalTenders);

        double percentageTenderWithStartEndDates = (double) root.get("percentageTenderWithStartEndDates");
        Assert.assertEquals(100, percentageTenderWithStartEndDates, 0);
    }

    @Test
    public void testQualityAverageAwardPeriod() {
        List<DBObject> qAverageTenderPeriod = averageTenderAndAwardPeriodsController
                .qualityAverageAwardPeriod(new DefaultFilterPagingRequest());

        DBObject root = qAverageTenderPeriod.get(0);

        int totalAwardWithStartEndDates = (int) root.get("totalAwardWithStartEndDates");
        Assert.assertEquals(4, totalAwardWithStartEndDates);

        int totalAwards = (int) root.get("totalAwards");
        Assert.assertEquals(4, totalAwards);

        double percentageAwardWithStartEndDates = (double) root.get("percentageAwardWithStartEndDates");
        Assert.assertEquals(100, percentageAwardWithStartEndDates, 0);
    }


    @Test
    public void testProcuringEntitySearchController() {
        List<Organization> procuringEntities = procuringEntitySearchController.searchText(
                new TextSearchRequest());
        Assert.assertEquals(3, procuringEntities.size(), 0);
    }

}
