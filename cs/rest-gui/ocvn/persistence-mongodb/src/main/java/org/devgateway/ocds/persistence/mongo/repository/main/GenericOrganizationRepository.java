package org.devgateway.ocds.persistence.mongo.repository.main;

import org.devgateway.ocds.persistence.mongo.Organization;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface GenericOrganizationRepository<T extends Organization> extends MongoRepository<T, String> {

    @Override
    T findOne(String id);

    T findByIdOrNameAllIgnoreCase(String id, String name);

    @Query(value = "{'additionalIdentifiers._id': ?0}")
    T findByAllIds(String id);
    
    @Query(value = "{$and: [ { $or: [ {'_id' : ?0 }, " + "{'name': ?0 } ] }  , { 'roles': ?1 } ]}")
    T findByIdOrNameAndTypes(String idName, Organization.OrganizationType type);

    @Query(value = "{ $or: [ {'_id' : ?0 }, " + "{'name': ?0} ] }")
    T findByIdOrName(String idName);

    T findByIdAndRoles(String id, Organization.OrganizationType type);

    T findByName(String name);
}
