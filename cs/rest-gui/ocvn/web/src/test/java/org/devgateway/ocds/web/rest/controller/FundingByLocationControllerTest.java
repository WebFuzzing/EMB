package org.devgateway.ocds.web.rest.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class FundingByLocationControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private FundingByLocationController fundingByLocationController;

    @Test
    public void fundingByTenderDeliveryLocation() throws Exception {
        final List<DBObject> fundingByTenderDeliveryLocation = fundingByLocationController
                .fundingByTenderDeliveryLocation(new YearFilterPagingRequest());

        final DBObject first = fundingByTenderDeliveryLocation.get(0);
        int year = (int) first.get(FundingByLocationController.Keys.YEAR);
        BasicDBObject deliveryLocation = (BasicDBObject) first
                .get(FundingByLocationController.Keys.ITEMS_DELIVERY_LOCATION);
        BasicDBObject geometry = (BasicDBObject) deliveryLocation.get("geometry");
        String geometryType = (String) geometry.get("type");
        List<Double> coordinates = (List<Double>) geometry.get("coordinates");
        double totalTendersAmount = (double) first.get(FundingByLocationController.Keys.TOTAL_TENDERS_AMOUNT);
        int tendersCount = (int) first.get(FundingByLocationController.Keys.TENDERS_COUNT);

        Assert.assertEquals(2015, year);
        Assert.assertEquals("Point", geometryType);
        Assert.assertEquals(new ArrayList<>(Arrays.asList(45.9432, 24.9668)), coordinates);
        Assert.assertEquals(9000.0, totalTendersAmount, 0);
        Assert.assertEquals(1, tendersCount);
    }

    @Test
    public void qualityFundingByTenderDeliveryLocation() throws Exception {
        final List<DBObject> qualityFundingByTenderDeliveryLocation = fundingByLocationController
                .qualityFundingByTenderDeliveryLocation(new YearFilterPagingRequest());

        final DBObject first = qualityFundingByTenderDeliveryLocation.get(0);
        int totalTendersWithStartDate = (int) first.get(FundingByLocationController.Keys.TOTAL_TENDERS_WITH_START_DATE);
        int totalTendersWithStartDateAndLocation = (int) first
                .get(FundingByLocationController.Keys.TOTAL_TENDERS_WITH_START_DATE_AND_LOCATION);
        double percentTendersWithStartDateAndLocation = (double) first
                .get(FundingByLocationController.Keys.PERCENT_TENDERS_WITH_START_DATE_AND_LOCATION);

        Assert.assertEquals(3, totalTendersWithStartDate);
        Assert.assertEquals(1, totalTendersWithStartDateAndLocation);
        Assert.assertEquals(33.33, percentTendersWithStartDateAndLocation, 1E-2);
    }

    @Test
    public void plannedFundingByLocation() throws Exception {
        final List<DBObject> plannedFundingByLocation = fundingByLocationController
                .plannedFundingByLocation(new YearFilterPagingRequest());

        final DBObject first = plannedFundingByLocation.get(0);
        int year = (int) first.get(FundingByLocationController.Keys.YEAR);
        BasicDBObject projectLocation = (BasicDBObject) first
                .get(FundingByLocationController.Keys.PLANNING_BUDGET_PROJECTLOCATION);
        BasicDBObject geometry = (BasicDBObject) projectLocation.get("geometry");
        String geometryType = (String) geometry.get("type");
        List<Double> coordinates = (List<Double>) geometry.get("coordinates");
        double totalPlannedAmount = (double) first.get(FundingByLocationController.Keys.TOTAL_PLANNED_AMOUNT);

        Assert.assertEquals(2016, year);
        Assert.assertEquals("Point", geometryType);
        Assert.assertEquals(new ArrayList<>(Arrays.asList(47.411, 28.36)), coordinates);
        Assert.assertEquals(1000.0, totalPlannedAmount, 0);
    }

    @Test
    public void qualityPlannedFundingByLocation() throws Exception {
        final List<DBObject> qualityPlannedFundingByLocation = fundingByLocationController
                .qualityPlannedFundingByLocation(new YearFilterPagingRequest());

        final DBObject first = qualityPlannedFundingByLocation.get(0);
        int totalPlansWithAmounts = (int) first.get(FundingByLocationController.Keys.TOTAL_PLANS_WITH_AMOUNTS);
        int totalPlansWithAmountsAndLocation = (int) first
                .get(FundingByLocationController.Keys.TOTAL_PLANS_WITH_AMOUNTS_AND_LOCATION);
        double percentPlansWithAmountsAndLocation = (double) first
                .get(FundingByLocationController.Keys.PERCENT_PLANS_WITH_AMOUNTS_AND_LOCATION);

        Assert.assertEquals(3, totalPlansWithAmounts);
        Assert.assertEquals(1, totalPlansWithAmountsAndLocation);
        Assert.assertEquals(33.33, percentPlansWithAmountsAndLocation, 1E-2);
    }
}
