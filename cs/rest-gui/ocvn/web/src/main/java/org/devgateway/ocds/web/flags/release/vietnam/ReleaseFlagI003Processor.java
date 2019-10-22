package org.devgateway.ocds.web.flags.release.vietnam;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.devgateway.ocvn.persistence.mongo.dao.VNAward;
import org.springframework.stereotype.Component;

/**
 * @author mpostelnicu
 * 
 * i003 Only winning bidder was eligible for a tender that had multiple bidders.
 */
@Component
public class ReleaseFlagI003Processor extends AbstractFlaggedReleaseFlagProcessor {


    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI003(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        long eligibleUnsuccessfulAwards = flaggable.getAwards().stream().map(a -> (VNAward) a)
                .filter(a -> Award.Status.unsuccessful.equals(a.getStatus()) && !"Y".equals(a.getIneligibleYN()))
                .count();
        rationale.append("Number of eligible unsuccessful awards: ").append(eligibleUnsuccessfulAwards);
        return eligibleUnsuccessfulAwards == 0;
    }

    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.FRAUD, FlagType.RIGGING));
    }

    @Override
    @PostConstruct
    protected void setPredicates() {
        preconditionsPredicates = Collections.unmodifiableList(Arrays.asList(
                FlaggedReleasePredicates.ACTIVE_AWARD,
                FlaggedReleasePredicates.UNSUCCESSFUL_AWARD,
                FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                FlaggedReleasePredicates.ELECTRONIC_SUBMISSION
        ));
    }
}