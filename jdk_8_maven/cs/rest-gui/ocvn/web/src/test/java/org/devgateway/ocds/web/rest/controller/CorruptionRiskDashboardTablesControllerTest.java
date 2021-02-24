package org.devgateway.ocds.web.rest.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.DBObject;
import org.devgateway.ocds.persistence.mongo.repository.main.FlaggedReleaseRepository;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.spring.ReleaseFlaggingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author mpostelnicu
 * @see {@link AbstractEndPointControllerTest}
 * @since 01/13/2017
 */
public class CorruptionRiskDashboardTablesControllerTest extends AbstractEndPointControllerTest {

    @Autowired
    private CorruptionRiskDashboardTablesController corruptionRiskDashboardTablesController;

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
    public void corruptionRiskOverviewTableTest() throws Exception {
        final List<DBObject> result = corruptionRiskDashboardTablesController
                .corruptionRiskOverviewTable(new YearFilterPagingRequest());
        Assert.assertEquals(1, result.size());
        Assert.assertEquals("[ocds-endpoint-001] procuringEntity name",
                ((DBObject)result.get(0).get("procuringEntity")).get("name"));
    }
}
