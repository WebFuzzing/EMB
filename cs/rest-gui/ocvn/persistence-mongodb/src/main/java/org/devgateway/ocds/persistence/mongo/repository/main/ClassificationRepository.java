package org.devgateway.ocds.persistence.mongo.repository.main;

import org.devgateway.ocds.persistence.mongo.Classification;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@CacheConfig(cacheNames = "classifications")
public interface ClassificationRepository extends MongoRepository<Classification, String> {

    @Cacheable
    @Override
    Classification findOne(String id);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Classification> List<S> save(Iterable<S> entites);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Classification> S save(S entity);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Classification> List<S> insert(Iterable<S> entities);

    @Override
    @CacheEvict(allEntries = true)
    <S extends Classification> S insert(S entity);
}
