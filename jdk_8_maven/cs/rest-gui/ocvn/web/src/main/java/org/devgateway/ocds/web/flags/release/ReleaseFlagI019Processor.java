package org.devgateway.ocds.web.flags.release;


import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.devgateway.ocds.persistence.mongo.Award;
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
 *         <p>
 *         i019 Long delays in contract negotiations or award
 *         (as bribe demands are possibly negotiated)
 */
@Component
public class ReleaseFlagI019Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final int MAX_ALLOWED_DAYS_TENDER_END_DATE_AWARD_DATE = 60;

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI019(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        Optional<Award> award = flaggable.getAwards().stream().filter(a -> a.getDate() != null
                && Award.Status.active.equals(a.getStatus())).findFirst();
        if (!award.isPresent()) {
            return false;
        }

        Days daysBetween = Days.daysBetween(new DateTime(flaggable.getTender().getTenderPeriod().getEndDate()),
                new DateTime(award.get().getDate()));
        rationale.append("Days between: ").append(daysBetween.getDays()).append("; Max allowed days: ")
                .append(MAX_ALLOWED_DAYS_TENDER_END_DATE_AWARD_DATE).append(";");
        return daysBetween.getDays() > MAX_ALLOWED_DAYS_TENDER_END_DATE_AWARD_DATE;
    }


    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING));
    }

    @Override
    @PostConstruct
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(Arrays.asList(
                FlaggedReleasePredicates.ACTIVE_AWARD_WITH_DATE,
                FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                FlaggedReleasePredicates.TENDER_END_DATE
        ));
    }
}
