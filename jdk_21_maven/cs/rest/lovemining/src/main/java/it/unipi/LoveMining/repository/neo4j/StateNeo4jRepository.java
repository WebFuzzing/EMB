package it.unipi.LoveMining.repository.neo4j;

import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import it.unipi.LoveMining.model.neo4j.StateNode;

@Repository
// Repository interface for State nodes in Neo4j
public interface StateNeo4jRepository extends Neo4jRepository<StateNode, String> {

}
