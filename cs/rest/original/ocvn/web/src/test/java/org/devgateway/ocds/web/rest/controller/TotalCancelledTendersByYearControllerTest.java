package org.devgateway.ocds.web.rest.controller;

import java.util.List;

import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.DBObject;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TotalCancelledTendersByYearControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private TotalCancelledTendersByYearController totalCancelledTendersByYearController;

    @Test
    public void totalCancelledTendersByYear() throws Exception {
        final List<DBObject> totalCancelledTendersByYear = totalCancelledTendersByYearController
                .totalCancelledTendersByYear(new YearFilterPagingRequest());

        // no cancelled tenders
        Assert.assertEquals(0, totalCancelledTendersByYear.size());
    }

    @Test
    public void totalCancelledTendersByYearByRationale() throws Exception {
        final List<DBObject> totalCancelledTendersByYearByRationale = totalCancelledTendersByYearController
                .totalCancelledTendersByYearByRationale(new YearFilterPagingRequest());

        // no cancelled tenders
        Assert.assertEquals(0, totalCancelledTendersByYearByRationale.size());
    }
}
