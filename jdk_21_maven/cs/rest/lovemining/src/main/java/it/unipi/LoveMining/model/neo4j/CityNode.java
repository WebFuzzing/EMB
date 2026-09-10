package it.unipi.LoveMining.model.neo4j;

import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("City")
public class CityNode {

    @Id
    private String name;

    @Relationship(type = "LOCATED_IN", direction = Relationship.Direction.OUTGOING)
    private StateNode state;
}
