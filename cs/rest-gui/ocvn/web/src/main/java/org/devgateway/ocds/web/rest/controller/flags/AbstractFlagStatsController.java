package org.devgateway.ocds.web.rest.controller.flags;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomGroupingOperation;
import org.devgateway.toolkit.persistence.mongo.aggregate.CustomProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;

/**
 * Created by mpostelnicu on 12/2/2016.
 */
public abstract class AbstractFlagStatsController extends AbstractFlagController {

    public static final class GenericKeys {
        public static final String TOTAL = "total";
        public static final String TOTAL_TRUE = "totalTrue";
        public static final String TOTAL_FALSE = "totalFalse";
        public static final String TOTAL_PRECOND_MET = "totalPrecondMet";
        public static final String PERCENT_TRUE_PRECOND_MET = "percentTruePrecondMet";
        public static final String PERCENT_FALSE_PRECOND_MET = "percentFalsePrecondMet";
        public static final String PERCENT_PRECOND_MET = "percentPrecondMet";
    }


    protected DBObject getProjectPrepare(final YearFilterPagingRequest year) {
        DBObject projectPrepare = new BasicDBObject();
        projectPrepare.put(getFlagProperty(), 1);
        addYearlyMonthlyProjection(year, projectPrepare, ref(getYearProperty()));
        return projectPrepare;
    }


    protected DBObject getGroup(final YearFilterPagingRequest filter) {
        DBObject group = new BasicDBObject();
        addYearlyMonthlyReferenceToGroup(filter, group);
        group.put(GenericKeys.TOTAL, new BasicDBObject("$sum", 1));
        group.put(GenericKeys.TOTAL_TRUE, new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$eq", Arrays.asList(ref(getFlagProperty()), true)), 1, 0))));
        group.put(GenericKeys.TOTAL_FALSE, new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$eq", Arrays.asList(ref(getFlagProperty()), false)), 1, 0))));
        group.put(GenericKeys.TOTAL_PRECOND_MET, new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$gt", Arrays.asList(ref(getFlagProperty()), null)), 1, 0))));
        return group;
    }

    protected DBObject getProjectPercentage(final YearFilterPagingRequest filter) {
        DBObject project2 = new BasicDBObject();
        if (filter.getMonthly()) {
            project2.put("year", 1);
            project2.put("month", 1);
        } else {
            project2.put(Fields.UNDERSCORE_ID, 1);
        }
        project2.put(GenericKeys.TOTAL, 1);
        project2.put(GenericKeys.TOTAL_TRUE, 1);
        project2.put(GenericKeys.TOTAL_FALSE, 1);
        project2.put(GenericKeys.TOTAL_PRECOND_MET, 1);
        project2.put(GenericKeys.PERCENT_TRUE_PRECOND_MET,
                new BasicDBObject("$cond",
                        Arrays.asList(new BasicDBObject("$eq", Arrays.asList(ref(GenericKeys.TOTAL_PRECOND_MET),
                                0)), new BasicDBObject("$literal", 0),
                                new BasicDBObject("$multiply",
                                        Arrays.asList(new BasicDBObject("$divide",
                                                Arrays.asList(ref(GenericKeys.TOTAL_TRUE),
                                                ref(GenericKeys.TOTAL_PRECOND_MET))), 100))
                        ))

        );
        project2.put(GenericKeys.PERCENT_FALSE_PRECOND_MET,
                new BasicDBObject("$cond",
                        Arrays.asList(new BasicDBObject("$eq", Arrays.asList(ref(GenericKeys.TOTAL_PRECOND_MET),
                                0)), new BasicDBObject("$literal", 0),
                                new BasicDBObject("$multiply",
                                        Arrays.asList(new BasicDBObject("$divide",
                                                Arrays.asList(ref(GenericKeys.TOTAL_FALSE),
                                                ref(GenericKeys.TOTAL_PRECOND_MET))), 100))
                        ))

        );
        project2.put(GenericKeys.PERCENT_PRECOND_MET, new BasicDBObject("$multiply",
                Arrays.asList(new BasicDBObject("$divide", Arrays.asList(ref(GenericKeys.TOTAL_PRECOND_MET),
                        ref(GenericKeys.TOTAL))), 100)));
        return project2;
    }

    public List<DBObject> flagStats(@ModelAttribute @Valid final YearFilterPagingRequest filter) {


        DBObject projectPrepare = getProjectPrepare(filter);

        DBObject group = getGroup(filter);

        DBObject projectPercentage = getProjectPercentage(filter);

        Aggregation agg = newAggregation(
                match(getYearDefaultFilterCriteria(filter, getYearProperty()).and(getYearProperty()).exists(true)),
                new CustomProjectionOperation(projectPrepare),
                new CustomGroupingOperation(group),
                new CustomProjectionOperation(projectPercentage),
                transformYearlyGrouping(filter).andInclude(GenericKeys.TOTAL, GenericKeys.TOTAL_TRUE,
                        GenericKeys.TOTAL_FALSE, GenericKeys.TOTAL_PRECOND_MET, GenericKeys.PERCENT_TRUE_PRECOND_MET,
                        GenericKeys.PERCENT_FALSE_PRECOND_MET, GenericKeys.PERCENT_PRECOND_MET)
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
}
