/**
 *
 */
package org.devgateway.ocds.web.spring;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;
import javax.annotation.PostConstruct;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.ReleaseFlags;
import org.devgateway.ocds.persistence.mongo.repository.shadow.ShadowFlaggedReleaseRepository;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI002Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI007Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI019Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI038Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI077Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI085Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI171Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI180Processor;
import org.devgateway.ocds.web.flags.release.vietnam.ReleaseFlagI003Processor;
import org.devgateway.ocds.web.flags.release.vietnam.VietnamReleaseFlagI004Processor;
import org.devgateway.toolkit.persistence.mongo.spring.MongoUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


import static org.springframework.data.mongodb.core.query.Criteria.where;


/**
 * @author mpostelnicu
 */
@Service
public class ReleaseFlaggingService {


    protected static Logger logger = Logger.getLogger(ReleaseFlaggingService.class);
    @Autowired
    @Qualifier("shadowMongoTemplate")
    private MongoTemplate mongoTemplate;
    @Autowired
    private ShadowFlaggedReleaseRepository shadowFlaggedReleaseRepository;
    @Autowired
    private ReleaseFlagI038Processor releaseFlagI038Processor;
    @Autowired
    private ReleaseFlagI007Processor releaseFlagI007Processor;
    @Autowired
    private ReleaseFlagI019Processor releaseFlagI019Processor;
    @Autowired
    private ReleaseFlagI077Processor releaseFlagI077Processor;

    @Autowired
    private ReleaseFlagI003Processor releaseFlagI003Processor;

    @Autowired
    private VietnamReleaseFlagI004Processor vietnamReleaseFlagI004Processor;

    @Autowired
    private ReleaseFlagI180Processor releaseFlagI180Processor;
    @Autowired
    private ReleaseFlagI002Processor releaseFlagI002Processor;
    @Autowired
    private ReleaseFlagI085Processor releaseFlagI085Processor;
    @Autowired
    private ReleaseFlagI171Processor releaseFlagI171Processor;

    private Collection<AbstractFlaggedReleaseFlagProcessor> releaseFlagProcessors;

    public void logMessage(String message) {
        logger.info(message);
    }


    /**
     * Trigger {@link AbstractFlaggedReleaseFlagProcessor#reInitialize()} for all processors
     */
    private void reinitialize() {
        releaseFlagProcessors.forEach(processor -> processor.reInitialize());
    }

    private void processAndSaveFlagsForRelease(FlaggedRelease release) {
        releaseFlagProcessors.forEach(processor -> processor.process(release));
        prepareStats(release);
        //shadowFlaggedReleaseRepository.save(release);
        mongoTemplate.updateFirst(Query.query(where("_id").is(release.getId())),
                Update.update("flags", release.getFlags()),
                FlaggedRelease.class);
    }

    /**
     * Saves the stats map in a collection format that Mongo can persist easily
     *
     * @param release
     */
    private void prepareStats(FlaggedRelease release) {
        release.getFlags().setFlaggedStats(release.getFlags().getFlaggedStatsMap().values());
        release.getFlags().setEligibleStats(release.getFlags().getEligibleStatsMap().values());
        release.getFlags().setTotalFlagged(release.getFlags().getFlagCnt());
    }

    public void processAndSaveFlagsForAllReleases(Consumer<String> logMessage) {

        logMessage.accept("<b>RUNNING CORRUPTION FLAGGING.</b>");

        reinitialize();

        MongoUtil.processRepositoryItemsPaginated(shadowFlaggedReleaseRepository, this::processAndSaveFlagsForRelease,
                this::logMessage);

        logMessage.accept("<b>CORRUPTION FLAGGING COMPLETE.</b>");
    }

    /**
     * Sets flags on top of a stub empty release. This is just to populate the flags property.
     *
     * @return the flags property of {@link FlaggedRelease}
     */
    public ReleaseFlags createStubFlagTypes() {
        FlaggedRelease fr = new FlaggedRelease();
        releaseFlagProcessors.forEach(processor -> processor.process(fr));
        return fr.getFlags();
    }

    @PostConstruct
    protected void setProcessors() {
        releaseFlagProcessors = Collections.unmodifiableList(Arrays.asList(
                releaseFlagI038Processor,
                releaseFlagI003Processor,
                releaseFlagI007Processor,
                vietnamReleaseFlagI004Processor,
                releaseFlagI019Processor,
                releaseFlagI077Processor,
                releaseFlagI180Processor,
                releaseFlagI002Processor,
                releaseFlagI085Processor,
                releaseFlagI171Processor
        ));

     //processAndSaveFlagsForAllReleases(this::logMessage);
    }
}
