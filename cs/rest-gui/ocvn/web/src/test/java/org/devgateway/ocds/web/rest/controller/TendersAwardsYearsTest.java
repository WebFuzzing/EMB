package org.devgateway.ocds.web.rest.controller;

import com.mongodb.DBObject;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.Fields;

import java.util.List;

/**
 * @author idobre
 * @since 9/15/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class TendersAwardsYearsTest extends AbstractEndPointControllerTest {
    @Autowired
    private TendersAwardsYears tendersAwardsYears;

    @Test
    public void tendersAwardsYears() throws Exception {
        final List<DBObject> response = tendersAwardsYears.tendersAwardsYears();

        final DBObject first = response.get(0);
        int year = (int) first.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2014, year);

        final DBObject second = response.get(1);
        year = (int) second.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2015, year);

        final DBObject third = response.get(2);
        year = (int) third.get(Fields.UNDERSCORE_ID);
        Assert.assertEquals(2016, year);
    }
}
