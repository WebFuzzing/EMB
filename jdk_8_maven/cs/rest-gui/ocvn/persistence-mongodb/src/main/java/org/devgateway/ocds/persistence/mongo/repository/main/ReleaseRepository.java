/**
 * 
 */
package org.devgateway.ocds.persistence.mongo.repository.main;

import org.devgateway.ocds.persistence.mongo.Release;
import org.springframework.data.mongodb.repository.Query;

/**
 * @author mpostelnicu
 *
 */
public interface ReleaseRepository extends GenericReleaseRepository<Release> {

    Release findById(String id);
    
    /**
     * Vietnam specific planning bid no find
     * 
     * @param bidNo
     * @return
     */
    @Query(value = "{ 'planning.bidNo' : ?0 }")
    Release findByPlanningBidNo(String bidNo);

    @Override
    <S extends Release> S save(S entity);
}
