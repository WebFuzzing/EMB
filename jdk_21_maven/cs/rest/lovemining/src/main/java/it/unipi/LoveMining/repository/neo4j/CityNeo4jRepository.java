package it.unipi.LoveMining.repository.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import it.unipi.LoveMining.model.neo4j.CityNode;

@Repository
// Repository interface for City nodes in Neo4j
public interface CityNeo4jRepository extends Neo4jRepository<CityNode, String> {

}
