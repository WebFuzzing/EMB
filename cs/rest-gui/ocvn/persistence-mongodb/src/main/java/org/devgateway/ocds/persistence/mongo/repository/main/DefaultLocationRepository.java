package org.devgateway.ocds.persistence.mongo.repository.main;

import org.devgateway.ocds.persistence.mongo.DefaultLocation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * @author idobre
 * @since 9/13/16
 */
@CacheConfig(cacheNames = "locations")
public interface DefaultLocationRepository extends MongoRepository<DefaultLocation, String> {

    @Cacheable
    DefaultLocation findByDescription(String description);

    @Override
    @CacheEvict(allEntries = true)
    <S extends DefaultLocation> List<S> save(Iterable<S> entites);

    @Override
    @CacheEvict(allEntries = true)
    <S extends DefaultLocation> S save(S entity);

    @Override
    @CacheEvict(allEntries = true)
    <S extends DefaultLocation> List<S> insert(Iterable<S> entities);

    @Override
    @CacheEvict(allEntries = true)
    <S extends DefaultLocation> S insert(S entity);
}
