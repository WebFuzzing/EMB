package org.devgateway.ocvn.persistence.mongo.repository.main;

import org.devgateway.ocvn.persistence.mongo.dao.OrgDepartment;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@CacheConfig(cacheNames = "orgDepartments")
public interface OrgDepartmentRepository extends MongoRepository<OrgDepartment, Integer> {

    @Cacheable
    @Override
    OrgDepartment findOne(Integer id);

    @Cacheable
    OrgDepartment findByName(String name);

    @Override
    @CacheEvict(allEntries = true)
    <S extends OrgDepartment> List<S> save(Iterable<S> entites);

    @Override
    @CacheEvict(allEntries = true)
    <S extends OrgDepartment> S save(S entity);

    @Override
    @CacheEvict(allEntries = true)
    <S extends OrgDepartment> List<S> insert(Iterable<S> entites);

    @CacheEvict(allEntries = true)
    @Override
    <S extends OrgDepartment> S insert(S entity);
}
