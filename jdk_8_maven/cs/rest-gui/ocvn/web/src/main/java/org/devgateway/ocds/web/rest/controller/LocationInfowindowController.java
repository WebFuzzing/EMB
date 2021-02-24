package org.devgateway.ocds.web.rest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.spring.json.Views;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by mpostelnicu on 2/9/17.
 */
@RestController
public class LocationInfowindowController extends GenericOCDSController {

    @ApiOperation(value = "Displays the tenders filtered by location. See the location filter "
            + "for the options to filter by")
    @RequestMapping(value = "/api/tendersByLocation", method = {RequestMethod.POST,
            RequestMethod.GET}, produces = "application/json")
    @JsonView(Views.Public.class)
    public List<DBObject> tendersByLocation(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(where("tender").exists(true).orOperator(
                        where("tender.items.deliveryLocation._id").exists(true),
                        where("planning.budget.projectLocation._id").exists(true)
                )
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                project("tender").andExclude(Fields.UNDERSCORE_ID),
                skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

    @ApiOperation(value = "Displays the planning items, filtered by location. See the location filter "
            + "for the options to filter by")
    @RequestMapping(value = "/api/planningByLocation", method = {RequestMethod.POST,
            RequestMethod.GET}, produces = "application/json")
    @JsonView(Views.Public.class)
    public List<DBObject> planningByLocation(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(where("planning.budget").exists(true).orOperator(
                        where("tender.items.deliveryLocation._id").exists(true),
                        where("planning.budget.projectLocation._id").exists(true)
                )
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                "planning.bidPlanProjectDateApprove"))),
                project("planning").andExclude(Fields.UNDERSCORE_ID),
                skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }

    @ApiOperation(value = "Displays the awards, filtered by location. See the location filter "
            + "for the options to filter by")
    @RequestMapping(value = "/api/awardsByLocation", method = {RequestMethod.POST,
            RequestMethod.GET}, produces = "application/json")
    @JsonView(Views.Public.class)
    public List<DBObject> awardsByLocation(
            @ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(where("awards.0").exists(true).orOperator(
                        where("tender.items.deliveryLocation._id").exists(true),
                        where("planning.budget.projectLocation._id").exists(true)
                )
                        .andOperator(getYearDefaultFilterCriteria(filter, "awards.date"))),
                project("awards").andExclude(Fields.UNDERSCORE_ID),
                unwind("awards"),
                skip(filter.getSkip()), limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }
}
