package it.unipi.LoveMining.repository.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import it.unipi.LoveMining.model.neo4j.InterestNode;

@Repository
// Repository interface for Interest nodes in Neo4j
public interface InterestNeo4jRepository extends Neo4jRepository<InterestNode, String> {

}
