package org.devgateway.ocds.persistence.mongo.repository.main;

import org.devgateway.ocds.persistence.mongo.Release;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericReleaseRepository<T extends Release> extends MongoRepository<T, String> {

    @Query(value = "{ 'planning.budget.projectID' : ?0 }")
    T findByBudgetProjectId(String projectId);

    T findByOcid(String ocid);

    T findById(String id);

    @Override
    <S extends T> S save(S entity);
}
