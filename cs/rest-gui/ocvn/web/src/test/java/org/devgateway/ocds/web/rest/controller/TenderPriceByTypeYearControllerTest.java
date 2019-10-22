package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TenderPriceByTypeYearControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private TenderPriceByTypeYearController tenderPriceByTypeYearController;

    @Test
    public void tenderPriceByProcurementMethod() throws Exception {
        final List<DBObject> tenderPriceByProcurementMethod = tenderPriceByTypeYearController
                .tenderPriceByProcurementMethod(new YearFilterPagingRequest());

        final DBObject first = tenderPriceByProcurementMethod.get(0);
        String procurementMethod = (String) first.get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD);
        Number totalTenderAmount = (Number) first.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
        Assert.assertEquals("selective", procurementMethod);
        Assert.assertEquals(600000.0, totalTenderAmount.doubleValue(), 0);

        final DBObject second = tenderPriceByProcurementMethod.get(1);
        procurementMethod = (String) second.get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD);
        totalTenderAmount = (Number) second.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
        Assert.assertEquals("open", procurementMethod);
        Assert.assertEquals(9000.0, totalTenderAmount.doubleValue(), 0);
    }

    @Test
    public void tenderPriceByBidSelectionMethod() throws Exception {
        final List<DBObject> tenderPriceByBidSelectionMethod = tenderPriceByTypeYearController
                .tenderPriceByBidSelectionMethod(new YearFilterPagingRequest());

        final DBObject first = tenderPriceByBidSelectionMethod.get(0);
        Number totalTenderAmount = (Number) first.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
        String procurementMethodDetails = (String) first
                .get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS);
        Assert.assertNull(procurementMethodDetails);
        Assert.assertEquals(600000.0, totalTenderAmount.doubleValue(), 0);

        final DBObject second = tenderPriceByBidSelectionMethod.get(1);
        totalTenderAmount = (Number) second.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
        procurementMethodDetails = (String) second.get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS);
        Assert.assertEquals("Competitive offers", procurementMethodDetails);
        Assert.assertEquals(9000.0, totalTenderAmount.doubleValue(), 0);
    }

    @Test
    public void tenderPriceByAllBidSelectionMethods() throws Exception {
        final List<DBObject> tenderPriceByAllBidSelectionMethods = tenderPriceByTypeYearController
                .tenderPriceByAllBidSelectionMethods(new YearFilterPagingRequest());

        final DBObject first = tenderPriceByAllBidSelectionMethods.get(1);
        Number totalTenderAmount = (Number) first.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
        String procurementMethodDetails = (String) first
                .get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS);
        Assert.assertEquals("Competitive offers", procurementMethodDetails);
        Assert.assertEquals(9000.0, totalTenderAmount.doubleValue(), 0);

        final DBObject second = tenderPriceByAllBidSelectionMethods.get(0);
        totalTenderAmount = (Number) second.get(TenderPriceByTypeYearController.Keys.TOTAL_TENDER_AMOUNT);
        procurementMethodDetails = (String) second.get(TenderPriceByTypeYearController.Keys.PROCUREMENT_METHOD_DETAILS);
        Assert.assertEquals("Chưa xác định", procurementMethodDetails);
        Assert.assertEquals(600000.0, totalTenderAmount.doubleValue(), 0);
    }
}

