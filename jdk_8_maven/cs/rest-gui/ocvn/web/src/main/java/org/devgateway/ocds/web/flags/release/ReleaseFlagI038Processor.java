/**
 *
 */
package org.devgateway.ocds.web.flags.release;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.stereotype.Component;

/**
 * @author mpostelnicu
 */
@Component
public class ReleaseFlagI038Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final int MIN_ALLOWED_DAYS_BIDDING_PERIOD = 7;

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI038(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        Days daysBetween = Days.daysBetween(new DateTime(flaggable.getTender().getTenderPeriod().getStartDate()),
                new DateTime(flaggable.getTender().getTenderPeriod().getEndDate()));
        rationale.append("Days between: ").append(daysBetween.getDays()).append("; Minimum allowed days: ")
                .append(MIN_ALLOWED_DAYS_BIDDING_PERIOD).append(";");
        return daysBetween.getDays() < MIN_ALLOWED_DAYS_BIDDING_PERIOD;
    }

    @PostConstruct
    @Override
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(Arrays.asList(
                FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                FlaggedReleasePredicates.TENDER_END_DATE,
                FlaggedReleasePredicates.TENDER_START_DATE
        ));
    }

    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING));
    }

}
