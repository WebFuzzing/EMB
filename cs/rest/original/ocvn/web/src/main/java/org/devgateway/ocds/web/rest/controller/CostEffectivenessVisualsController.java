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
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.request.GroupingFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomGroupingOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomUnwindOperation;
import org.devgateway.toolkit.web.spring.AsyncControllerLookupService;
import org.devgateway.toolkit.web.spring.util.AsyncBeanParamControllerMethodCallable;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
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
public class CostEffectivenessVisualsController extends GenericOCDSController {



    @Autowired
    private AsyncControllerLookupService controllerLookupService;

    public static final class Keys {
        public static final String TOTAL_AWARD_AMOUNT = "totalAwardAmount";
        public static final String YEAR = "year";
        public static final String TOTAL_AWARDS = "totalAwards";
        public static final String TOTAL_AWARDS_WITH_TENDER = "totalAwardsWithTender";
        public static final String PERCENTAGE_AWARDS_WITH_TENDER = "percentageAwardsWithTender";
        private static final String FRACTION_AWARDS_WITH_TENDER = "fractionAwardsWithTender";
        public static final String TOTAL_TENDER_AMOUNT = "totalTenderAmount";
        public static final String TOTAL_TENDERS = "totalTenders";
        public static final String TOTAL_TENDER_WITH_AWARDS = "totalTenderWithAwards";
        public static final String PERCENTAGE_TENDERS_WITH_AWARDS = "percentageTendersWithAwards";
        private static final String FRACTION_TENDERS_WITH_AWARDS = "fractionTendersWithAwards";
        public static final String PERCENTAGE_AWARD_AMOUNT = "percentageAwardAmount";
        public static final String PERCENTAGE_DIFF_AMOUNT = "percentageDiffAmount";
        public static final String DIFF_TENDER_AWARD_AMOUNT = "diffTenderAwardAmount";
        private static final String YEAR_MONTH = "year-month"; //this is for internal use
        public static final String MONTH = "month";
    }


