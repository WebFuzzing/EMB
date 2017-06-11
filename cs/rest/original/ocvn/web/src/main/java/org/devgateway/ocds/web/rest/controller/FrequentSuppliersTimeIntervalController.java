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
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 *
 * @author mpostelnicu
 *
 */
@RestController
public class FrequentSuppliersTimeIntervalController extends GenericOCDSController {

    public static class FrequentSuppliersId {

        private String procuringEntityId;
        private String supplierId;
        private Integer timeInterval;

        public String getProcuringEntityId() {
            return procuringEntityId;
        }

        public void setProcuringEntityId(String procuringEntityId) {
            this.procuringEntityId = procuringEntityId;
        }

        public String getSupplierId() {
            return supplierId;
        }

        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }

        public Integer getTimeInterval() {
            return timeInterval;
        }

        public void setTimeInterval(Integer timeInterval) {
            this.timeInterval = timeInterval;
        }

        @Override
        public String toString() {
            return "procuringEntityId=" + procuringEntityId + "; supplierId=" + supplierId
                    + "; timeInterval=" + timeInterval;
        }
    }

    public static class FrequentSuppliersTuple {
        private FrequentSuppliersId identifier;
        private Integer count;
        private Set<String> awardIds;

        public FrequentSuppliersId getIdentifier() {
            return identifier;
        }

        public void setIdentifier(FrequentSuppliersId identifier) {
            this.identifier = identifier;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Set<String> getAwardIds() {
            return awardIds;
        }

        public void setAwardIds(Set<String> awardIds) {
            this.awardIds = awardIds;
        }

        @Override
        public String toString() {
            return identifier.toString() + "; count=" + count;
        }
    }


    @ApiOperation(value = "Returns the frequent suppliers of a procuringEntity split by a time interval. "
            + "The time interval is "
            + "a parameter and represents the number of days to take as interval, starting with today and going back "
            + "till the last award date. The awards are grouped by procuringEntity, supplier and the time interval. "
            + "maxAwards parameter is used to designate what is the maximum number of awards granted to one supplier "
            + "by the same procuringEntity inside one timeInterval. The default value for maxAwards is 3 (days) and the"
            + " default value for intervalDays is 365.")
    @RequestMapping(value = "/api/frequentSuppliersTimeInterval", method = {RequestMethod.POST, RequestMethod.GET},
            produces = "application/json")
    public List<FrequentSuppliersTuple> frequentSuppliersTimeInterval(
            @RequestParam(defaultValue = "365", required = false) Integer intervalDays,
            @RequestParam(defaultValue = "3", required = false) Integer maxAwards) {

        DBObject project = new BasicDBObject();
        project.put("awards._id", 1);
        project.put("tender.procuringEntity._id", 1);
        project.put("awards.suppliers._id", 1);
        project.put("awards.date", 1);
        project.put(Fields.UNDERSCORE_ID, 0);
        project.put("timeInterval", new BasicDBObject("$ceil", new BasicDBObject("$divide",
                Arrays.asList(new BasicDBObject("$divide", Arrays.asList(new BasicDBObject("$subtract",
                        Arrays.asList(new Date(), "$awards.date")), 86400000)), intervalDays))));


        Aggregation agg = Aggregation.newAggregation(
                match(where("tender.procuringEntity").exists(true).and("awards.suppliers.0").exists(true)
                        .and("awards.date").exists(true)),
                unwind("awards"),
                unwind("awards.suppliers"),
                new CustomProjectionOperation(project),
                group(Fields.from(Fields.field("procuringEntityId", "tender.procuringEntity._id"),
                        Fields.field("supplierId", "awards.suppliers._id"),
                        Fields.field("timeInterval", "timeInterval")
                )).
                        count().as("count").addToSet("awards._id").as("awardIds"),
                project("count", "awardIds").and("identifier").previousOperation(),
                match(where("count").gt(maxAwards)),
                sort(Sort.Direction.DESC, "count")
        );

        AggregationResults<FrequentSuppliersTuple> results = mongoTemplate.aggregate(agg, "release",
                FrequentSuppliersTuple.class);
        List<FrequentSuppliersTuple> list = results.getMappedResults();
        return list;
    }

}