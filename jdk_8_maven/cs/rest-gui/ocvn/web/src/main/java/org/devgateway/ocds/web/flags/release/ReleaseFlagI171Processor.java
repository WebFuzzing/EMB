package org.devgateway.ocds.web.flags.release;

import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.flags.AbstractFlaggedReleaseFlagProcessor;
import org.devgateway.ocds.persistence.mongo.flags.Flag;
import org.devgateway.ocds.persistence.mongo.flags.FlagType;
import org.devgateway.ocds.persistence.mongo.flags.preconditions.FlaggedReleasePredicates;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author mpostelnicu
 *         <p>
 *         i171 Bid is too close to budget, estimate or preferred solution
 */
@Component
public class ReleaseFlagI171Processor extends AbstractFlaggedReleaseFlagProcessor {


    public static final BigDecimal MAX_ALLOWED_PERCENT_TENDER_VALUE_AMOUNT = new BigDecimal(0.01);


    @PostConstruct
    @Override
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(Arrays.asList(
                FlaggedReleasePredicates.ACTIVE_AWARD,
                FlaggedReleasePredicates.ELECTRONIC_SUBMISSION,
                FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD,
                FlaggedReleasePredicates.TENDER_VALUE_AMOUNT
        ));
    }


    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI171(flag);
    }

    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING, FlagType.FRAUD));
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {

        boolean result = false;
        for (Award award : flaggable.getAwards()) {
            if (!Award.Status.active.equals(award.getStatus())) {
                continue;
            }

            BigDecimal dLeft = relativeDistanceLeft(flaggable.getTender().getValue().getAmount(),
                    award.getValue().getAmount());

            BigDecimal dRight = relativeDistanceRight(flaggable.getTender().getValue().getAmount(),
                    award.getValue().getAmount());

            rationale.append(";Award=").append(award.getValue().getAmount())
                    .append(" with tender=").append(flaggable.getTender().getValue().getAmount());

            if (dLeft.compareTo(MAX_ALLOWED_PERCENT_TENDER_VALUE_AMOUNT) < 0
                    || dRight.compareTo(MAX_ALLOWED_PERCENT_TENDER_VALUE_AMOUNT) < 0) {
                result = true;
                break;
            }

        }
        return result;
    }


}