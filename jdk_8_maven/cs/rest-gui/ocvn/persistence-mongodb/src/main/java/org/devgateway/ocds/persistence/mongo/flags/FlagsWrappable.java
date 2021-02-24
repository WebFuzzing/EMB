/**
 *
 */
package org.devgateway.ocds.persistence.mongo.flags;

import java.util.Map;

/**
 * @author mpostelnicu
 *         Interface designating the wrapper entity holding the flags
 */
public interface FlagsWrappable {

    Map<FlagType, FlagTypeCount> getFlaggedStatsMap();

    Map<FlagType, FlagTypeCount> getEligibleStatsMap();

    void incFlagCnt();

}
