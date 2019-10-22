package org.devgateway.ocds.persistence.mongo.flags;

import org.devgateway.ocds.persistence.mongo.flags.preconditions.NamedPredicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractFlagProcessor<T extends Flaggable> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Calculates the given flag and returns the flag value. Does not set any
     * flag to the given {@link Flaggable}
     *
     * @param flaggable a {@link Flaggable}
     * @return the value of the flag. This can be null (not applicable), false ,
     * or true
     */
    protected abstract Boolean calculateFlag(T flaggable, StringBuffer rationale);

    /**
     * Performs required initializations for flags, if needed
     *
     * @param flaggable
     */
    protected abstract void initializeFlags(T flaggable);

    /**
     * Decides if the current {@link Flaggable} is eligible for this flag
     *
     * @return true if is eligible, false otherwise
     */
    protected Collection<NamedPredicate<T>> preconditionsPredicates;

    /**
     * Processes the flag on the flaggable only if the preconditions are met
     *
     * @param flaggable
     */
    public final void process(T flaggable) {
        Boolean flagValue = null;
        StringBuffer rationale = new StringBuffer();
        Set<NamedPredicate<T>> failedPreconditionsPredicates = preconditionsPredicates.parallelStream()
                .filter(predicate -> !predicate.test(flaggable)).collect(Collectors.toSet());

        if (failedPreconditionsPredicates.isEmpty()) {
            logger.debug("Flaggable " + flaggable.getIdProperty() + " does meet all preconditions. Calculating flag.");
            flagValue = calculateFlag(flaggable, rationale);
        } else {
            logger.debug("Flaggable " + flaggable.getIdProperty()
                    + " does NOT meet all preconditions. Dumping failed predicates.");
            rationale.append("Preconditions that are not met: ");
            failedPreconditionsPredicates.forEach(p -> rationale.append(p.toString()).append("; "));
        }

        if (flagValue != null && flagValue) {
            logger.debug("Setting flag with value " + flagValue + " to flaggable " + flaggable.getIdProperty());
        }

        initializeFlags(flaggable);
        Flag flag = new Flag(flagValue, rationale.toString(), flagTypes());
        setFlag(flag, flaggable);
        collectStats(flag, flaggable);
    }

    /**
     * These are the flag types related to the current flag. They are defined in {@link FlagType}
     *
     * @return
     */
    protected abstract Set<FlagType> flagTypes();

    /**
     * Sets the flag to the {@link Flaggable}
     *
     * @param flag      the new flag
     * @param flaggable the flaggable to set the flag to
     */
    protected abstract void setFlag(Flag flag, T flaggable);


    protected void collectStats(Flag flag, T flaggable) {

        //eligible
        if (flag.getValue() != null) {
            flag.getTypes().forEach(f -> flaggable.getFlags().getEligibleStatsMap()
                    .put(f, flaggable.getFlags().getEligibleStatsMap().containsKey(f)
                            ? flaggable.getFlags().getEligibleStatsMap().get(f).inc() : FlagTypeCount.newInstance(f)));
        }

        //flagged
        if (flag.getValue() != null && flag.getValue()) {
            flaggable.getFlags().incFlagCnt();
            flag.getTypes().forEach(f -> flaggable.getFlags().getFlaggedStatsMap().
                    put(f, flaggable.getFlags().getFlaggedStatsMap().containsKey(f)
                            ? flaggable.getFlags().getFlaggedStatsMap().get(f).inc() : FlagTypeCount.newInstance(f)));
        }


    }

}
