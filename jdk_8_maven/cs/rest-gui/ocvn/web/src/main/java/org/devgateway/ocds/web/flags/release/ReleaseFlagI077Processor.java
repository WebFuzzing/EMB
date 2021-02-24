package org.devgateway.ocds.web.flags.release;


import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocds.web.rest.controller.FrequentSuppliersTimeIntervalController;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mpostelnicu
 *         <p>
 *         i077 High number of contract awards to one supplier within a given time period by a single procurement entity
 */
@Component
public class ReleaseFlagI077Processor extends AbstractFlaggedReleaseFlagProcessor {

    private static final Integer INTERVAL_DAYS = 365;
    private static final Integer MAX_AWARDS = 3;

    private ConcurrentHashMap<String, FrequentSuppliersTimeIntervalController.FrequentSuppliersResponse>
            frequentSuppliersMap;

    private Date now;
    private Double nowDouble;

    @Autowired
    private FrequentSuppliersTimeIntervalController frequentSuppliersTimeIntervalController;

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI077(flag);
    }

    protected Integer getInterval(Date awardDate) {
        return new Double(Math.ceil((nowDouble - awardDate.getTime())
                / GenericOCDSController.DAY_MS / INTERVAL_DAYS)).intValue();
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        return flaggable.getAwards().stream().filter(award -> award.getSuppliers().stream().anyMatch(supplier ->
                flaggable.getTender() != null && flaggable.getTender().getProcuringEntity() != null
                        && supplier != null
                        && frequentSuppliersMap.containsKey(FrequentSuppliersTimeIntervalController.
                        getFrequentSuppliersResponseKey(flaggable.getTender().getProcuringEntity().getId(),
                                supplier.getId(), getInterval(award.getDate()))
                ))
        ).map(award -> rationale
                .append("Award ").append(award.getId()).append(" flagged; "))
                .count() > 0;
    }

    /**
     * Refreshes the internal awards map used to quicksearch after award ids. This needs to be triggered
     * before each flagging process actually starts
     */
    @Override
    public void reInitialize() {
        now = new Date();
        nowDouble = new Double(now.getTime());
        List<FrequentSuppliersTimeIntervalController.FrequentSuppliersResponse> frequentSuppliersTimeInterval
                = frequentSuppliersTimeIntervalController.frequentSuppliersTimeInterval(getIntervalDays(),
                getMaxAwards(), now);

        frequentSuppliersMap = new ConcurrentHashMap<>();

        frequentSuppliersTimeInterval.
                forEach(response -> frequentSuppliersMap.put(
                        FrequentSuppliersTimeIntervalController.getFrequentSuppliersResponseKey(response), response));
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
                        FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD
                                .or(FlaggedReleasePredicates.SELECTIVE_PROCUREMENT_METHOD)));
    }


    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING));
    }
}