package org.devgateway.ocvn.persistence.mongo.repository.main;

import org.devgateway.ocvn.persistence.mongo.dao.City;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

@CacheConfig(cacheNames = "cities")
public interface CityRepository extends MongoRepository<City, Integer> {

    @Cacheable
    @Override
    City findOne(Integer id);

    @Cacheable
    City findByName(String name);

    @Override
    @CacheEvict(allEntries = true)
    <S extends City> List<S> save(Iterable<S> entites);

    @Override
    @CacheEvict(allEntries = true)
    <S extends City> S save(S entity);

    @Override
    @CacheEvict(allEntries = true)
    <S extends City> List<S> insert(Iterable<S> entites);

    @CacheEvict(allEntries = true)
    @Override
    <S extends City> S insert(S entity);
}
