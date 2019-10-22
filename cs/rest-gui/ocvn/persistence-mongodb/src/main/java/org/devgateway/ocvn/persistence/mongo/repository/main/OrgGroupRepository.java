package org.devgateway.ocvn.persistence.mongo.repository.main;

import org.devgateway.ocvn.persistence.mongo.dao.OrgGroup;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@CacheConfig(cacheNames = "orgGroups")
public interface OrgGroupRepository extends MongoRepository<OrgGroup, Integer> {

    @Cacheable
    @Override
    OrgGroup findOne(Integer id);

    @Cacheable
    OrgGroup findByName(String name);

    @Override
    @CacheEvict(allEntries = true)
    <S extends OrgGroup> List<S> save(Iterable<S> entites);

    @Override
    @CacheEvict(allEntries = true)
    <S extends OrgGroup> S save(S entity);

    @Override
    @CacheEvict(allEntries = true)
    <S extends OrgGroup> List<S> insert(Iterable<S> entites);

    @CacheEvict(allEntries = true)
    @Override
    <S extends OrgGroup> S insert(S entity);
}
