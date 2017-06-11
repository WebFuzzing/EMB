package org.devgateway.ocds.web.rest.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author mpostelnicu
 * @since 01/13/2017
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class FrequentTenderersControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private FrequentTenderersController frequentTenderersController;

    @Test
    public void frequentTenderersTest() throws Exception {
        final List<FrequentTenderersController.ValueObject> frequentTenderers = frequentTenderersController
                .frequentTenderers(new YearFilterPagingRequest());

        Assert.assertEquals(0, frequentTenderers.size());
    }

}
