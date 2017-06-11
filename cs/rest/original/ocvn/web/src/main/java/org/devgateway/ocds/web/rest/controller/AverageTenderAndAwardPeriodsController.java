/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.ocds.web.rest.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class AverageTenderAndAwardPeriodsController extends GenericOCDSController {



    public static final class Keys {
        public static final String AVERAGE_TENDER_DAYS = "averageTenderDays";
        public static final String TOTAL_TENDER_WITH_START_END_DATES = "totalTenderWithStartEndDates";
        public static final String TOTAL_TENDERS = "totalTenders";
        public static final String TOTAL_AWARDS = "totalAwards";
        public static final String AVERAGE_AWARD_DAYS = "averageAwardDays";
        public static final String TOTAL_AWARD_WITH_START_END_DATES = "totalAwardWithStartEndDates";
        public static final String PERCENTAGE_AWARD_WITH_START_END_DATES = "percentageAwardWithStartEndDates";
        public static final String PERCENTAGE_TENDER_WITH_START_END_DATES = "percentageTenderWithStartEndDates";
        public static final String YEAR = "year";
    }


    @ApiOperation(value = "Calculates the average tender period, per each year. The year is taken from "
            + "tender.tenderPeriod.startDate and the duration is taken by counting the days"
            + "between tender.tenderPeriod.endDate and tender.tenderPeriod.startDate")
    @RequestMapping(value = "/api/averageTenderPeriod", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> averageTenderPeriod(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject tenderLengthDays = new BasicDBObject("$divide",
                Arrays.asList(
                        new BasicDBObject("$subtract",
                                Arrays.asList(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE_REF,
                                        MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF)),
                        MongoConstants.DAY_MS));

        DBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        addYearlyMonthlyProjection(filter, project, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);
        project.put("tenderLengthDays", tenderLengthDays);

        Aggregation agg = newAggregation(
                match(where(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE)
                        .exists(true).and(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE)
                .exists(true).andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                new CustomProjectionOperation(project),
                getYearlyMonthlyGroupingOperation(filter).avg("$tenderLengthDays").as(Keys.AVERAGE_TENDER_DAYS),
                transformYearlyGrouping(filter).andInclude(Keys.AVERAGE_TENDER_DAYS),
                getSortByYearMonth(filter), skip(filter.getSkip()), limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }



    @ApiOperation(value = "Quality indicator for averageTenderPeriod endpoint, "
            + "showing the percentage of tenders that have start and end dates vs the total tenders in the system")
    @RequestMapping(value = "/api/qualityAverageTenderPeriod",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> qualityAverageTenderPeriod(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        project.put("tenderWithStartEndDates",
                new BasicDBObject("$cond",
                        Arrays.asList(
                                new BasicDBObject("$and", Arrays.asList(
                                        new BasicDBObject("$gt",
                                                Arrays.asList(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF,
                                                        null)),
                                        new BasicDBObject("$gt",
                                                Arrays.asList(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE_REF,
                                                        null)))),
                                1, 0)));

        DBObject project1 = new BasicDBObject();
        project1.put(Fields.UNDERSCORE_ID, 0);
        project1.put(Keys.TOTAL_TENDER_WITH_START_END_DATES, 1);
        project1.put(Keys.TOTAL_TENDERS, 1);
        project1.put(Keys.PERCENTAGE_TENDER_WITH_START_END_DATES,
                new BasicDBObject("$multiply", Arrays.asList(
                        new BasicDBObject("$divide", Arrays.asList("$totalTenderWithStartEndDates", "$totalTenders")),
                        100)));

        Aggregation agg = newAggregation(match(getDefaultFilterCriteria(filter)),
                new CustomProjectionOperation(project),
                group().sum("tenderWithStartEndDates").as(Keys.TOTAL_TENDER_WITH_START_END_DATES).count().
                        as(Keys.TOTAL_TENDERS),
                new CustomProjectionOperation(project1));




        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

    @ApiOperation(value = "Calculates the average award period, per each year. The year is taken from "
            + "awards.date and the duration is taken by counting the days"
            + "between tender.tenderPeriod.endDate and tender.tenderPeriod.startDate. The award has to be active.")
    @RequestMapping(value = "/api/averageAwardPeriod", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> averageAwardPeriod(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject awardLengthDays = new BasicDBObject("$divide", Arrays.asList(
                new BasicDBObject("$subtract", Arrays.asList("$awards.date",
                        MongoConstants.FieldNames.TENDER_PERIOD_END_DATE_REF)), MongoConstants.DAY_MS));

        DBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        addYearlyMonthlyProjection(filter, project, "$awards.date");
        project.put("awardLengthDays", awardLengthDays);
        project.put("awards.date", 1);
        project.put("awards.status", 1);
        project.put(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE, 1);

        Aggregation agg = newAggregation(
                // this is repeated so we gain speed by filtering items before
                // unwind
                match(where(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE)
                        .exists(true).and("awards.date").exists(true)
                        .and("awards.status").is("active")),
                unwind("$awards"),
                // we need to filter the awards again after unwind
                match(where("awards.date").exists(true).and("awards.status").is("active")
                        .andOperator(getYearDefaultFilterCriteria(filter, "awards.date"))),
                new CustomOperation(new BasicDBObject("$project", project)),
                group(getYearlyMonthlyGroupingFields(filter)).avg("$awardLengthDays").as(Keys.AVERAGE_AWARD_DAYS),
                transformYearlyGrouping(filter).andInclude(Keys.AVERAGE_AWARD_DAYS),
                getSortByYearMonth(filter), skip(filter.getSkip()),
                limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }


    @ApiOperation(value = "Quality indicator for averageAwardPeriod endpoint, "
            + "showing the percentage of awards that have start and end dates vs the total tenders in the system")
    @RequestMapping(value = "/api/qualityAverageAwardPeriod",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> qualityAverageAwardPeriod(@ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        project.put("awardWithStartEndDates",
                new BasicDBObject("$cond",
                        Arrays.asList(
                                new BasicDBObject("$and", Arrays.asList(
                                        new BasicDBObject("$gt", Arrays.asList("$awards.date", null)),
                                        new BasicDBObject("$gt",
                                                Arrays.asList(MongoConstants.FieldNames.TENDER_PERIOD_END_DATE_REF,
                                                        null)))),
                                1, 0)));

        DBObject project1 = new BasicDBObject();
        project1.put(Fields.UNDERSCORE_ID, 0);
        project1.put(Keys.TOTAL_AWARD_WITH_START_END_DATES, 1);
        project1.put(Keys.TOTAL_AWARDS, 1);
        project1.put(Keys.PERCENTAGE_AWARD_WITH_START_END_DATES,
                new BasicDBObject("$multiply", Arrays.asList(
                        new BasicDBObject("$divide", Arrays.asList("$totalAwardWithStartEndDates", "$totalAwards")),
                        100)));

        Aggregation agg = newAggregation(
                match(where("awards.0").exists(true).andOperator(getDefaultFilterCriteria(filter))), unwind("$awards"),
                new CustomProjectionOperation(project), group().sum("awardWithStartEndDates")
                        .as(Keys.TOTAL_AWARD_WITH_START_END_DATES).count().as(Keys.TOTAL_AWARDS),
                new CustomProjectionOperation(project1));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }


}