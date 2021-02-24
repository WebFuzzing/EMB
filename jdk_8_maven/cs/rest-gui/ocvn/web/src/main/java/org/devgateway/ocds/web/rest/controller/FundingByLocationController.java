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
import org.apache.commons.lang3.ArrayUtils;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.request.DefaultFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomGroupingOperation;
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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
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
public class FundingByLocationController extends GenericOCDSController {

    public static final class Keys {
        public static final String ITEMS_DELIVERY_LOCATION = "deliveryLocation";
        public static final String TOTAL_TENDERS_AMOUNT = "totalTendersAmount";
        public static final String TENDERS_COUNT = "tendersCount";
        public static final String TOTAL_TENDERS_WITH_START_DATE_AND_LOCATION = "totalTendersWithStartDateAndLocation";
        public static final String TOTAL_TENDERS_WITH_START_DATE = "totalTendersWithStartDate";
        public static final String PERCENT_TENDERS_WITH_START_DATE_AND_LOCATION =
                "percentTendersWithStartDateAndLocation";
        public static final String TOTAL_PLANS_WITH_AMOUNTS = "totalPlansWithAmounts";
        public static final String TOTAL_PLANS_WITH_AMOUNTS_AND_LOCATION = "totalPlansWithAmountsAndLocation";
        public static final String PERCENT_PLANS_WITH_AMOUNTS_AND_LOCATION = "percentPlansWithAmountsAndLocation";
        public static final String TOTAL_PLANNED_AMOUNT = "totalPlannedAmount";
        public static final String YEAR = "year";
        public static final String RECORDS_COUNT = "recordsCount";
        public static final String PLANNING_BUDGET_PROJECTLOCATION = "projectLocation";
    }

