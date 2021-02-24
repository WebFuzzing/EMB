package org.devgateway.ocds.web.flags.release;

import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Detail;
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
import java.util.Optional;
import java.util.Set;

/**
 * @author mpostelnicu
 *         <p>
 *         i002 Winning supplier provides a substantially lower bid price than competitors
 */
@Component
public class ReleaseFlagI002Processor extends AbstractFlaggedReleaseFlagProcessor {

    public static final BigDecimal MAX_ALLOWED_PERCENT_BID_AWARD_AMOUNT = new BigDecimal(0.25);


    @PostConstruct
    @Override
    protected void setPredicates() {
        preconditionsPredicates = Collections.synchronizedList(Arrays.asList(
                FlaggedReleasePredicates.ACTIVE_AWARD,
                FlaggedReleasePredicates.UNSUCCESSFUL_AWARD,
                FlaggedReleasePredicates.ELECTRONIC_SUBMISSION,
                FlaggedReleasePredicates.OPEN_PROCUREMENT_METHOD
        ));
    }


    @Override
    protected void setFlag(Flag flag, FlaggedRelease flaggable) {
        flaggable.getFlags().setI002(flag);
    }

    @Override
    protected Set<FlagType> flagTypes() {
        return new HashSet<FlagType>(Arrays.asList(FlagType.RIGGING, FlagType.FRAUD));
    }

    @Override
    protected Boolean calculateFlag(FlaggedRelease flaggable, StringBuffer rationale) {

        //get smallest bid
        Optional<Detail> smallestBid = flaggable.getBids().getDetails().stream()
                .min((o1, o2) -> o1.getValue().getAmount().compareTo(o2.getValue().getAmount()));

        //get the award
        Optional<Award> award = flaggable.getAwards().stream().filter(a ->
                Award.Status.active.equals(a.getStatus())).findFirst();

        boolean result = smallestBid.isPresent() && award.isPresent()
                && (relativeDistanceLeft(award.get().getValue().getAmount(),
                smallestBid.get().getValue().getAmount()).compareTo(MAX_ALLOWED_PERCENT_BID_AWARD_AMOUNT)
                > 0 || relativeDistanceRight(award.get().getValue().getAmount(),
                smallestBid.get().getValue().getAmount()).compareTo(MAX_ALLOWED_PERCENT_BID_AWARD_AMOUNT) > 0);


        rationale.append("Award ").append(award.isPresent() ? award.get().getValue().getAmount() : "not present"
        ).append("; smallest bid ").append(smallestBid.isPresent() ? smallestBid.get().getValue().getAmount()
                : "not present");
        return result;
    }


}