package org.devgateway.ocds.web.flags.release;


import java.util.Arrays;
import java.util.Collections;
import javax.annotation.PostConstruct;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.springframework.stereotype.Component;

/**
 * @author mpostelnicu
 *         <p>
 *         i180 Contractor receives multiple single-source/non-competitive contracts from a single procuring entity
 *         during a defined time period
 */
@Component
public class ReleaseFlagI180Processor extends ReleaseFlagI077Processor {

    private static final Integer MAX_AWARDS = 2;

    @Override
    public Integer getMaxAwards() {
        return MAX_AWARDS;
    }

    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI180(flag);
    }


    @PostConstruct
    @Override
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(
                Arrays.asList(FlaggedReleasePredicates.ACTIVE_AWARD_WITH_DATE,
                        FlaggedReleasePredicates.TENDER_PROCURING_ENTITY,
                        FlaggedReleasePredicates.LIMITED_PROCUREMENT_METHOD));

        reInitialize();
    }

}