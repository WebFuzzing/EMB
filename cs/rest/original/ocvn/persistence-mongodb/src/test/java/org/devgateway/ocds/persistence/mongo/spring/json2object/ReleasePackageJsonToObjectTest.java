package org.devgateway.ocds.persistence.mongo.spring.json2object;

import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.ReleasePackage;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author idobre
 * @since 6/1/16
 */
public class ReleasePackageJsonToObjectTest {
    @Test
    public void toObject() throws Exception {
        final String jsonReleasePackage = "{\n"
                + "    \"uri\": "
                + "\"http://standard.open-contracting.org/examples/releases/ocds-213czf-000-00001-01-planning.json\",\n"
                + "    \"publishedDate\": \"2009-03-15T14:45:00Z\",\n"
                + "    \"publisher\": {\n"
                + "        \"scheme\": \"GB-COH\",\n"
                + "        \"uid\": \"09506232\",\n"
                + "        \"name\": \"Open Data Services Co-operative Limited\",\n"
                + "        \"uri\": \"http://standard.open-contracting.org/examples/\"\n"
                + "    },\n"
                + "    \"license\": \"http://opendatacommons.org/licenses/pddl/1.0/\",\n"
                + "    \"publicationPolicy\": \"https://github.com/open-contracting/sample-data/\",\n"
                + "    \"releases\":[{\n"
                + "        \"id\": \"123\",\n"
                + "        \"tag\": [\"tender\"],\n"
                + "        \"planning\": {\n"
                + "            \"budget\": {\n"
                + "                \"description\": \"budget desc...\",\n"
                + "                \"amount\": {\n"
                + "                    \"amount\": 10000,\n"
                + "                    \"currency\": \"USD\"\n"
                + "                }\n"
                + "            }\n"
                + "        }\n"
                + "    }]\n"
                + "}";


        final JsonToObject releasePackageJsonToObject = new ReleasePackageJsonToObject(jsonReleasePackage, false);

        Assert.assertTrue("Object is a ReleasePackage",
                releasePackageJsonToObject.toObject() instanceof ReleasePackage);

        final ReleasePackage releasePackage = (ReleasePackage) releasePackageJsonToObject.toObject();

        Assert.assertEquals("Publisher uid are the same", "09506232", releasePackage.getPublisher().getUid());
        Assert.assertEquals("Check published date",
                new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:sszzzz").parse("2009-03-15T14:45:00-0000"),
                releasePackage.getPublishedDate());

        Assert.assertFalse("Release array is not empty", releasePackage.getReleases().isEmpty());
        final Release release = releasePackage.getReleases().iterator().next();

        Assert.assertEquals("Check budget amount", new BigDecimal(10000),
                release.getPlanning().getBudget().getAmount().getAmount());
        Assert.assertEquals("Check budget currency", "USD",
                release.getPlanning().getBudget().getAmount().getCurrency());
    }

}
