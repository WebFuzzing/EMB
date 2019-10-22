package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Fields;

import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TendersByItemClassificationTest extends AbstractEndPointControllerTest {
    @Autowired
    private TendersByItemClassification tendersByItemClassification;

    @Test
    public void tendersByItemClassification() throws Exception {
        final List<DBObject> numberOfTendersByItem = tendersByItemClassification
                .tendersByItemClassification(new YearFilterPagingRequest());

        final DBObject first = numberOfTendersByItem.get(0);
        String id = (String) first.get(Fields.UNDERSCORE_ID);
        String description = (String) first.get(TendersByItemClassification.Keys.DESCRIPTION);
        int totalTenders = (int) first.get(TendersByItemClassification.Keys.TOTAL_TENDERS);
        double totalTenderAmount = (double) first.get(TendersByItemClassification.Keys.TOTAL_TENDER_AMOUNT);
        Assert.assertEquals("09100000", id);
        Assert.assertEquals("Fuels", description);
        Assert.assertEquals(1, totalTenders);
        Assert.assertEquals(9000.0, totalTenderAmount,0);

        final DBObject second = numberOfTendersByItem.get(1);
        id = (String) second.get(Fields.UNDERSCORE_ID);
        description = (String) second.get(TendersByItemClassification.Keys.DESCRIPTION);
        totalTenders = (int) second.get(TendersByItemClassification.Keys.TOTAL_TENDERS);
        totalTenderAmount = (double) second.get(TendersByItemClassification.Keys.TOTAL_TENDER_AMOUNT);
        Assert.assertEquals("45233130", id);
        Assert.assertEquals("Construction work for highways", description);
        Assert.assertEquals(2, totalTenders);
        Assert.assertEquals(1000000, totalTenderAmount, 0);
    }
}
