/**
 *
 */
package org.devgateway.ocds.persistence.mongo.flags;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;

import java.math.BigDecimal;

/**
 * @author mpostelnicu
 */
public abstract class AbstractFlaggedReleaseFlagProcessor extends AbstractFlagProcessor<FlaggedRelease> {

    @Override
    protected void initializeFlags(FlaggedRelease flaggable) {
        if (flaggable.getFlags() == null) {
            flaggable.setFlags(new ReleaseFlags());
        }
    }

    protected BigDecimal relativeDistanceLeft(BigDecimal left, BigDecimal right) {
        return left.
                subtract(right).
                divide(left, 5, BigDecimal.ROUND_HALF_UP).abs();
    }

    protected BigDecimal relativeDistanceRight(BigDecimal left, BigDecimal right) {
        return right.
                subtract(left).
                divide(right, 5, BigDecimal.ROUND_HALF_UP).abs();
    }

    /**
     * Set the predicates used by the flag processor, this is usually invoked
     * in subclass services using @PostConstruct
     */
    protected abstract void setPredicates();

    /**
     * Possible external reinitialization of internal state
     */
    public void reInitialize() {

    }

}
