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
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class TotalCancelledTendersByYearController extends GenericOCDSController {

    public static final class Keys {
        public static final String TOTAL_CANCELLED_TENDERS_AMOUNT = "totalCancelledTendersAmount";
        public static final String YEAR = "year";
    }

    @ApiOperation(value = "Total Cancelled tenders by year. The tender amount is read from tender.value."
            + "The tender status has to be 'cancelled'. The year is retrieved from tender.tenderPeriod.startDate.")
    @RequestMapping(value = "/api/totalCancelledTendersByYear", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> totalCancelledTendersByYear(@ModelAttribute @Valid final YearFilterPagingRequest filter) {


        DBObject project = new BasicDBObject();
        addYearlyMonthlyProjection(filter, project, MongoConstants.FieldNames.TENDER_PERIOD_START_DATE_REF);
        project.put("tender.value.amount", 1);

        Aggregation agg = newAggregation(
                match(where("tender.status").is("cancelled")
                        .and(MongoConstants.FieldNames.TENDER_PERIOD_START_DATE).exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                new CustomOperation(new BasicDBObject("$project", project)),
                getYearlyMonthlyGroupingOperation(filter).
                sum("$tender.value.amount").as(Keys.TOTAL_CANCELLED_TENDERS_AMOUNT),
                transformYearlyGrouping(filter).andInclude(Keys.TOTAL_CANCELLED_TENDERS_AMOUNT),
                getSortByYearMonth(filter));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

    @ApiOperation(value = "Total Cancelled tenders by year by cancel reason. "
            + "The tender amount is read from tender.value."
            + "The tender status has to be 'cancelled'. The year is retrieved from tender.tenderPeriod.startDate."
            + "The cancellation reason is read from tender.cancellationRationale.")
    @RequestMapping(value = "/api/totalCancelledTendersByYearByRationale", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> totalCancelledTendersByYearByRationale(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put(Fields.UNDERSCORE_ID, 0);
        addYearlyMonthlyProjection(filter, project, "$tender.tenderPeriod.startDate");
        project.put("tender.value.amount", 1);
        project.put("tender.cancellationRationale", 1);

        Aggregation agg = newAggregation(
                match(where("tender.status").is("cancelled").and("tender.tenderPeriod.startDate").exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter, "tender.tenderPeriod.startDate"))),
                new CustomProjectionOperation(project), group(getYearlyMonthlyGroupingFields(
                        filter, "$tender.cancellationRationale"))
                        .sum("$tender.value.amount").as(Keys.TOTAL_CANCELLED_TENDERS_AMOUNT),
                getSortByYearMonth(filter));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }

}