    @ApiOperation(value = "Cost effectiveness of Awards: Displays the total amount of active awards grouped by year."
            + "The tender entity, for each award, has to have amount value. The year is calculated from "
            + MongoConstants.FieldNames.TENDER_PERIOD_START_DATE)
    @RequestMapping(value = "/api/costEffectivenessAwardAmount",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> costEffectivenessAwardAmount(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        addYearlyMonthlyProjection(filter, project, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);
        project.put("awards.value.amount", 1);
        project.put("totalAwardsWithTender", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$gt",
                        Arrays.asList(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF, null)), 1, 0)));
        project.put("awardsWithTenderValue",
                new BasicDBObject("$cond",
                        Arrays.asList(new BasicDBObject("$gt",
                                        Arrays.asList(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF, null)),
                                "$awards.value.amount", 0)));

        Aggregation agg = Aggregation.newAggregation(
                match(where("awards").elemMatch(where("status").is(Award.Status.active.toString())).and("awards.date")
                        .exists(true).and(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE).exists(true)),
                getMatchDefaultFilterOperation(filter), unwind("$awards"),
                match(where("awards.status").is(Award.Status.active.toString()).and("awards.value").exists(true).
                        andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                new CustomProjectionOperation(project),
                getYearlyMonthlyGroupingOperation(filter)
                        .sum("awardsWithTenderValue").as(Keys.TOTAL_AWARD_AMOUNT).count().as(Keys.TOTAL_AWARDS)
                        .sum("totalAwardsWithTender").as(Keys.TOTAL_AWARDS_WITH_TENDER),
                project(Fields.UNDERSCORE_ID, Keys.TOTAL_AWARD_AMOUNT, Keys.TOTAL_AWARDS, Keys.TOTAL_AWARDS_WITH_TENDER)
                        .and(Keys.TOTAL_AWARDS_WITH_TENDER).divide(Keys.TOTAL_AWARDS)
                        .as(Keys.FRACTION_AWARDS_WITH_TENDER),
                project(Fields.UNDERSCORE_ID, Keys.TOTAL_AWARD_AMOUNT, Keys.TOTAL_AWARDS, Keys.TOTAL_AWARDS_WITH_TENDER,
                        Keys.FRACTION_AWARDS_WITH_TENDER).and(Keys.FRACTION_AWARDS_WITH_TENDER).multiply(100)
                                .as(Keys.PERCENTAGE_AWARDS_WITH_TENDER),
                transformYearlyGrouping(filter).andInclude(Keys.TOTAL_AWARD_AMOUNT, Keys.TOTAL_AWARDS,
                        Keys.TOTAL_AWARDS_WITH_TENDER, Keys.PERCENTAGE_AWARDS_WITH_TENDER
                ), getSortByYearMonth(filter),
                skip(filter.getSkip()), limit(filter.getPageSize()));


        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

    @ApiOperation(value = "Cost effectiveness of Tenders:"
            + " Displays the total amount of the active tenders that have active awards, "
            + "grouped by year. Only tenders.status=active"
            + "are taken into account. The year is calculated from tenderPeriod.startDate")
    @RequestMapping(value = "/api/costEffectivenessTenderAmount",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject> costEffectivenessTenderAmount(
            @ModelAttribute @Valid final GroupingFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF));
        addYearlyMonthlyProjection(filter, project, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);
        project.put("tender.value.amount", 1);
        project.put(Fields.UNDERSCORE_ID, "$tender._id");
        project.put("tenderWithAwards",
                new BasicDBObject("$cond", Arrays.asList(
                        new BasicDBObject("$eq", Arrays.asList("$awards.status", Award.Status.active.toString())), 1,
                        0)));
        project.put("tenderWithAwardsValue", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$eq", Arrays.asList("$awards.status", Award.Status.active.toString())),
                        "$tender.value.amount", 0)));
        project.putAll(filterProjectMap);

        DBObject group1 = new BasicDBObject();
        group1.put(Fields.UNDERSCORE_ID, Fields.UNDERSCORE_ID_REF);
        addYearlyMonthlyGroupingOperationFirst(filter, group1);
        group1.put("tenderWithAwards", new BasicDBObject("$max", "$tenderWithAwards"));
        group1.put("tenderWithAwardsValue", new BasicDBObject("$max", "$tenderWithAwardsValue"));
        group1.put("tenderAmount", new BasicDBObject("$first", "$tender.value.amount"));
        filterProjectMap.forEach((k, v) ->
                group1.put(k.replace(".", ""),
                        k.equals("tender.items.classification._id")
                                ? new BasicDBObject("$first", new BasicDBObject("$arrayElemAt",
                                        Arrays.asList("$" + k, 0)))
                                :
                                new BasicDBObject("$first", "$" + k)));

        Aggregation agg = Aggregation.newAggregation(
                match(where("tender.status").is(Tender.Status.active.toString()).
                        and(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE)
                        .exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                getMatchDefaultFilterOperation(filter),
                new CustomUnwindOperation("$awards", true),
                new CustomProjectionOperation(project),
                new CustomGroupingOperation(group1),
                getTopXFilterOperation(filter, getYearlyMonthlyGroupingFields(filter)).sum("tenderWithAwardsValue")
                        .as(Keys.TOTAL_TENDER_AMOUNT).count().as(Keys.TOTAL_TENDERS).sum("tenderWithAwards")
                        .as(Keys.TOTAL_TENDER_WITH_AWARDS),
                project(Keys.TOTAL_TENDER_AMOUNT, Keys.TOTAL_TENDERS, Keys.TOTAL_TENDER_WITH_AWARDS)
                        .andInclude(Fields.from(Fields.field(Fields.UNDERSCORE_ID, Fields.UNDERSCORE_ID_REF)))
                        .and(Keys.TOTAL_TENDER_WITH_AWARDS).divide(Keys.TOTAL_TENDERS)
                        .as(Keys.FRACTION_TENDERS_WITH_AWARDS),
                project(Keys.TOTAL_TENDER_AMOUNT, Keys.TOTAL_TENDERS, Keys.TOTAL_TENDER_WITH_AWARDS,
                        Fields.UNDERSCORE_ID).and(Keys.FRACTION_TENDERS_WITH_AWARDS).multiply(100)
                                .as(Keys.PERCENTAGE_TENDERS_WITH_AWARDS),
                (filter.getGroupByCategory() == null
                        ? transformYearlyGrouping(filter) : project()).andInclude(Keys.TOTAL_TENDER_AMOUNT,
                        Keys.TOTAL_TENDERS,
                        Keys.TOTAL_TENDER_WITH_AWARDS, Keys.PERCENTAGE_TENDERS_WITH_AWARDS),
                filter.getGroupByCategory() == null
                        ? getSortByYearMonth(filter) : sort(Sort.Direction.DESC, Keys.TOTAL_TENDER_AMOUNT),
                skip(filter.getSkip()), limit(filter.getPageSize()))
                .withOptions(Aggregation.newAggregationOptions().allowDiskUse(true).build());

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();


        return tagCount;

    }


    private String getYearMonthlyKey(GroupingFilterPagingRequest filter, DBObject db) {
        return filter.getMonthly() ? db.get(Keys.YEAR) + "-" + db.get(Keys.MONTH) : db.get(Keys.YEAR).toString();
    }

    @ApiOperation(value = "Aggregated version of /api/costEffectivenessTenderAmount and "
            + "/api/costEffectivenessAwardAmount."
            + "This endpoint aggregates the responses from the specified endpoints, per year. "
            + "Responds to the same filters.")
    @RequestMapping(value = "/api/costEffectivenessTenderAwardAmount", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> costEffectivenessTenderAwardAmount(
            @ModelAttribute @Valid final GroupingFilterPagingRequest filter) {

        Future<List<DBObject>> costEffectivenessAwardAmountFuture = controllerLookupService
                .asyncInvoke(new AsyncBeanParamControllerMethodCallable<List<DBObject>, GroupingFilterPagingRequest>() {
                    @Override
                    public List<DBObject> invokeControllerMethod(GroupingFilterPagingRequest filter) {
                        return costEffectivenessAwardAmount(filter);
                    }
                }, filter);


        Future<List<DBObject>> costEffectivenessTenderAmountFuture = controllerLookupService
                .asyncInvoke(new AsyncBeanParamControllerMethodCallable<List<DBObject>, GroupingFilterPagingRequest>() {
                    @Override
                    public List<DBObject> invokeControllerMethod(GroupingFilterPagingRequest filter) {
                        return costEffectivenessTenderAmount(filter);
                    }
                }, filter);


        //this is completely unnecessary since the #get methods are blocking
        //controllerLookupService.waitTillDone(costEffectivenessAwardAmountFuture, costEffectivenessTenderAmountFuture);


        LinkedHashMap<Object, DBObject> response = new LinkedHashMap<>();

        try {

            costEffectivenessAwardAmountFuture.get()
                    .forEach(dbobj -> response.put(getYearMonthlyKey(filter, dbobj), dbobj));
            costEffectivenessTenderAmountFuture.get().forEach(dbobj -> {
                if (response.containsKey(getYearMonthlyKey(filter, dbobj))) {
                    Map<?, ?> map = dbobj.toMap();
                    map.remove(Keys.YEAR);
                    if (filter.getMonthly()) {
                        map.remove(Keys.MONTH);
                    }
                    response.get(getYearMonthlyKey(filter, dbobj)).putAll(map);
                } else {
                    response.put(getYearMonthlyKey(filter, dbobj), dbobj);
                }
            });

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        Collection<DBObject> respCollection = response.values();

        respCollection.forEach(dbobj -> {

            BigDecimal totalTenderAmount = BigDecimal
                    .valueOf(dbobj.get(Keys.TOTAL_TENDER_AMOUNT) == null ? 0d
                            : ((Number) dbobj.get(Keys.TOTAL_TENDER_AMOUNT)).doubleValue());

            BigDecimal totalAwardAmount = BigDecimal
                    .valueOf(dbobj.get(Keys.TOTAL_AWARD_AMOUNT) == null ? 0d
                            : ((Number) dbobj.get(Keys.TOTAL_AWARD_AMOUNT)).doubleValue());

            dbobj.put(Keys.DIFF_TENDER_AWARD_AMOUNT,
                    totalTenderAmount
                            .subtract(totalAwardAmount));

            dbobj.put(Keys.PERCENTAGE_AWARD_AMOUNT,
                    totalTenderAmount.compareTo(BigDecimal.ZERO) != 0
                            ? (totalAwardAmount.setScale(15)
                                    .divide(totalTenderAmount, BigDecimal.ROUND_HALF_UP)
                                    .multiply(ONE_HUNDRED)) : BigDecimal.ZERO);

            dbobj.put(Keys.PERCENTAGE_DIFF_AMOUNT,
                    totalTenderAmount.compareTo(BigDecimal.ZERO) != 0
                            ? (((BigDecimal) dbobj.get(Keys.DIFF_TENDER_AWARD_AMOUNT)).setScale(15)
                            .divide(totalTenderAmount, BigDecimal.ROUND_HALF_UP)
                            .multiply(ONE_HUNDRED)) : BigDecimal.ZERO);

        });

        return new ArrayList<>(respCollection);
    }



}