package org.devgateway.ocds.web.rest.controller.flags;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.devgateway.ocds.persistence.mongo.flags.FlagsConstants;
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
public abstract class AbstractFlagCrosstabController extends AbstractFlagController {

    public static final class GenericKeys {
        public static final String PERCENT = "percent";


    }

    public String getFlagDesignation(String flagProperty) {
        return flagProperty.substring(6, flagProperty.length() - 6);
    }


    protected DBObject getProjectPrepare(final YearFilterPagingRequest year) {
        DBObject projectPrepare = new BasicDBObject();
        projectPrepare.put("flags", 1);
        return projectPrepare;
    }


    protected DBObject getGroup(final YearFilterPagingRequest filter) {
        DBObject group = new BasicDBObject();
        group.put(Fields.UNDERSCORE_ID, null);
        FlagsConstants.FLAGS_LIST.forEach(f -> group.put(getFlagDesignation(f), groupSumBoolean(f, true)));
        return group;
    }

    protected DBObject getProjectPercentage(final YearFilterPagingRequest filter) {
        DBObject project2 = new BasicDBObject();

        project2.put(Fields.UNDERSCORE_ID, 0);
        FlagsConstants.FLAGS_LIST.forEach(f -> {
            project2.put(getFlagDesignation(f), 1);
            project2.put(GenericKeys.PERCENT + "." + getFlagDesignation(f),
                    new BasicDBObject("$cond",
                            Arrays.asList(new BasicDBObject("$eq",
                                            Arrays.asList(ref(getFlagDesignation(getFlagProperty())),
                                                    0)), new BasicDBObject("$literal", 0),
                                    new BasicDBObject("$multiply",
                                            Arrays.asList(new BasicDBObject("$divide",
                                                    Arrays.asList(ref(getFlagDesignation(f)),
                                                            ref(getFlagDesignation(getFlagProperty())))), 100))
                            ))

            );


        });


        return project2;
    }

    protected List<DBObject> flagStats(@ModelAttribute @Valid final YearFilterPagingRequest filter) {


        DBObject projectPrepare = getProjectPrepare(filter);

        DBObject group = getGroup(filter);

        DBObject projectPercentage = getProjectPercentage(filter);

        Aggregation agg = newAggregation(
                match(getYearDefaultFilterCriteria(filter, getYearProperty())
                        .and(getYearProperty()).exists(true)
                        .and(getFlagProperty()).is(true)),
                new CustomProjectionOperation(projectPrepare),
                new CustomGroupingOperation(group),
                new CustomProjectionOperation(projectPercentage)
        );

        AggregationResults<DBObject> results = mongoTemplate.aggregate(agg, "release", DBObject.class);
        List<DBObject> list = results.getMappedResults();
        return list;
    }
}
