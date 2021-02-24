package org.devgateway.ocds.web.rest.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocds.web.rest.controller.selector.BidSelectionMethodSearchController;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.sort;
import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
@CacheConfig(keyGenerator = "genericPagingRequestKeyGenerator", cacheNames = "genericPagingRequestJson")
@Cacheable
public class TenderPriceByTypeYearController extends GenericOCDSController {

    @Autowired
    private BidSelectionMethodSearchController bidSelectionMethodSearchController;

    private static final String UNSPECIFIED = "Chưa xác định";

    public static final class Keys {
        public static final String YEAR = "year";
        public static final String TOTAL_TENDER_AMOUNT = "totalTenderAmount";
        public static final String PROCUREMENT_METHOD = "procurementMethod";
        public static final String PROCUREMENT_METHOD_DETAILS = "procurementMethodDetails";
    }

    @ApiOperation(value = "Returns the tender price by OCDS type (procurementMethod), by year. "
            + "The OCDS type is read from tender.procurementMethod. The tender price is read from "
            + "tender.value.amount")
    @RequestMapping(value = "/api/tenderPriceByProcurementMethod", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> tenderPriceByProcurementMethod(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("tender." + Keys.PROCUREMENT_METHOD, 1);
        project.put("tender.value", 1);

        Aggregation agg = newAggregation(
                match(where("awards").elemMatch(where("status").is("active")).and("tender.value").exists(true)
                        .andOperator(getYearDefaultFilterCriteria(filter,
                                MongoConstants.FieldNames.TENDER_PERIOD_START_DATE))),
                new CustomProjectionOperation(project), group("tender." + Keys.PROCUREMENT_METHOD)
                        .sum("$tender.value.amount").as(Keys.TOTAL_TENDER_AMOUNT),
                project().and(Fields.UNDERSCORE_ID).as(Keys.PROCUREMENT_METHOD).andInclude(Keys.TOTAL_TENDER_AMOUNT)
                        .andExclude(Fields.UNDERSCORE_ID),
                sort(Direction.DESC, Keys.TOTAL_TENDER_AMOUNT));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;

    }

    @ApiOperation(value = "Returns the tender price by Vietnam type (procurementMethodDetails), by year. "
            + "The OCDS type is read from tender.procurementMethodDetails. The tender price is read from "
            + "tender.value.amount")
    @RequestMapping(value = "/api/tenderPriceByBidSelectionMethod", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<DBObject> tenderPriceByBidSelectionMethod(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        DBObject project = new BasicDBObject();
        project.put("tender." + Keys.PROCUREMENT_METHOD_DETAILS, 1);
        project.put("tender.value", 1);

        Aggregation agg = newAggregation(
                match(where("awards").elemMatch(where("status").is("active")).and("tender.value").exists(true)
                        .andOperator(getYearFilterCriteria(filter, "tender.tenderPeriod.startDate"))),
                getMatchDefaultFilterOperation(filter), new CustomProjectionOperation(project),
                group("tender." + Keys.PROCUREMENT_METHOD_DETAILS).sum("$tender.value.amount")
                        .as(Keys.TOTAL_TENDER_AMOUNT),
                project().and(Fields.UNDERSCORE_ID).as(Keys.PROCUREMENT_METHOD_DETAILS)
                        .andInclude(Keys.TOTAL_TENDER_AMOUNT).andExclude(Fields.UNDERSCORE_ID),
                sort(Direction.DESC, Keys.TOTAL_TENDER_AMOUNT));

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> tagCount = results.getMappedResults();
        return tagCount;

    }

    @ApiOperation(value = "Same as /api/tenderPriceByBidSelectionMethod, but it always returns "
            + "all bidSelectionMethods (it adds the missing bid selection methods with zero totals")
    @RequestMapping(value = "/api/tenderPriceByAllBidSelectionMethods",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<DBObject>
            tenderPriceByAllBidSelectionMethods(@ModelAttribute @Valid final YearFilterPagingRequest filter) {

        List<DBObject> tenderPriceByBidSelectionMethod = tenderPriceByBidSelectionMethod(filter);

        // create a treeset ordered by procurment method details key
        Collection<DBObject> ret = new TreeSet<>((DBObject o1, DBObject o2) -> o1.get(Keys.PROCUREMENT_METHOD_DETAILS)
                .toString().compareTo(o2.get(Keys.PROCUREMENT_METHOD_DETAILS).toString()));

        // add them all to sorted set
        for (DBObject o : tenderPriceByBidSelectionMethod) {
            if (o.containsField(Keys.PROCUREMENT_METHOD_DETAILS) && o.get(Keys.PROCUREMENT_METHOD_DETAILS) != null) {
                ret.add(o);
            } else {
                o.put(Keys.PROCUREMENT_METHOD_DETAILS, UNSPECIFIED);
                ret.add(o);
            }
        }

        // get all the non null bid selection methods
        Set<Object> bidSelectionMethods = bidSelectionMethodSearchController.bidSelectionMethods().stream()
                .filter(e -> e.get(Fields.UNDERSCORE_ID) != null).map(e -> e.get(Fields.UNDERSCORE_ID))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        bidSelectionMethods.add(UNSPECIFIED);

        // remove elements that already are in the result
        bidSelectionMethods
                .removeAll(ret.stream().map(e -> e.get(Keys.PROCUREMENT_METHOD_DETAILS)).collect(Collectors.toSet()));

        // add the missing procurementmethoddetails with zero amounts
        bidSelectionMethods.forEach(e -> {
            DBObject obj = new BasicDBObject(Keys.PROCUREMENT_METHOD_DETAILS, e.toString());
            obj.put(Keys.TOTAL_TENDER_AMOUNT, BigDecimal.ZERO);
            ret.add(obj);
        });

        return new ArrayList<>(ret);
    }

}
