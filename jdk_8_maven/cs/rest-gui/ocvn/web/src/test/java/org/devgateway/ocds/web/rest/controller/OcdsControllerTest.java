package org.devgateway.ocds.web.rest.controller;

import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.ReleasePackage;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

/**
 * @author idobre
 * @since 9/13/16
 *
 * @see {@link AbstractEndPointControllerTest}
 */
public class OcdsControllerTest extends AbstractEndPointControllerTest {
    @Autowired
    private OcdsController ocdsController;

    @Test
    public void ocdsByProjectId() throws Exception {
        final Release release = ocdsController.ocdsByProjectId("SP001");

        Assert.assertNotNull(release);
        Assert.assertEquals("ocds-endpoint-001", release.getOcid());
    }

    @Test
    public void ocdsByOcid() throws Exception {
        final Release release = ocdsController.ocdsByOcid("ocds-endpoint-001");

        Assert.assertNotNull(release);
        Assert.assertEquals("ocds-endpoint-001-tender", release.getTender().getId());
    }

    @Test
    public void ocdsPackageByOcid() throws Exception {
        final ReleasePackage releasePackage = ocdsController.ocdsPackageByOcid("ocds-endpoint-001");

        Assert.assertNotNull(releasePackage);
        final Optional<Release> release = releasePackage.getReleases().stream().findFirst();
        Assert.assertEquals(true, release.isPresent());
        Assert.assertEquals("ocds-endpoint-001", release.get().getOcid());
    }

    @Test
    public void createReleasePackage() throws Exception {
        final ReleasePackage releasePackage = ocdsController.createReleasePackage(
                ocdsController.ocdsByOcid("ocds-endpoint-001"));

        Assert.assertNotNull(releasePackage);
        final Optional<Release> release = releasePackage.getReleases().stream().findFirst();
        Assert.assertEquals(true, release.isPresent());
        Assert.assertEquals("ocds-endpoint-001", release.get().getOcid());
    }

    @Test
    public void packagedReleaseByProjectId() throws Exception {
        final ReleasePackage releasePackage = ocdsController.packagedReleaseByProjectId("SP001");

        Assert.assertNotNull(releasePackage);
        final Optional<Release> release = releasePackage.getReleases().stream().findFirst();
        Assert.assertEquals(true, release.isPresent());
        Assert.assertEquals("ocds-endpoint-001-tender", release.get().getTender().getId());
    }

    @Test
    public void ocdsReleases() throws Exception {
        final List<Release> releases = ocdsController.ocdsReleases(new YearFilterPagingRequest());
        Assert.assertEquals(3, releases.size());
    }

    @Test
    public void ocdsPackages() throws Exception {
        final List<ReleasePackage> releasePackages = ocdsController.ocdsPackages(new YearFilterPagingRequest());

        Assert.assertEquals(3, releasePackages.size());
    }
}
