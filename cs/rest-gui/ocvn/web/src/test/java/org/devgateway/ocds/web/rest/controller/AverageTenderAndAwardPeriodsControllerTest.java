package org.devgateway.ocds.web.rest.controller;

import java.util.List;

import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DBObject;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class AverageTenderAndAwardPeriodsControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private AverageTenderAndAwardPeriodsController averageTenderAndAwardPeriodsController;

    @Test
    public void averageTenderPeriod() throws Exception {
        final List<DBObject> averageTenderPeriod = averageTenderAndAwardPeriodsController
                .averageTenderPeriod(new YearFilterPagingRequest());

        final DBObject first = averageTenderPeriod.get(0);
        int year = (int) first.get(AverageTenderAndAwardPeriodsController.Keys.YEAR);
        double averageTenderDays = (double) first.get(AverageTenderAndAwardPeriodsController.Keys.AVERAGE_TENDER_DAYS);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(15.0, averageTenderDays, 0);

        final DBObject second = averageTenderPeriod.get(1);
        year = (int) second.get(AverageTenderAndAwardPeriodsController.Keys.YEAR);
        averageTenderDays = (double) second.get(AverageTenderAndAwardPeriodsController.Keys.AVERAGE_TENDER_DAYS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(46.0, averageTenderDays, 0);
    }

    @Test
    public void qualityAverageTenderPeriod() throws Exception {
        final List<DBObject> qualityAverageTenderPeriod = averageTenderAndAwardPeriodsController
                .qualityAverageTenderPeriod(new DefaultFilterPagingRequest());

        final DBObject first = qualityAverageTenderPeriod.get(0);
        int totalTenderWithStartEndDates = (int) first
                .get(AverageTenderAndAwardPeriodsController.Keys.TOTAL_TENDER_WITH_START_END_DATES);
        int totalTenders = (int) first
                .get(AverageTenderAndAwardPeriodsController.Keys.TOTAL_TENDERS);
        double percentageTenderWithStartEndDates = (double) first
                .get(AverageTenderAndAwardPeriodsController.Keys.PERCENTAGE_TENDER_WITH_START_END_DATES);
        Assert.assertEquals(3, totalTenderWithStartEndDates);
        Assert.assertEquals(3, totalTenders);
        Assert.assertEquals(100.0, percentageTenderWithStartEndDates, 0);
    }

    @Test
    public void averageAwardPeriod() throws Exception {
        final List<DBObject> averageAwardPeriod = averageTenderAndAwardPeriodsController
                .averageAwardPeriod(new YearFilterPagingRequest());

        final DBObject first = averageAwardPeriod.get(0);
        int year = (int) first.get(AverageTenderAndAwardPeriodsController.Keys.YEAR);
        double averageAwardDays = (double) first.get(AverageTenderAndAwardPeriodsController.Keys.AVERAGE_AWARD_DAYS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(365.0, averageAwardDays, 0);

        final DBObject second = averageAwardPeriod.get(1);
        year = (int) second.get(AverageTenderAndAwardPeriodsController.Keys.YEAR);
        averageAwardDays = (double) second.get(AverageTenderAndAwardPeriodsController.Keys.AVERAGE_AWARD_DAYS);
        Assert.assertEquals(2016, year);
        Assert.assertEquals(405.0, averageAwardDays, 0);
    }

    @Test
    public void qualityAverageAwardPeriod() throws Exception {
        final List<DBObject> qualityAverageAwardPeriod = averageTenderAndAwardPeriodsController
                .qualityAverageAwardPeriod(new DefaultFilterPagingRequest());

        final DBObject first = qualityAverageAwardPeriod.get(0);
        int totalAwardWithStartEndDates = (int) first
                .get(AverageTenderAndAwardPeriodsController.Keys.TOTAL_AWARD_WITH_START_END_DATES);
        int totalAwards = (int) first
                .get(AverageTenderAndAwardPeriodsController.Keys.TOTAL_AWARDS);
        double percentageAwardWithStartEndDates = (double) first
                .get(AverageTenderAndAwardPeriodsController.Keys.PERCENTAGE_AWARD_WITH_START_END_DATES);
        Assert.assertEquals(3, totalAwardWithStartEndDates);
        Assert.assertEquals(3, totalAwards);
        Assert.assertEquals(100.0, percentageAwardWithStartEndDates, 0);
    }

}
