/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.flags;

import org.devgateway.ocds.persistence.mongo.Identifiable;

/**
 * @author mpostelnicu An entity that can be flagged
 */
public interface Flaggable extends Identifiable {

    FlagsWrappable getFlags();
}
