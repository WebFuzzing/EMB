package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TenderPercentagesControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private TenderPercentagesController tenderPercentagesController;

    @Test
    public void percentTendersCancelled() throws Exception {
        final List<DBObject> percentTendersCancelled = tenderPercentagesController
                .percentTendersCancelled(new YearFilterPagingRequest());

        final DBObject first = percentTendersCancelled.get(0);
        int year = (int) first.get(TenderPercentagesController.Keys.YEAR);
        int totalTenders = (int) first.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        int totalCancelled = (int) first.get(TenderPercentagesController.Keys.TOTAL_CANCELLED);
        double percentCancelled = (double) first.get(TenderPercentagesController.Keys.PERCENT_CANCELLED);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(0, totalCancelled);
        Assert.assertEquals(0.0, percentCancelled, 0);

        final DBObject second = percentTendersCancelled.get(1);
        year = (int) second.get(TenderPercentagesController.Keys.YEAR);
        totalTenders = (int) second.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        totalCancelled = (int) second.get(TenderPercentagesController.Keys.TOTAL_CANCELLED);
        percentCancelled = (double) second.get(TenderPercentagesController.Keys.PERCENT_CANCELLED);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(2, totalTenders);
        Assert.assertEquals(0, totalCancelled);
        Assert.assertEquals(0.0, percentCancelled, 0);
    }

    @Test
    public void percentTendersWithTwoOrMoreTenderers() throws Exception {
        final List<DBObject> percentTendersWithTwoOrMoreTenderers = tenderPercentagesController
                .percentTendersWithTwoOrMoreTenderers(new YearFilterPagingRequest());

        final DBObject first = percentTendersWithTwoOrMoreTenderers.get(0);
        int year = (int) first.get(TenderPercentagesController.Keys.YEAR);
        int totalTenders = (int) first.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        int totalTendersWithTwoOrMoreTenderers = (int) first
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS);
        double percentTenders = (double) first.get(TenderPercentagesController.Keys.PERCENT_TENDERS);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(1, totalTendersWithTwoOrMoreTenderers);
        Assert.assertEquals(100.0, percentTenders, 0);

        final DBObject second = percentTendersWithTwoOrMoreTenderers.get(1);
        year = (int) second.get(TenderPercentagesController.Keys.YEAR);
        totalTenders = (int) second.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        totalTendersWithTwoOrMoreTenderers = (int) second
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS);
        percentTenders = (double) second.get(TenderPercentagesController.Keys.PERCENT_TENDERS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(2, totalTenders);
        Assert.assertEquals(1, totalTendersWithTwoOrMoreTenderers);
        Assert.assertEquals(50.0, percentTenders, 0);
    }

    @Test
    public void percentTendersAwarded() throws Exception {
        final List<DBObject> percentTendersAwarded = tenderPercentagesController
                .percentTendersAwarded(new YearFilterPagingRequest());

        final DBObject first = percentTendersAwarded.get(0);
        int year = (int) first.get(TenderPercentagesController.Keys.YEAR);
        int totalTendersWithOneOrMoreTenderers = (int) first
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_ONE_OR_MORE_TENDERERS);
        int totalTendersWithTwoOrMoreTenderers = (int) first
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS);
        double percentTenders = (double) first.get(TenderPercentagesController.Keys.PERCENT_TENDERS);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(1, totalTendersWithOneOrMoreTenderers);
        Assert.assertEquals(1, totalTendersWithTwoOrMoreTenderers);
        Assert.assertEquals(100.0, percentTenders, 0);

        final DBObject second = percentTendersAwarded.get(1);
        year = (int) second.get(TenderPercentagesController.Keys.YEAR);
        totalTendersWithOneOrMoreTenderers = (int) second
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_ONE_OR_MORE_TENDERERS);
        totalTendersWithTwoOrMoreTenderers = (int) second
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_TWO_OR_MORE_TENDERERS);
        percentTenders = (double) second.get(TenderPercentagesController.Keys.PERCENT_TENDERS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(2, totalTendersWithOneOrMoreTenderers);
        Assert.assertEquals(1, totalTendersWithTwoOrMoreTenderers);
        Assert.assertEquals(50.0, percentTenders, 0);
    }

    @Test
    public void percentTendersUsingEBid() throws Exception {
        final List<DBObject> percentTendersUsingEBid = tenderPercentagesController
                .percentTendersUsingEBid(new YearFilterPagingRequest());

        final DBObject first = percentTendersUsingEBid.get(0);
        int year = (int) first.get(TenderPercentagesController.Keys.YEAR);
        int totalTenders = (int) first.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        int totalTendersUsingEbid = (int) first.get(TenderPercentagesController.Keys.TOTAL_TENDERS_USING_EBID);
        double percentageTendersUsingEbid = (double) first
                .get(TenderPercentagesController.Keys.PERCENTAGE_TENDERS_USING_EBID);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(1, totalTendersUsingEbid);
        Assert.assertEquals(100.0, percentageTendersUsingEbid, 0);

        final DBObject second = percentTendersUsingEBid.get(1);
        year = (int) second.get(TenderPercentagesController.Keys.YEAR);
        totalTenders = (int) second.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        totalTendersUsingEbid = (int) second.get(TenderPercentagesController.Keys.TOTAL_TENDERS_USING_EBID);
        percentageTendersUsingEbid = (double) second
                .get(TenderPercentagesController.Keys.PERCENTAGE_TENDERS_USING_EBID);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(1, totalTendersUsingEbid);
        Assert.assertEquals(100.0, percentageTendersUsingEbid, 0);
    }

    @Test
    public void percentTendersWithLinkedProcurementPlan() throws Exception {
        final List<DBObject> percentTendersWithLinkedProcurementPlan = tenderPercentagesController
                .percentTendersWithLinkedProcurementPlan(new YearFilterPagingRequest());

        final DBObject first = percentTendersWithLinkedProcurementPlan.get(0);
        int year = (int) first.get(TenderPercentagesController.Keys.YEAR);
        int totalTendersWithLinkedProcurementPlan = (int) first
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN);
        int totalTenders = (int) first
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        double percentTenders = (double) first.get(TenderPercentagesController.Keys.PERCENT_TENDERS);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(1, totalTendersWithLinkedProcurementPlan);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(100.0, percentTenders, 0);

        final DBObject second = percentTendersWithLinkedProcurementPlan.get(1);
        year = (int) second.get(TenderPercentagesController.Keys.YEAR);
        totalTendersWithLinkedProcurementPlan = (int) second
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS_WITH_LINKED_PROCUREMENT_PLAN);
        totalTenders = (int) second
                .get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        percentTenders = (double) second.get(TenderPercentagesController.Keys.PERCENT_TENDERS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(2, totalTendersWithLinkedProcurementPlan);
        Assert.assertEquals(2, totalTenders);
        Assert.assertEquals(100.0, percentTenders, 0);
    }


    @Test
    public void percentTendersUsingEgp() throws Exception {
        final List<DBObject> percentTendersUsingEgp = tenderPercentagesController
                .percentTendersUsingEgp(new YearFilterPagingRequest());

        final DBObject first = percentTendersUsingEgp.get(0);
        int year = (int) first.get(TenderPercentagesController.Keys.YEAR);
        int totalTenders = (int) first.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        int totalEgp = (int) first.get(TenderPercentagesController.Keys.TOTAL_EGP);
        double percentEgp = (double) first.get(TenderPercentagesController.Keys.PERCENTAGE_EGP);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(0, totalEgp);
        Assert.assertEquals(0.0, percentEgp, 0);

        final DBObject second = percentTendersUsingEgp.get(1);
        year = (int) second.get(TenderPercentagesController.Keys.YEAR);
        totalTenders = (int) second.get(TenderPercentagesController.Keys.TOTAL_TENDERS);
        totalEgp = (int) second.get(TenderPercentagesController.Keys.TOTAL_EGP);
        percentEgp = (double) second.get(TenderPercentagesController.Keys.PERCENTAGE_EGP);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(2, totalTenders);
        Assert.assertEquals(1, totalEgp);
        Assert.assertEquals(50.0, percentEgp, 0);
    }

    @Test
    public void avgTimeFromPlanToTenderPhase() throws Exception {
        final List<DBObject> avgTimeFromPlanToTenderPhase = tenderPercentagesController
                .avgTimeFromPlanToTenderPhase(new YearFilterPagingRequest());

        final DBObject first = avgTimeFromPlanToTenderPhase.get(0);
        int year = (int) first.get(TenderPercentagesController.Keys.YEAR);
        double avgTimeFromPlanToTender = (double) first
                .get(TenderPercentagesController.Keys.AVG_TIME_FROM_PLAN_TO_TENDER_PHASE);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(-315.00, avgTimeFromPlanToTender, 0);
    }
}
