package org.devgateway.ocds.persistence.mongo.spring.json2object;

import org.devgateway.ocds.persistence.mongo.Release;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * @author idobre
 * @since 5/31/16
 */
public class ReleaseJsonToObjectTest {
    @Test
    public void toObject() throws Exception {
        final String jsonRelease = "{\n"
                + "    id: \"123\",\n"
                + "    tag: [\"tender\"],\n"
                + "    planning: {\n"
                + "        budget: {\n"
                + "            description: \"budget desc...\",\n"
                + "            amount: {\n"
                + "                amount: 10000,\n"
                + "                currency: \"USD\"\n"
                + "            }\n"
                + "        }\n"
                + "    }\n"
                + "}";

        final JsonToObject releaseJsonToObject = new ReleaseJsonToObject(jsonRelease, false);

        Assert.assertTrue("Object is a Release", releaseJsonToObject.toObject() instanceof Release);

        final Release release = (Release) releaseJsonToObject.toObject();

        Assert.assertEquals("IDs are the same", "123", release.getId());
        Assert.assertEquals("Check budget amount", new BigDecimal(10000),
                release.getPlanning().getBudget().getAmount().getAmount());
        Assert.assertEquals("Check budget currency", "USD",
                release.getPlanning().getBudget().getAmount().getCurrency());
    }

    @Test(expected = IOException.class)
    public void toObjectInvalidJson() throws Exception {
        final String invalidJsonRelease = "{\n"
                + "    id: \"123\",\n"
                + "    tag: [\"tenderrrrrr\"],\n"
                + "    tag: [\"award\"],\n"
                + "}";

        final JsonToObject invalidJsonToObject = new ReleaseJsonToObject(invalidJsonRelease, false);

        invalidJsonToObject.toObject();
    }

    @Test
    public void toObjectFromFile() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("json/release-json-to-object-test.json").getFile());

        final JsonToObject releaseJsonToObject = new ReleaseJsonToObject(file, false);

        Assert.assertTrue("Object is a Release", releaseJsonToObject.toObject() instanceof Release);

        final Release release = (Release) releaseJsonToObject.toObject();

        Assert.assertEquals("IDs are the same", "12345", release.getId());
        Assert.assertEquals("Check budget amount", new BigDecimal(10000),
                release.getPlanning().getBudget().getAmount().getAmount());
        Assert.assertEquals("Check budget currency", "RON",
                release.getPlanning().getBudget().getAmount().getCurrency());
    }
}
