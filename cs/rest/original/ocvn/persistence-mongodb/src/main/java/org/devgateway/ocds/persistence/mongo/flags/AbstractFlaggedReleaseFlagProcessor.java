/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.flags;

import org.devgateway.ocds.persistence.mongo.FlaggedRelease;

/**
 * @author mpostelnicu
 *
 */
public abstract class AbstractFlaggedReleaseFlagProcessor extends AbstractFlagProcessor<FlaggedRelease> {

    @Override
    protected void initializeFlags(FlaggedRelease flaggable) {
        if (flaggable.getFlags() == null) {
            flaggable.setFlags(new ReleaseFlags());
        }
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
