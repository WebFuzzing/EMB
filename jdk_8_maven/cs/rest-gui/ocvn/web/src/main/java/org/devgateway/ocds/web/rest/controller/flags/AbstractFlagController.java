package org.devgateway.ocds.web.rest.controller.flags;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;

import java.util.Arrays;

/**
 * Created by mpostelnicu on 12/2/2016.
 */
public abstract class AbstractFlagController extends GenericOCDSController {

    protected abstract String getFlagProperty();


    /**
     * Creates Group function that performs sum on conditional boolean of type value given.
     *
     * @param property
     * @param value
     * @return
     */
    protected DBObject groupSumBoolean(String property, Boolean value) {
        return new BasicDBObject("$sum", new BasicDBObject("$cond",
                Arrays.asList(new BasicDBObject("$eq", Arrays.asList(ref(property), value)), 1, 0)));
    }


    protected String getYearProperty() {
        return MongoConstants.FieldNames.TENDER_PERIOD_START_DATE;
    }

}