    @ApiOperation(value = "Total estimated funding (tender.value) grouped by "
            + "tender.items.deliveryLocation and also grouped by year."
            + " The endpoint also returns the count of tenders for each location. "
            + "It responds to all filters. The year is calculated based on tender.tenderPeriod.startDate")
    @RequestMapping(value = "/api/fundingByTenderDeliveryLocation", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> fundingByTenderDeliveryLocation(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("tender.items.deliveryLocation", 1);
        project.put("tender.value.amount", 1);        
        addYearlyMonthlyProjection(filter, project, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);

        Aggregation agg = newAggregation(
                match(where("tender").exists(true).and(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE).exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                new CustomProjectionOperation(project), unwind("$tender.items"),
                unwind("$tender.items.deliveryLocation"),
                project(getYearlyMonthlyGroupingFields(filter)).and("tender.value.amount")
                        .as("tenderAmount")
                        .and("tender.items.deliveryLocation").as("deliveryLocation"),
                match(where("deliveryLocation.geometry.coordinates.0").exists(true)),
                group(getYearlyMonthlyGroupingFields(filter, Keys.ITEMS_DELIVERY_LOCATION))
                        .sum("tenderAmount").as(Keys.TOTAL_TENDERS_AMOUNT).count().as(Keys.TENDERS_COUNT),
                getSortByYearMonth(filter)
        // ,skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }


    @ApiOperation("Calculates percentage of releases with tender with at least one specified delivery location,"
            + " that is the array tender.items.deliveryLocation has to have items."
            + "Filters out stub tenders, therefore tender.tenderPeriod.startDate has to exist.")
    @RequestMapping(value = "/api/qualityFundingByTenderDeliveryLocation", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> qualityFundingByTenderDeliveryLocation(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.putAll(filterProjectMap);
        project.put(Fields.UNDERSCORE_ID, "$tender._id");
        project.put("tenderItemsDeliveryLocation", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$gt",
                        Arrays.asList("$tender.items.deliveryLocation", 0)), 1, 0)));


        DBObject project1 = new BasicDBObject();
        project1.put(Fields.UNDERSCORE_ID, 0);
        project1.put(Keys.TOTAL_TENDERS_WITH_START_DATE, 1);
        project1.put(Keys.TOTAL_TENDERS_WITH_START_DATE_AND_LOCATION, 1);
        project1.put(Keys.PERCENT_TENDERS_WITH_START_DATE_AND_LOCATION,
                new BasicDBObject("$multiply",
                        Arrays.asList(new BasicDBObject("$divide",
                                        Arrays.asList(
                                                "$totalTendersWithStartDateAndLocation", "$totalTendersWithStartDate")),
                                100)));

        Aggregation agg = newAggregation(
                match(where(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE).exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                unwind("$tender.items"), new CustomProjectionOperation(project),
                group(Fields.UNDERSCORE_ID_REF).max("tenderItemsDeliveryLocation").as("hasTenderItemsDeliverLocation"),
                group().count().as("totalTendersWithStartDate").sum("hasTenderItemsDeliverLocation")
                        .as(Keys.TOTAL_TENDERS_WITH_START_DATE_AND_LOCATION),
                new CustomProjectionOperation(project1)
                //,skip(filter.getSkip()),limit(filter.getPageSize())
               );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }


    @ApiOperation(value = "Planned funding by location by year. Returns the total amount of planning.budget"
            + " available per planning.budget.projectLocation, grouped by year. "
            + "This will return full location information, including geocoding data."
            + "Responds only to the procuring entity id filter: tender.procuringEntity._id")
    @RequestMapping(value = "/api/plannedFundingByLocation",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> plannedFundingByLocation(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject vars = new BasicDBObject();
        vars.put("numberOfLocations", new BasicDBObject("$size", "$planning.budget.projectLocation"));
        vars.put("planningBudget", "$planning.budget.amount.amount");
        DBObject in = new BasicDBObject("$divide", Arrays.asList("$$planningBudget", "$$numberOfLocations"));

        DBObject let = new BasicDBObject();
        let.put("vars", vars);
        let.put("in", in);

        DBObject dividedTotal = new BasicDBObject("$let", let);

        DBObject project = new BasicDBObject();
        project.put("planning.budget.projectLocation", 1);
        project.put("cntprj", new BasicDBObject("$literal", 1));
        project.put("planning.budget.amount.amount", 1);
        project.put("dividedTotal", dividedTotal);
        addYearlyMonthlyProjection(filter, project, "$planning.bidPlanProjectDateApprove");

        Aggregation agg =
                newAggregation(
                        match(where("planning").exists(true).and("planning.budget.projectLocation.0").exists(true)
                                .andOperator(getProcuringEntityIdCriteria(filter),
                                        getYearFilterCriteria(filter, "planning.bidPlanProjectDateApprove"))),
                new CustomProjectionOperation(project), unwind("$planning.budget.projectLocation"),
                match(where("planning.budget.projectLocation.geometry.coordinates.0").exists(true)),
                        project(getYearlyMonthlyGroupingFields(filter))
                                .and("dividedTotal").as("dividedTotal")
                                .and("cntprj").as("cntprj")
                                .and("planning.budget.projectLocation").as("projectLocation"),
                group(getYearlyMonthlyGroupingFields(filter,  Keys.PLANNING_BUDGET_PROJECTLOCATION))
                        .sum("dividedTotal").as(Keys.TOTAL_PLANNED_AMOUNT)
                        .sum("cntprj").as(Keys.RECORDS_COUNT),
                        getSortByYearMonth(filter)
                );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;

    }


    @ApiOperation("Calculates percentage of releases with planning with at least one specified location,"
            + " that is the array planning.budget.projectLocation has to be initialzied."
            + "Filters out stub planning, therefore planning.budget.amount has to exist."
            + "Responds only to the procuring entity id filter: tender.procuringEntity._id")
    @RequestMapping(value = "/api/qualityPlannedFundingByLocation", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> qualityPlannedFundingByLocation(
            @ModelAttribute @Valid final DefaultFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.putAll(filterProjectMap);
        project.put("planning.budget.projectLocation", 1);
        project.put("planning.budget.amount", 1);
        project.put(Fields.UNDERSCORE_ID, 0);

        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, null);
        group.put(Keys.TOTAL_PLANS_WITH_AMOUNTS, new BasicDBObject("$sum", 1));
        group.put(Keys.TOTAL_PLANS_WITH_AMOUNTS_AND_LOCATION,
                new BasicDBObject("$sum", new BasicDBObject("$cond", Arrays.asList(
                        new BasicDBObject("$gt",
                                Arrays.asList(
                                        new BasicDBObject("$size",
                                                new BasicDBObject("$ifNull", Arrays.asList(
                                                        "$planning.budget.projectLocation", ArrayUtils.toArray()))),
                                        0)),
                        1, 0))));

        DBObject project2 = new BasicDBObject();
        project2.put(Fields.UNDERSCORE_ID, 0);
        project2.put(Keys.TOTAL_PLANS_WITH_AMOUNTS, 1);
        project2.put(Keys.TOTAL_PLANS_WITH_AMOUNTS_AND_LOCATION, 1);
        project2.put(Keys.PERCENT_PLANS_WITH_AMOUNTS_AND_LOCATION,
                new BasicDBObject("$multiply",
                        Arrays.asList(
                                new BasicDBObject("$divide",
                                        Arrays.asList("$totalPlansWithAmountsAndLocation", "$totalPlansWithAmounts")),
                                100)));

        Aggregation agg = newAggregation(new CustomProjectionOperation(project),
                match(where("planning.budget.amount").exists(true).andOperator(getProcuringEntityIdCriteria(filter))),
                new CustomGroupingOperation(group), new CustomProjectionOperation(project2)
                //, skip(filter.getSkip()),limit(filter.getPageSize())
                );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

}