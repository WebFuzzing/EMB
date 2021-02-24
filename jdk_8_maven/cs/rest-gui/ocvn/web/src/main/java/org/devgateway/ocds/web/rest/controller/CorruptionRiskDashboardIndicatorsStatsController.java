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
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author mpostelnicu
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class CorruptionRiskDashboardIndicatorsStatsController extends GenericOCDSController {

    public static final class Keys {
        public static final String TYPE = "type";
        public static final String INDICATOR_COUNT = "indicatorCount";
        public static final String ELIGIBLE_PROJECT_COUNT = "eligibleProjectCount";
        public static final String FLAGGED_PROJECT_COUNT = "flaggedProjectCount";
        public static final String ELIGIBLE_COUNT = "eligibleCount";
        public static final String FLAGGED_COUNT = "flaggedCount";
        public static final String YEAR = "year";
        public static final String MONTH = "month";
        public static final String PROJECT_COUNT = "projectCount";
        public static final String PERCENT = "percent";
    }

    private static final String ELIGIBLE_STATS = "eligibleStats";
    private static final String FLAGGED_STATS = "flaggedStats";


    @ApiOperation(value = "Counts the indicators flagged, and groups them by indicator type. "
            + "An indicator that has two types it will be counted twice, once in each group.")
    @RequestMapping(value = "/api/totalFlaggedIndicatorsByIndicatorType",
            method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<DBObject> totalFlaggedIndicatorsByIndicatorType(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        return totalIndicatorsByIndicatorType(FLAGGED_STATS, filter);
    }

    @ApiOperation(value = "Counts the indicators eligible, and groups them by indicator type. "
            + "An indicator that has two types it will be counted twice, once in each group.")
    @RequestMapping(value = "/api/totalEligibleIndicatorsByIndicatorType",
            method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<DBObject> totalEligibleIndicatorsByIndicatorType(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        return totalIndicatorsByIndicatorType(ELIGIBLE_STATS, filter);
    }

    @ApiOperation(value = "Counts the indicators that are flagged, across all releases. If one "
            + "indicator has multiple types is only counted once, so this is different from flaggedStats and "
            + "cannot be reproduced by just summing up all types in flaggedStats!")
    @RequestMapping(value = "/api/totalFlags", method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<DBObject> totalFlags(final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE)),
                project().and("flags.totalFlagged").as("totalFlagged"),
                group().sum("totalFlagged").as(Keys.FLAGGED_COUNT),
                project(Keys.FLAGGED_COUNT).andExclude(Fields.UNDERSCORE_ID)
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release",
                DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }



    private List<DBObject> totalIndicatorsByIndicatorType(String statsProperty,
                                                          final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(where("flags." + statsProperty + ".0").exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                unwind("flags." + statsProperty),
                project("flags." + statsProperty),
                group(statsProperty + ".type").sum(statsProperty + ".count").as(Keys.INDICATOR_COUNT),
                project(Keys.INDICATOR_COUNT).and(Fields.UNDERSCORE_ID).as(Keys.TYPE).andExclude(Fields.UNDERSCORE_ID),
                sort(Sort.Direction.ASC,   "type")
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release",
                DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

    @ApiOperation(value = "Counts the indicators flagged, and groups them by indicator type and by year/month. "
            + "An indicator that has two types it will be counted twice, once in each group.")
    @RequestMapping(value = "/api/totalFlaggedIndicatorsByIndicatorTypeByYear",
            method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<DBObject> totalFlaggedIndicatorsByIndicatorTypeByYear(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        return totalIndicatorsByIndicatorTypeByYear(FLAGGED_STATS, filter);
    }

    @ApiOperation(value = "Counts the indicators eligible, and groups them by indicator type and by year/month. "
            + "An indicator that has two types it will be counted twice, once in each group.")
    @RequestMapping(value = "/api/totalEligibleIndicatorsByIndicatorTypeByYear",
            method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<DBObject> totalEligibleIndicatorsByIndicatorTypeByYear(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        return totalIndicatorsByIndicatorTypeByYear(ELIGIBLE_STATS, filter);
    }


    private List<DBObject> totalIndicatorsByIndicatorTypeByYear(String statsProperty,
                                                                final YearFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        addYearlyMonthlyProjection(filter, project1, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);
        project1.put("stats", "$flags." + statsProperty);
        project1.put(Fields.UNDERSCORE_ID, 0);

        Aggregation agg = newAggregation(
                match(where(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE).exists(true)
                        .and("flags." + statsProperty + ".0").exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                unwind("flags." + statsProperty),
                new CustomProjectionOperation(project1),
                group(getYearlyMonthlyGroupingFields(filter, "stats.type")).
                        sum("stats.count").as(Keys.INDICATOR_COUNT),
                getSortByYearMonthWhenOtherGroups(filter, "_id.type")
        );


        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release",
                DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

    @ApiOperation(value = "Counts the projects and the indicators flagged, grouped by indicator type. "
            + "The 'count' represents the number of indicators flagged, the 'projectCount' represents the number"
            + " of projects flagged.")
    @RequestMapping(value = "/api/totalFlaggedProjectsByIndicatorTypeByYear",
            method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public List<DBObject> totalFlaggedProjectsByIndicatorTypeByYear(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {
        return totalProjectsByIndicatorTypeByYear(FLAGGED_STATS, filter);
    }

    @ApiOperation(value = "Counts the projects and the indicators eligible, grouped by indicator type. "
            + "The 'count' represents the number of indicators eligible, the 'projectCount' represents the number"
            + " of projects eligible.")
    @RequestMapping(value = "/api/totalEligibleProjectsByIndicatorTypeByYear",
            method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public List<DBObject> totalEligibleProjectsByIndicatorTypeByYear(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {
        return totalProjectsByIndicatorTypeByYear(ELIGIBLE_STATS, filter);
    }


    private List<DBObject> totalProjectsByIndicatorTypeByYear(String statsProperty,
                                                              final YearFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        addYearlyMonthlyProjection(filter, project1, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);
        project1.put("stats", "$flags." + statsProperty);
        project1.put(Fields.UNDERSCORE_ID, 0);

        Aggregation agg = newAggregation(
                match(where(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE).exists(true)
                        .and("flags." + statsProperty + ".0").exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                unwind("flags." + statsProperty),
                new CustomProjectionOperation(project1),
                group(getYearlyMonthlyGroupingFields(filter, "stats.type")).
                        sum("stats.count").as(
                        statsProperty.equals(ELIGIBLE_STATS) ? Keys.ELIGIBLE_COUNT : Keys.FLAGGED_COUNT).count()
                        .as(statsProperty.equals(ELIGIBLE_STATS)
                                ? Keys.ELIGIBLE_PROJECT_COUNT : Keys.FLAGGED_PROJECT_COUNT),
                getSortByYearMonthWhenOtherGroups(filter, "_id.type")
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release",
                DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }


    @ApiOperation(value = "Count total projects by year/month.")
    @RequestMapping(value = "/api/totalProjectsByYear",
            method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public List<DBObject> totalProjectsByYear(final YearFilterPagingRequest filter) {

        DBObject project1 = new BasicDBObject();
        addYearlyMonthlyProjection(filter, project1, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);
        project1.put(Fields.UNDERSCORE_ID, 0);

        Aggregation agg = newAggregation(
                match(where(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE).exists(true).
                        andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                new CustomProjectionOperation(project1),
                group(getYearlyMonthlyGroupingFields(filter)).
                        count().as(Keys.PROJECT_COUNT),
                transformYearlyGrouping(filter).andInclude(Keys.PROJECT_COUNT),
                getSortByYearMonth(filter)
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release",
                DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

    /**
     * Finds a specific {@link DBObject} by year and by type (if available) and (if available) month.
     * Very inefficient but we have only 10-12 entries so simplicity prevails :-)
     *
     * @param year
     * @param month
     * @return
     */
    private List<DBObject> findByYearAndTypeAndMonth(List<DBObject> source, Integer year, String type, Integer month) {
        return source.stream().filter(o -> year.equals(o.get(Keys.YEAR))
                && (type == null || type.equals(o.get(Keys.TYPE)))
                && (month == null || month.equals(o.get(Keys.MONTH)))).collect(Collectors.toList());
    }

    @ApiOperation(value = "Percent of total projects flagged (denominator is total projects)")
    @RequestMapping(value = "/api/percentTotalProjectsFlaggedByYear",
            method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public List<DBObject> percentTotalProjectsFlaggedByYear(final YearFilterPagingRequest filter) {

        //get the total projects flagged by year
        List<DBObject> totalFlaggedProjects = totalFlaggedProjectsByIndicatorTypeByYear(filter);

        //denominator total projects eligible by year
        List<DBObject> totalProjectsByYear = totalProjectsByYear(filter);

        totalFlaggedProjects.forEach(e -> {
            findByYearAndTypeAndMonth(totalProjectsByYear, (Integer) e.get(Keys.YEAR), null,
                    (Integer) e.get(Keys.MONTH))
                    .forEach(f -> {
                        e.put(Keys.PROJECT_COUNT, f.get(Keys.PROJECT_COUNT));
                        e.put(Keys.PERCENT, (BigDecimal.valueOf((Integer) e.get(Keys.FLAGGED_PROJECT_COUNT))
                                .setScale(BIGDECIMAL_SCALE)
                                .divide(BigDecimal.valueOf((Integer) f.get(Keys.PROJECT_COUNT)),
                                        BigDecimal.ROUND_HALF_UP).multiply(ONE_HUNDRED)));
                    });
        });

        return totalFlaggedProjects;
    }

    @ApiOperation(value = "Percent of total projects eligible (denominator is total projects)")
    @RequestMapping(value = "/api/percentTotalProjectsEligibleByYear",
            method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public List<DBObject> percentTotalProjectsEligibleByYear(final YearFilterPagingRequest filter) {

        //get the total projects eligible by year
        List<DBObject> totalEligibleProjects = totalEligibleProjectsByIndicatorTypeByYear(filter);

        //denominator total projects eligible by year
        List<DBObject> totalProjectsByYear = totalProjectsByYear(filter);

        totalEligibleProjects.forEach(e -> {
            findByYearAndTypeAndMonth(totalProjectsByYear, (Integer) e.get(Keys.YEAR), null,
                    (Integer) e.get(Keys.MONTH))
                    .forEach(f -> {
                        e.put(Keys.PROJECT_COUNT, f.get(Keys.PROJECT_COUNT));
                        e.put(Keys.PERCENT, (BigDecimal.valueOf((Integer) e.get(Keys.ELIGIBLE_PROJECT_COUNT))
                                .setScale(BIGDECIMAL_SCALE)
                                .divide(BigDecimal.valueOf((Integer) f.get(Keys.PROJECT_COUNT)),
                                        BigDecimal.ROUND_HALF_UP).multiply(ONE_HUNDRED)));
                    });
        });

        return totalEligibleProjects;
    }


    @ApiOperation(value = "Percent of eligible projects flagged (denominator is number of eligible projects)")
    @RequestMapping(value = "/api/percentOfEligibleProjectsFlaggedByYear",
            method = {RequestMethod.POST, RequestMethod.GET}, produces = "application/json")
    public List<DBObject> percentOfEligibleProjectsFlaggedByYear(final YearFilterPagingRequest filter) {

        //get the total projects eligible by year
        List<DBObject> totalFlaggedProjects = totalFlaggedProjectsByIndicatorTypeByYear(filter);

        //denominator total projects eligible by year
        List<DBObject> totalEligibleProjectsByYear = totalEligibleProjectsByIndicatorTypeByYear(filter);

        //because this is reversed, we may end up with empty percentages on the eligible side, so we need to add zeros
        totalEligibleProjectsByYear.forEach(e -> {
            e.put(Keys.PERCENT, BigDecimal.ZERO);
            e.put(Keys.FLAGGED_PROJECT_COUNT, 0);
        });

        totalEligibleProjectsByYear.forEach(e -> {
            findByYearAndTypeAndMonth(totalFlaggedProjects, (Integer) e.get(Keys.YEAR), (String) e.get(Keys.TYPE),
                    (Integer) e.get(Keys.MONTH))
                    .forEach(f -> {
                        e.put(Keys.FLAGGED_PROJECT_COUNT, f.get(Keys.FLAGGED_PROJECT_COUNT));
                        e.put(Keys.PERCENT, (BigDecimal.valueOf((Integer) f.get(Keys.FLAGGED_PROJECT_COUNT))
                                .setScale(BIGDECIMAL_SCALE)
                                .divide(BigDecimal.valueOf((Integer) e.get(Keys.ELIGIBLE_PROJECT_COUNT)),
                                        BigDecimal.ROUND_HALF_UP).multiply(ONE_HUNDRED)));
                    });
        });

        return totalEligibleProjectsByYear;
    }


}