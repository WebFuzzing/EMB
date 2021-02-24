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
import org.springframework.stereotype.Component;

/**
 * @author mpostelnicu
 *         <p>
 *         i007 This awarded competitive tender only featured a single bid
 */
@Component
public class ReleaseFlagI007Processor extends AbstractFlaggedReleaseFlagProcessor {

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI007(flag);
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {
        long countAwards = flaggable.getAwards().size();

        rationale.append("Number of bids: ").append(countAwards);
        return countAwards == 1;
    }

    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING));
    }

    @Override
    @PostConstruct
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(
                Arrays.asList(FlaggedReleasePredicates.ACTIVE_AWARD,
                        FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                        FlaggedReleasePredicates.ELECTRONIC_SUBMISSION
                ));
    }
}