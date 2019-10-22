/**
 *
 */
package org.devgateway.ocds.web.rest.controller.stats.crosstab;

import com.mongodb.DBObject;
import org.devgateway.ocds.persistence.mongo.flags.FlagsConstants;
import org.devgateway.ocds.web.rest.controller.AbstractEndPointControllerTest;
import org.devgateway.ocds.web.rest.controller.flags.crosstab.FlagI007CrosstabController;
import org.devgateway.ocds.web.rest.controller.flags.crosstab.FlagI019CrosstabController;
import org.devgateway.ocds.web.rest.controller.flags.crosstab.FlagI038CrosstabController;
import org.devgateway.ocds.web.rest.controller.flags.crosstab.FlagI077CrosstabController;
import org.devgateway.ocds.web.rest.controller.flags.crosstab.FlagI180CrosstabController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.spring.ReleaseFlaggingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author mpostelnicu
 */
public class CorruptionRiskDashboardCrosstabTest extends AbstractEndPointControllerTest {

    @Autowired
    private ReleaseFlaggingService releaseFlaggingService;

    @Autowired
    private FlagI007CrosstabController flagI007CrosstabController;

    @Autowired
    private FlagI019CrosstabController flagI019CrosstabController;

    @Autowired
    private FlagI038CrosstabController flagI038CrosstabController;

    @Autowired
    private FlagI077CrosstabController flagI077CrosstabController;

    @Autowired
    private FlagI180CrosstabController flagI180CrosstabController;

    public static void logMessage(String message) {
        logger.info(message);
    }

    @Before
    public final void setUp() throws Exception {
        super.setUp();
        releaseFlaggingService.processAndSaveFlagsForAllReleases(CorruptionRiskDashboardCrosstabTest::logMessage);
    }


    @Test
    public void testI007CrossTab() {
        List<DBObject> flagStats = flagI007CrosstabController.flagStats(new YearFilterPagingRequest());
        Assert.assertEquals(1,
                flagStats.get(0).get(flagI007CrosstabController.
                        getFlagDesignation(FlagsConstants.I007_VALUE)));

        Assert.assertEquals(1,
                flagStats.get(0).get(flagI007CrosstabController.
                        getFlagDesignation(FlagsConstants.I019_VALUE)));
    }

    @Test
    public void testI019CrossTab() {
        List<DBObject> flagStats = flagI019CrosstabController.flagStats(new YearFilterPagingRequest());
        Assert.assertEquals(1,
                flagStats.get(0).get(flagI019CrosstabController.
                        getFlagDesignation(FlagsConstants.I007_VALUE)));

        Assert.assertEquals(1,
                flagStats.get(0).get(flagI019CrosstabController.
                        getFlagDesignation(FlagsConstants.I019_VALUE)));
    }


    @Test
    public void testI038CrossTab() {
        List<DBObject> flagStats = flagI038CrosstabController.flagStats(new YearFilterPagingRequest());
        Assert.assertEquals(0,flagStats.size());
    }


    @Test
    public void testI077CrossTab() {
        List<DBObject> flagStats = flagI077CrosstabController.flagStats(new YearFilterPagingRequest());
        Assert.assertEquals(0,flagStats.size());
    }


    @Test
    public void test180CrossTab() {
        List<DBObject> flagStats = flagI180CrosstabController.flagStats(new YearFilterPagingRequest());
        Assert.assertEquals(0,flagStats.size());
    }

}
