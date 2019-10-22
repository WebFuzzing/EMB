/**
 *
 */
package org.devgateway.ocds.web.rest.controller;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.flags.ReleaseFlags;
import org.devgateway.ocds.persistence.mongo.repository.main.FlaggedReleaseRepository;
import org.devgateway.ocds.web.spring.ReleaseFlaggingService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author mpostelnicu
 */
public class ReleaseFlaggingServiceTest extends AbstractEndPointControllerTest {

    @Autowired
    private ReleaseFlaggingService releaseFlaggingService;


    @Autowired
    private FlaggedReleaseRepository flaggedReleaseRepository;

    public static void logMessage(String message) {
        logger.info(message);
    }

    @Before
    public final void setUp() throws Exception {
        super.setUp();
        releaseFlaggingService.processAndSaveFlagsForAllReleases(ReleaseFlaggingServiceTest::logMessage);
    }

    @Test
    public void testI038() {
        FlaggedRelease release1 = flaggedReleaseRepository.findByOcid("ocds-endpoint-001");
        Assert.assertNotNull(release1);
        Assert.assertEquals(false, release1.getFlags().getI038().getValue());
        Assert.assertEquals(true, release1.getFlags().getI038().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release1.getFlags().getI038().getTypes().size());

        FlaggedRelease release2 = flaggedReleaseRepository.findByOcid("ocds-endpoint-002");
        Assert.assertNotNull(release2);
        Assert.assertEquals(null, release2.getFlags().getI038().getValue());
        Assert.assertEquals(true, release2.getFlags().getI038().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release2.getFlags().getI038().getTypes().size());
    }

    @Test
    public void testI019() {
        FlaggedRelease release1 = flaggedReleaseRepository.findByOcid("ocds-endpoint-001");
        Assert.assertNotNull(release1);
        Assert.assertEquals(true, release1.getFlags().getI019().getValue());
        Assert.assertEquals(true, release1.getFlags().getI019().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release1.getFlags().getI019().getTypes().size());

        FlaggedRelease release2 = flaggedReleaseRepository.findByOcid("ocds-endpoint-002");
        Assert.assertNotNull(release2);
        Assert.assertEquals(null, release2.getFlags().getI019().getValue());
        Assert.assertEquals(true, release2.getFlags().getI019().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release2.getFlags().getI019().getTypes().size());
    }

    @Test
    public void testI007() {
        FlaggedRelease release1 = flaggedReleaseRepository.findByOcid("ocds-endpoint-001");
        Assert.assertNotNull(release1);
        Assert.assertEquals(true, release1.getFlags().getI007().getValue());
        Assert.assertEquals(true, release1.getFlags().getI007().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release1.getFlags().getI007().getTypes().size());

        FlaggedRelease release2 = flaggedReleaseRepository.findByOcid("ocds-endpoint-002");
        Assert.assertNotNull(release2);
        Assert.assertEquals(null, release2.getFlags().getI007().getValue());
        Assert.assertEquals(true, release2.getFlags().getI007().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release2.getFlags().getI007().getTypes().size());
    }

    @Test
    public void testI077() {
        FlaggedRelease release1 = flaggedReleaseRepository.findByOcid("ocds-endpoint-001");
        Assert.assertNotNull(release1);
        Assert.assertEquals(false, release1.getFlags().getI077().getValue());
        Assert.assertEquals(true, release1.getFlags().getI077().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release1.getFlags().getI077().getTypes().size());

        FlaggedRelease release2 = flaggedReleaseRepository.findByOcid("ocds-endpoint-002");
        Assert.assertNotNull(release2);
        Assert.assertEquals(false, release2.getFlags().getI077().getValue());
        Assert.assertEquals(true, release2.getFlags().getI077().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release2.getFlags().getI077().getTypes().size());
    }

    @Test
    public void testI004() {
        FlaggedRelease release1 = flaggedReleaseRepository.findByOcid("ocds-endpoint-001");
        Assert.assertNotNull(release1);
        Assert.assertEquals(null, release1.getFlags().getI004().getValue());

        FlaggedRelease release2 = flaggedReleaseRepository.findByOcid("ocds-endpoint-002");
        Assert.assertNotNull(release2);
        Assert.assertEquals(null, release2.getFlags().getI004().getValue());
    }



    @Test
    public void testI180() {
        FlaggedRelease release1 = flaggedReleaseRepository.findByOcid("ocds-endpoint-001");
        Assert.assertNotNull(release1);
        Assert.assertEquals(null, release1.getFlags().getI180().getValue());
        Assert.assertEquals(true, release1.getFlags().getI180().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release1.getFlags().getI180().getTypes().size());

        FlaggedRelease release2 = flaggedReleaseRepository.findByOcid("ocds-endpoint-002");
        Assert.assertNotNull(release2);
        Assert.assertEquals(null, release2.getFlags().getI180().getValue());
        Assert.assertEquals(true, release2.getFlags().getI180().getTypes().contains(FlagType.RIGGING));
        Assert.assertEquals(1, release2.getFlags().getI180().getTypes().size());
    }

    @Test
    public void testFlaggedEligibleTypes() {
        FlaggedRelease release1 = flaggedReleaseRepository.findByOcid("ocds-endpoint-001");
        Assert.assertNotNull(release1);
        Assert.assertEquals(2, release1.getFlags().getFlaggedStats().
                stream().filter(f -> f.getType().equals(FlagType.RIGGING)).findFirst().get().getCount(), 0);
        Assert.assertEquals(5, release1.getFlags().getEligibleStats().
                stream().filter(f -> f.getType().equals(FlagType.RIGGING)).findFirst().get().getCount(), 0);

        FlaggedRelease release2 = flaggedReleaseRepository.findByOcid("ocds-endpoint-002");
        Assert.assertNotNull(release2);

        Assert.assertEquals(false, release2.getFlags().getFlaggedStats().
                stream().filter(f -> f.getType().equals(FlagType.RIGGING)).findFirst().isPresent());

        Assert.assertEquals(1, release2.getFlags().getEligibleStats().
                stream().filter(f -> f.getType().equals(FlagType.RIGGING)).findFirst().get().getCount(), 0);
    }


    @Test
    public void testCreateStubFlagTypes() {
        ReleaseFlags stubFlagTypes = releaseFlaggingService.createStubFlagTypes();
        Assert.assertTrue(stubFlagTypes.getI038().getTypes().contains(FlagType.RIGGING));
    }
}
