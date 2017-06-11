/**
 *
 */
package org.devgateway.ocds.web.spring;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.web.flags.release.vietnam.ReleaseFlagI003Processor;
import org.devgateway.ocds.web.flags.release.vietnam.VietnamReleaseFlagI004Processor;
import org.devgateway.ocds.persistence.mongo.repository.FlaggedReleaseRepository;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI007Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI019Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI038Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI077Processor;
import org.devgateway.ocds.web.flags.release.ReleaseFlagI180Processor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * @author mpostelnicu
 */
@Service
public class ReleaseFlaggingService {

    @Autowired
    private FlaggedReleaseRepository releaseRepository;

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


    public static final int FLAGGING_BATCH_SIZE = 5000;

    private Collection<AbstractFlaggedReleaseFlagProcessor> releaseFlagProcessors;


    /**
     * Trigger {@link AbstractFlaggedReleaseFlagProcessor#reInitialize()} for all processors
     */
    private void reinitialize() {
        releaseFlagProcessors.forEach(processor -> processor.reInitialize());
    }

    private void processAndSaveFlagsForRelease(FlaggedRelease release) {
        releaseFlagProcessors.forEach(processor -> processor.process(release));
        prepareStats(release);
        releaseRepository.save(release);
    }

    /**
     * Saves the stats map in a collection format that Mongo can persist easily
     *
     * @param release
     */
    private void prepareStats(FlaggedRelease release) {
        release.getFlags().setFlaggedStats(release.getFlags().getFlaggedStatsMap().values());
        release.getFlags().setEligibleStats(release.getFlags().getEligibleStatsMap().values());
    }

    public void processAndSaveFlagsForAllReleases(Consumer<String> logMessage) {

        logMessage.accept("<b>RUNNING CORRUPTION FLAGGING.</b>");

        reinitialize();

        int pageNumber = 0;
        int processedCount = 0;

        Page<FlaggedRelease> page;
        do {
            page = releaseRepository.findAll(new PageRequest(pageNumber++, FLAGGING_BATCH_SIZE));
            page.getContent().parallelStream().forEach(this::processAndSaveFlagsForRelease);
            processedCount += page.getNumberOfElements();
            logMessage.accept("Flagged " + processedCount + " releases");
        } while (!page.isLast());

        logMessage.accept("<b>CORRUPTION FLAGGING COMPLETE.</b>");
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
                releaseFlagI180Processor
        ));
    }
}
