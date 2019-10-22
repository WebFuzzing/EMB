/**
 * 
 */
package org.devgateway.ocds.web.rest.controller;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.limit;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.skip;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.unwind;
import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import io.swagger.annotations.ApiOperation;

/**
 * @author mpostelnicu
 *
 */

@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class PercentageAwardsNarrowPublicationDates extends GenericOCDSController {

    public static final class Keys {
        public static final String TOTAL_AWARDS = "totalAwards";
        public static final String TOTAL_AWARDS_NARROW_PUBLICATION_DATES = "totalAwardsNarrowPublicationDates";
        public static final String PERCENT_NARROW_AWARD_PUBLICATION_DATES = "percentNarrowAwardPublicationDates";
        public static final String YEAR = "year";
    }

    @ApiOperation(value = "Percentage of awards where award publication date - award.date is less than 7 days."
            + " Percentage should be by year. The denominator for the percentage is the "
            + "number of awards that have both awards.date and awards.publishedDate")
    @RequestMapping(value = "/api/percentageAwardsNarrowPublicationDates",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject>
            percentageAwardsNarrowPublicationDates(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        BasicDBObject project = new BasicDBObject();
        project.put("year", new BasicDBObject("$year", "$awards.date"));
        project.put("narrowAwardPublicationDates",
                new BasicDBObject("$cond",
                        Arrays.asList(new BasicDBObject("$and", Arrays
                                .asList(new BasicDBObject("$gt", Arrays.asList("$awards.date", null)),
                                        new BasicDBObject("$gt",
                                                Arrays.asList("$awards.publishedDate",
                                                        null)),
                                        new BasicDBObject("$lt",
                                                Arrays.asList(new BasicDBObject("$divide",
                                                        Arrays.asList(
                                                                new BasicDBObject("$subtract",
                                                                        Arrays.asList("$awards.publishedDate",
                                                                                "$awards.date")),
                                                                MongoConstants.DAY_MS)),
                                                        7)))),
                                1, 0)));

        DBObject project2 = new BasicDBObject();
        project2.put(Keys.YEAR, Fields.UNDERSCORE_ID_REF);
        project2.put(Fields.UNDERSCORE_ID, 0);
        project2.put(Keys.TOTAL_AWARDS, 1);
        project2.put(Keys.TOTAL_AWARDS_NARROW_PUBLICATION_DATES, 1);
        project2.put(Keys.PERCENT_NARROW_AWARD_PUBLICATION_DATES, new BasicDBObject("$multiply", Arrays.asList(
                new BasicDBObject("$divide", Arrays.asList("$totalAwardsNarrowPublicationDates", "$totalAwards")),
                100)));

        Aggregation agg = Aggregation.newAggregation(match(where("awards.0").exists(true)), unwind("$awards"),
                match(where("awards.date").exists(true).and("awards.publishedDate").exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter, "awards.date"))),
                new CustomProjectionOperation(project),
                group("$year").count().as(Keys.TOTAL_AWARDS).sum("narrowAwardPublicationDates")
                        .as(Keys.TOTAL_AWARDS_NARROW_PUBLICATION_DATES),
                new CustomProjectionOperation(project2), sort(Direction.ASC, Fields.UNDERSCORE_ID),
                skip(filter.getSkip()), limit(filter.getPageSize()));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;
    }
}
