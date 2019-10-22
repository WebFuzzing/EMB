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
package org.devgateway.ocds.web.rest.controller.selector;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;

import java.util.List;

import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import io.swagger.annotations.ApiOperation;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
@Cacheable
@CacheConfig(cacheNames = "bidSelectionMethodsJson")
public class BidSelectionMethodSearchController extends GenericOCDSController {

    /**
     * db.release.aggregate([ {$project : {"tender.procurementMethodDetails":1}
     * }, {$group: {_id: "$tender.procurementMethodDetails" }} ])
     *
     * @return
     */
    @ApiOperation(value = "Display the available bid selection methods. "
            + "These are taken from tender.procurementMethodDetails")
    @RequestMapping(value = "/api/ocds/bidSelectionMethod/all", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> bidSelectionMethods() {

        DBObject project = new BasicDBObject("tender.procurementMethodDetails", 1);

        Aggregation agg = newAggregation(new CustomOperation(new BasicDBObject("$project", project)),
                group("$tender.procurementMethodDetails"), sort(Direction.ASC, Fields.UNDERSCORE_ID));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);

        List<DBObject> mappedResults = results.getMappedResults();

        return mappedResults;

    }

}