package org.devgateway.ocds.web.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.repository.main.FlaggedReleaseRepository;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.spring.ReleaseFlaggingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author mpostelnicu
 * @see {@link AbstractEndPointControllerTest}
 * @since 01/13/2017
 */
public class CorruptionRiskDashboardIndicatorsStatsControllerTest extends AbstractEndPointControllerTest {

    @Autowired
    private CorruptionRiskDashboardIndicatorsStatsController corruptionRiskDashboardIndicatorsStatsController;

    @Autowired
    private ReleaseFlaggingService releaseFlaggingService;

    @Autowired
    private FlaggedReleaseRepository releaseRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public static void logMessage(String message) {
        logger.info(message);
    }

    @Before
    public final void setUp() throws Exception {
        super.setUp();
        releaseFlaggingService.processAndSaveFlagsForAllReleases(ReleaseFlaggingServiceTest::logMessage);


//        //debugging
//        releaseRepository.findAll().forEach(r -> {
//            try {
//                System.out.println("date=" + r.getTender().getTenderPeriod().getStartDate() + " " +
//                        objectMapper.writeValueAsString(r.getFlags()));
//            } catch (JsonProcessingException e) {
//                e.printStackTrace();
//            }
//        });
    }

    @Test
    public void totalFlagsByIndicatorTypeTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .totalFlaggedIndicatorsByIndicatorType(new YearFilterPagingRequest());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.TYPE));
        Assert.assertEquals(2, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.INDICATOR_COUNT));
    }

    @Test
    public void totalEligibleIndicatorsByIndicatorTypeTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .totalEligibleIndicatorsByIndicatorType(new YearFilterPagingRequest());
        Assert.assertEquals(3, result.size());
        Assert.assertEquals(FlagType.COLLUSION.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(2, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .INDICATOR_COUNT));

        Assert.assertEquals(FlagType.FRAUD.toString(), result.get(1).get
                (CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(3, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .INDICATOR_COUNT));

        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(2).get
                (CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(7, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .INDICATOR_COUNT));
    }

    @Test
    public void totalFlaggedIndicatorsByIndicatorTypeByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .totalFlaggedIndicatorsByIndicatorTypeByYear(new YearFilterPagingRequest());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.TYPE));
        Assert.assertEquals(2, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.INDICATOR_COUNT));
        Assert.assertEquals(2015, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
    }

    @Test
    public void totalEligibleIndicatorsByIndicatorTypeByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .totalEligibleIndicatorsByIndicatorTypeByYear(new YearFilterPagingRequest());
        Assert.assertEquals(6, result.size());
        Assert.assertEquals(FlagType.COLLUSION.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(2014, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .INDICATOR_COUNT));

        Assert.assertEquals(FlagType.FRAUD.toString(), result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(2014, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.INDICATOR_COUNT));
    }

    @Test
    public void totalFlaggedProjectsByIndicatorTypeByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .totalFlaggedProjectsByIndicatorTypeByYear(new YearFilterPagingRequest());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.TYPE));
        Assert.assertEquals(2015, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(2, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.FLAGGED_COUNT));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.FLAGGED_PROJECT_COUNT));
    }

    @Test
    public void totalEligibleProjectsByIndicatorTypeByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .totalEligibleProjectsByIndicatorTypeByYear(new YearFilterPagingRequest());
        Assert.assertEquals(6, result.size());

        Assert.assertEquals(FlagType.COLLUSION.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(2014, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_PROJECT_COUNT));

        Assert.assertEquals(FlagType.FRAUD.toString(), result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(2014, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.ELIGIBLE_PROJECT_COUNT));

        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(2).get
                (CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(2014, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(1, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_PROJECT_COUNT));

        Assert.assertEquals(FlagType.COLLUSION.toString(), result.get(3).get
                (CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(2015, result.get(3).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(1, result.get(3).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(3).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_PROJECT_COUNT));
    }

    @Test
    public void totalProjectsByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .totalProjectsByYear(new YearFilterPagingRequest());
        Assert.assertEquals(2, result.size());

        Assert.assertEquals(2014, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PROJECT_COUNT));

        Assert.assertEquals(2015, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(2, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PROJECT_COUNT));
    }


    @Test
    public void percentTotalProjectsFlaggedByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .percentTotalProjectsFlaggedByYear(new YearFilterPagingRequest());
        Assert.assertEquals(1, result.size());

        Assert.assertEquals(2015, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.TYPE));
        Assert.assertEquals(2, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.FLAGGED_COUNT));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.FLAGGED_PROJECT_COUNT));
        Assert.assertEquals(2, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PROJECT_COUNT));
        Assert.assertTrue(BigDecimal.valueOf(50).
                compareTo((BigDecimal)result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PERCENT))==0);
    }


    @Test
    public void percentTotalProjectsEligibleByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .percentTotalProjectsEligibleByYear(new YearFilterPagingRequest());
        Assert.assertEquals(6, result.size());

        Assert.assertEquals(2014, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(FlagType.COLLUSION.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_PROJECT_COUNT));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PROJECT_COUNT));
        Assert.assertTrue(BigDecimal.valueOf(100).
                compareTo((BigDecimal)result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PERCENT))==0);

        Assert.assertEquals(2014, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(FlagType.FRAUD.toString(), result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.ELIGIBLE_PROJECT_COUNT));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PROJECT_COUNT));
        Assert.assertTrue(BigDecimal.valueOf(100).
                compareTo((BigDecimal)result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PERCENT))==0);

        Assert.assertEquals(2014, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(FlagType.RIGGING.toString(), result.get(2).get
                (CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(1, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_PROJECT_COUNT));
        Assert.assertEquals(1, result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PROJECT_COUNT));
        Assert.assertTrue(BigDecimal.valueOf(100).
                compareTo((BigDecimal)result.get(2).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                        .PERCENT))==0);
    }

    @Test
    public void percentOfEligibleProjectsFlaggedByYearTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController
                .percentOfEligibleProjectsFlaggedByYear(new YearFilterPagingRequest());
        Assert.assertEquals(6, result.size());

        Assert.assertEquals(2014, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(FlagType.COLLUSION.toString(), result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .ELIGIBLE_PROJECT_COUNT));
        Assert.assertEquals(0, result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys
                .FLAGGED_PROJECT_COUNT));
        Assert.assertTrue(BigDecimal.valueOf(0).
                compareTo((BigDecimal)result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PERCENT))==0);

        Assert.assertEquals(2014, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.YEAR));
        Assert.assertEquals(FlagType.FRAUD.toString(), result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController
                .Keys.TYPE));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.ELIGIBLE_COUNT));
        Assert.assertEquals(1, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.ELIGIBLE_PROJECT_COUNT));
        Assert.assertEquals(0, result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.FLAGGED_PROJECT_COUNT));
        Assert.assertTrue(BigDecimal.ZERO.
                compareTo((BigDecimal)result.get(1).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.PERCENT))==0);
    }

    @Test
    public void totalFlagsTest() throws Exception {

        final List<DBObject> result = corruptionRiskDashboardIndicatorsStatsController.totalFlags(new
                YearFilterPagingRequest());

        Assert.assertEquals(2,
                result.get(0).get(CorruptionRiskDashboardIndicatorsStatsController.Keys.FLAGGED_COUNT));
    }


}
