package org.devgateway.ocvn.persistence.mongo.repository.main;

import org.devgateway.ocvn.persistence.mongo.dao.VNLocation;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@CacheConfig(cacheNames = "locations")
public interface VNLocationRepository extends MongoRepository<VNLocation, String> {

    @Cacheable
    VNLocation findByDescription(String description);

    @Override
    @CacheEvict(allEntries = true)
    <S extends VNLocation> List<S> save(Iterable<S> entites);

    @Override
    @CacheEvict(allEntries = true)
    <S extends VNLocation> S save(S entity);

    @Override
    @CacheEvict(allEntries = true)
    <S extends VNLocation> List<S> insert(Iterable<S> entities);

    @Override
    @CacheEvict(allEntries = true)
    <S extends VNLocation> S insert(S entity);
}
