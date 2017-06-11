package org.devgateway.ocds.web.flags.release;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.PostConstruct;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocds.web.rest.controller.FrequentSuppliersTimeIntervalController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author mpostelnicu
 *         <p>
 *         i077 High number of contract awards to one supplier within a given time period by a single procurement entity
 */
@Component
public class ReleaseFlagI077Processor extends AbstractFlaggedReleaseFlagProcessor {

    private static final Integer INTERVAL_DAYS = 365;
    private static final Integer MAX_AWARDS = 3;

    private ConcurrentHashMap<String, FrequentSuppliersTimeIntervalController.FrequentSuppliersTuple>
            awardsMap;

    @Autowired
    private FrequentSuppliersTimeIntervalController frequentSuppliersTimeIntervalController;

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI077(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        return flaggable.getAwards().stream().filter(award ->
                awardsMap.get(award.getId()) != null).map(award -> rationale
                .append("Award " + award.getId() + " flagged by tuple " + awardsMap.get(award.getId()) + "; "))
                .count() > 0;
    }

    /**
     * Refreshes the internal awards map used to quicksearch after award ids. This needs to be triggered
     * before each flagging process actually starts
     */
    @Override
    public void reInitialize() {
        List<FrequentSuppliersTimeIntervalController.FrequentSuppliersTuple> frequentSuppliersTimeInterval
                = frequentSuppliersTimeIntervalController.frequentSuppliersTimeInterval(getIntervalDays(),
                getMaxAwards());

        awardsMap = new ConcurrentHashMap<>();

        frequentSuppliersTimeInterval.
                forEach(tuple -> tuple.getAwardIds().forEach(awardId -> awardsMap.put(awardId, tuple)));
    }

    protected Integer getMaxAwards() {
        return MAX_AWARDS;
    }

    protected Integer getIntervalDays() {
        return INTERVAL_DAYS;
    }

    @PostConstruct
    @Override
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(
                Arrays.asList(FlaggedReleasePredicates.ACTIVE_AWARD_WITH_DATE,
                        FlaggedReleasePredicates.TENDER_PROCURING_ENTITY));

        reInitialize();

    }


    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING));
    }
}