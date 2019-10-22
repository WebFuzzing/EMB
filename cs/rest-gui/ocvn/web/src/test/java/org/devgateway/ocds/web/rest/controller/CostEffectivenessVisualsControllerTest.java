package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class CostEffectivenessVisualsControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private CostEffectivenessVisualsController costEffectivenessVisualsController;

    @Test
    public void costEffectivenessAwardAmount() throws Exception {
        final List<DBObject> costEffectivenessAwardAmount = costEffectivenessVisualsController
                .costEffectivenessAwardAmount(new YearFilterPagingRequest());

        final DBObject first = costEffectivenessAwardAmount.get(0);
        int year = (int) first.get(CostEffectivenessVisualsController.Keys.YEAR);
        double totalAwardAmount = (double) first.get(CostEffectivenessVisualsController.Keys.TOTAL_AWARD_AMOUNT);
        int totalAwards = (int) first.get(CostEffectivenessVisualsController.Keys.TOTAL_AWARDS);
        int totalAwardsWithTender = (int) first.get(CostEffectivenessVisualsController.Keys.TOTAL_AWARDS_WITH_TENDER);
        double percentageAwardsWithTender = (double) first.
                get(CostEffectivenessVisualsController.Keys.PERCENTAGE_AWARDS_WITH_TENDER);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(6000000.0, totalAwardAmount, 0);
        Assert.assertEquals(1, totalAwards);
        Assert.assertEquals(1, totalAwardsWithTender);
        Assert.assertEquals(100.0, percentageAwardsWithTender, 0);

        final DBObject second = costEffectivenessAwardAmount.get(1);
        year = (int) second.get(CostEffectivenessVisualsController.Keys.YEAR);
        totalAwardAmount = (double) second.get(CostEffectivenessVisualsController.Keys.TOTAL_AWARD_AMOUNT);
        totalAwards = (int) second.get(CostEffectivenessVisualsController.Keys.TOTAL_AWARDS);
        totalAwardsWithTender = (int) second.get(CostEffectivenessVisualsController.Keys.TOTAL_AWARDS_WITH_TENDER);
        percentageAwardsWithTender = (double) second.
                get(CostEffectivenessVisualsController.Keys.PERCENTAGE_AWARDS_WITH_TENDER);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(40000.0, totalAwardAmount, 0);
        Assert.assertEquals(1, totalAwards);
        Assert.assertEquals(1, totalAwardsWithTender);
        Assert.assertEquals(100.0, percentageAwardsWithTender, 0);
    }

    @Test
    public void costEffectivenessTenderAmount() throws Exception {
        final List<DBObject> costEffectivenessTenderAmount = costEffectivenessVisualsController
                .costEffectivenessTenderAmount(new GroupingFilterPagingRequest());

        final DBObject first = costEffectivenessTenderAmount.get(0);
        int year = (int) first.get(CostEffectivenessVisualsController.Keys.YEAR);
        double totalTenderAmount = (double) first.get(CostEffectivenessVisualsController.Keys.TOTAL_TENDER_AMOUNT);
        int totalTenders = (int) first.get(CostEffectivenessVisualsController.Keys.TOTAL_TENDERS);
        int totalTenderWithAwards = (int) first.get(CostEffectivenessVisualsController.Keys.TOTAL_TENDER_WITH_AWARDS);
        double percentageTendersWithAwards = (double) first.
                get(CostEffectivenessVisualsController.Keys.PERCENTAGE_TENDERS_WITH_AWARDS);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(600000.0, totalTenderAmount, 0);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(1, totalTenderWithAwards);
        Assert.assertEquals(100.0, percentageTendersWithAwards, 0);

        final DBObject second = costEffectivenessTenderAmount.get(1);
        year = (int) second.get(CostEffectivenessVisualsController.Keys.YEAR);
        totalTenderAmount = (double) second.get(CostEffectivenessVisualsController.Keys.TOTAL_TENDER_AMOUNT);
        totalTenders = (int) second.get(CostEffectivenessVisualsController.Keys.TOTAL_TENDERS);
        totalTenderWithAwards = (int) second.get(CostEffectivenessVisualsController.Keys.TOTAL_TENDER_WITH_AWARDS);
        percentageTendersWithAwards = (double) second
                .get(CostEffectivenessVisualsController.Keys.PERCENTAGE_TENDERS_WITH_AWARDS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(9000.0, totalTenderAmount, 0);
        Assert.assertEquals(2, totalTenders);
        Assert.assertEquals(1, totalTenderWithAwards);
        Assert.assertEquals(50.0, percentageTendersWithAwards, 0);
    }

    @Test
    public void costEffectivenessTenderAwardAmount() throws Exception {
        final List<DBObject> costEffectivenessTenderAwardAmount = costEffectivenessVisualsController
                .costEffectivenessTenderAwardAmount(new GroupingFilterPagingRequest());

        final DBObject first = costEffectivenessTenderAwardAmount.get(0);
        int year = (int) first.get(CostEffectivenessVisualsController.Keys.YEAR);
        BigDecimal diffTenderAwardAmount = (BigDecimal) first
                .get(CostEffectivenessVisualsController.Keys.DIFF_TENDER_AWARD_AMOUNT);
        Assert.assertEquals(-5400000, diffTenderAwardAmount.doubleValue(), 0);
        Assert.assertEquals(2014, year);

        final DBObject second = costEffectivenessTenderAwardAmount.get(1);
        year = (int) second.get(CostEffectivenessVisualsController.Keys.YEAR);
        diffTenderAwardAmount = (BigDecimal) second
                .get(CostEffectivenessVisualsController.Keys.DIFF_TENDER_AWARD_AMOUNT);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(-31000, diffTenderAwardAmount.doubleValue(), 0);

//        final DBObject third = costEffectivenessTenderAwardAmount.get(2);
//        year = (int) third.get(Fields.UNDERSCORE_ID);
//        diffTenderAwardAmount = (BigDecimal) third
//                .get(CostEffectivenessVisualsController.Keys.DIFF_TENDER_AWARD_AMOUNT);
//        Assert.assertEquals(2014, year);
//        Assert.assertEquals(600000, diffTenderAwardAmount.doubleValue(), 0);
    }

}
