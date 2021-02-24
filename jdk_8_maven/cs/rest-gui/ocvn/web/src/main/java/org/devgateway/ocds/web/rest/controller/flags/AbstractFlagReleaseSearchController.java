package org.devgateway.ocds.web.rest.controller.flags;

import com.fasterxml.jackson.annotation.JsonView;
import com.mongodb.DBObject;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.persistence.mongo.spring.json.Views;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Created by mpostelnicu on 12/2/2016.
 */
public abstract class AbstractFlagReleaseSearchController extends AbstractFlagController {


    @JsonView(Views.Internal.class)
    public List<DBObject> releaseFlagSearch(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        Aggregation agg = newAggregation(
                match(where("flags.flaggedStats.0").exists(true).and(getFlagProperty()).is(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                unwind("flags.flaggedStats"),
                match(where(getFlagProperty()).is(true)),
                project("ocid", "tender.procuringEntity.name", "tender.tenderPeriod", "flags",
                        "tender.title", "tag")
                        .and("tender.value").as("tender.value").and("awards.value").as("awards.value")
                        .andExclude(Fields.UNDERSCORE_ID),
                sort(Sort.Direction.DESC, "flags.flaggedStats.count"),
                skip(filter.getSkip()),
                limit(filter.getPageSize())
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release",
                DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
}
