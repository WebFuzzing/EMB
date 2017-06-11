package org.devgateway.ocds.web.rest.controller;

import java.util.List;

import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DBObject;

/**
 * @author idobre
 * @since 9/12/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class AverageNumberOfTenderersControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private AverageNumberOfTenderersController averageNumberOfTenderersController;

    @Test
    public void averageNumberOfTenderers() throws Exception {
        final List<DBObject> averageNumberOfTenderers = averageNumberOfTenderersController
                .averageNumberOfTenderers(new YearFilterPagingRequest());

        final DBObject sec = averageNumberOfTenderers.get(1);
        int year = (int) sec.get(AverageNumberOfTenderersController.Keys.YEAR);
        double averageNoTenderers = (double) sec.get(AverageNumberOfTenderersController.Keys.AVERAGE_NO_OF_TENDERERS);
        Assert.assertEquals(2015, year);
        Assert.assertEquals(5.5, averageNoTenderers, 0);

        final DBObject first = averageNumberOfTenderers.get(0);
        year = (int) first.get(AverageNumberOfTenderersController.Keys.YEAR);
        averageNoTenderers = (double) first.get(AverageNumberOfTenderersController.Keys.AVERAGE_NO_OF_TENDERERS);
        Assert.assertEquals(2014, year);
        Assert.assertEquals(5.0, averageNoTenderers, 0);
    }
}
