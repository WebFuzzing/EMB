package org.devgateway.ocds.persistence.mongo.test;

import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.toolkit.persistence.mongo.AbstractMongoTest;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class ReleaseRepositoryTest extends AbstractMongoTest {

    private String ocid = "release-X";

    @Autowired
    private ReleaseRepository releaseRepository;

    @Test
    public void testReleaseSaveAndFind() {
        final Release release = new Release();

        release.setOcid(ocid);
        releaseRepository.insert(release);

        final Release byOcid = releaseRepository.findByOcid(ocid);
        Assert.assertEquals(ocid, byOcid.getOcid());
    }
}
