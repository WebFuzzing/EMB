package org.devgateway.ocvn.persistence.mongo.repository.main;

import org.devgateway.ocvn.persistence.mongo.dao.ContrMethod;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@CacheConfig(cacheNames = "contrMethods")
public interface ContrMethodRepository extends MongoRepository<ContrMethod, Integer> {

    @Cacheable
    @Override
    ContrMethod findOne(Integer id);

    @Cacheable
    ContrMethod findByDetails(String details);

    @Override
    @CacheEvict(allEntries = true)
    <S extends ContrMethod> List<S> save(Iterable<S> entites);

    @Override
    @CacheEvict(allEntries = true)
    <S extends ContrMethod> S save(S entity);

    @Override
    @CacheEvict(allEntries = true)
    <S extends ContrMethod> List<S> insert(Iterable<S> entites);

    @CacheEvict(allEntries = true)
    @Override
    <S extends ContrMethod> S insert(S entity);
}
