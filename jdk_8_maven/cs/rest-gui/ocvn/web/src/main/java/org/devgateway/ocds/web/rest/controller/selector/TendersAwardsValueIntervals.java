/**
 *
 */
package org.devgateway.ocds.web.rest.controller.selector;

import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
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
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author mpostelnicu
 *
 */
@RestController
@Cacheable
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
public class TendersAwardsValueIntervals extends GenericOCDSController {
    @ApiOperation(value = "Returns the min and max of tender.value.amount")
    @RequestMapping(value = "/api/tenderValueInterval", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> tenderValueInterval(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = Aggregation.newAggregation(match(where("tender.value.amount").exists(true).
                        andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                project().and("tender.value.amount").as("tender.value.amount"),
                group().min("tender.value.amount").as("minTenderValue").
                        max("tender.value.amount").as("maxTenderValue"),
                project().andInclude("minTenderValue", "maxTenderValue").andExclude(Fields.UNDERSCORE_ID));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }


    @ApiOperation(value = "Returns the min and max of awards.value.amount")
    @RequestMapping(value = "/api/awardValueInterval", method = { RequestMethod.POST,
            RequestMethod.GET }, produces = "application/json")
    public List<DBObject> awardValueInterval(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = Aggregation.newAggregation(
                unwind("awards"),
                match(where("awards.value.amount").exists(true).
                        andOperator(getYearDefaultFilterCriteria(filter, "awards.date"))),
                project().and("awards.value.amount").as("awards.value.amount"),
                group().min("awards.value.amount").as("minAwardValue")
                        .max("awards.value.amount").as("maxAwardValue"),
                project().andInclude("minAwardValue", "maxAwardValue").andExclude(Fields.UNDERSCORE_ID));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }


}
