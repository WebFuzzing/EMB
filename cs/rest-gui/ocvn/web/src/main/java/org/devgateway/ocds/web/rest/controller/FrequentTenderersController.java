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

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.validation.Valid;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.aggregation.ConditionalOperators;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author mpostelnicu
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class FrequentTenderersController extends GenericOCDSController {


    @ApiOperation(value = "Detect frequent pairs of tenderers that apply together to bids."
            + "We are only showing pairs if they applied to more than one bid together."
            + "We are sorting the results after the number of occurences, descending."
            + "You can use all the filters that are available along with pagination options.")
    @RequestMapping(value = "/api/frequentTenderers", method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<DBObject> frequentTenderers(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(where("tender.tenderers.1").exists(true).and("awards.suppliers.0").exists(true)
                        .and("awards.status").is("active")
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                unwind("tender.tenderers"),
                unwind("awards"),
                unwind("awards.suppliers"),
                match(where("awards.status").is("active")),
                project().and("awards.suppliers._id").as("supplierId")
                        .and("tender.tenderers._id").as("tendererId").andExclude(Fields.UNDERSCORE_ID)
                        .and(ComparisonOperators.valueOf("awards.suppliers._id").
                                compareTo("tender.tenderers._id")).as("cmp"),
                match((where("cmp").ne(0))),
                project("supplierId", "tendererId", "cmp")
                        .and(ConditionalOperators.when(
                                Criteria.where("cmp").is(1)).thenValueOf("$supplierId")
                                .otherwiseValueOf("$tendererId")).as("tendererId1")
                        .and(ConditionalOperators.when(
                                Criteria.where("cmp").is(1)).thenValueOf("$tendererId")
                                .otherwiseValueOf("$supplierId")).as("tendererId2"),
                group("tendererId1", "tendererId2").count().as("pairCount"),
                sort(Sort.Direction.DESC, "pairCount"),  skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

    @ApiOperation(value = "Counts the tenders/awards where the given supplier id is among the winners. "
            + "This assumes there is only  one active award, which always seems to be the case, per tender. ")
    @RequestMapping(value = "/api/activeAwardsCount",
            method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<DBObject> activeAwardsCount(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(where("awards.status").is("active")
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                unwind("awards"),
                match(where("awards.status").is("active")
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                group().count().as("cnt"),
                project("cnt").andExclude(Fields.UNDERSCORE_ID)
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

}