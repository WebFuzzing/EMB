package org.devgateway.ocds.web.rest.controller.flags;

import org.devgateway.ocds.persistence.mongo.constants.MongoConstants;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;

/**
 * Created by mpostelnicu on 12/2/2016.
 */
public abstract class AbstractFlagController extends GenericOCDSController {

    protected abstract String getFlagProperty();


    protected String getYearProperty() {
        return MongoConstants.FieldNames.TENDER_PERIOD_START_DATE;
    }

}
