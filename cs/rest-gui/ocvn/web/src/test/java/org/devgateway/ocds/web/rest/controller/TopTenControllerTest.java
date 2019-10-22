package org.devgateway.ocds.web.rest.controller;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TopTenControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private TopTenController topTenController;

    @Test
    public void topTenLargestAwards() throws Exception {
        final List<DBObject> topTenLargestAwards = topTenController
                .topTenLargestAwards(new YearFilterPagingRequest());

        Assert.assertEquals(2, topTenLargestAwards.size());

        final DBObject first = topTenLargestAwards.get(0);
        BasicDBObject awards = (BasicDBObject) first.get("awards");
        BasicDBObject value = (BasicDBObject) awards.get("value");
        double amount = (double) value.get("amount");
        Assert.assertEquals(6000000.0, amount, 0);

        final DBObject second = topTenLargestAwards.get(1);
        awards = (BasicDBObject) second.get("awards");
        value = (BasicDBObject) awards.get("value");
        amount = (double) value.get("amount");
        Assert.assertEquals(40000.0, amount, 0);
    }

    @Test
    public void topTenLargestTenders() throws Exception {
        final List<DBObject> topTenLargestTenders = topTenController
                .topTenLargestTenders(new YearFilterPagingRequest());

        Assert.assertEquals(3, topTenLargestTenders.size());

        final DBObject first = topTenLargestTenders.get(0);
        BasicDBObject tender = (BasicDBObject) first.get("tender");
        BasicDBObject value = (BasicDBObject) tender.get("value");
        double amount = (double) value.get("amount");
        Assert.assertEquals(600000.0, amount, 0);

        final DBObject second = topTenLargestTenders.get(1);
        tender = (BasicDBObject) second.get("tender");
        value = (BasicDBObject) tender.get("value");
        amount = (double) value.get("amount");
        Assert.assertEquals(400000.0, amount, 0);

        final DBObject third = topTenLargestTenders.get(2);
        tender = (BasicDBObject) third.get("tender");
        value = (BasicDBObject) tender.get("value");
        amount = (double) value.get("amount");
        Assert.assertEquals(9000.0, amount, 0);
    }


    @Test
    public void topTenLargestSuppliers() throws Exception {
        final List<DBObject> topTenLargestSuppliers = topTenController
                .topTenLargestSuppliers(new YearFilterPagingRequest());


        final DBObject first = topTenLargestSuppliers.get(0);
        Assert.assertEquals(6000000d, first.get(TopTenController.Keys.TOTAL_AWARD_AMOUNT));
        Assert.assertEquals(1, first.get(TopTenController.Keys.TOTAL_CONTRACTS));
        Assert.assertEquals("E09000005",
                ((Collection) first.get(TopTenController.Keys.PROCURING_ENTITY_IDS)).iterator().next());
        Assert.assertEquals("GB-COH-1234567845", first.get(TopTenController.Keys.SUPPLIER_ID));
        Assert.assertEquals(1, first.get(TopTenController.Keys.PROCURING_ENTITY_IDS_COUNT));

        Assert.assertEquals(2, topTenLargestSuppliers.size());

    }
}
