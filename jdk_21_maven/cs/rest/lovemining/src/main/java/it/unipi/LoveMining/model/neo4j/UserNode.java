package it.unipi.LoveMining.model.neo4j;

import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.annotation.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Node("User")
public class UserNode {

    @Id
    @Property("_id")
    private String id;

    private Integer age;
    private String sex;
    private String orientation;

    @lombok.EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    @Relationship(type = "LIVES_IN", direction = Relationship.Direction.OUTGOING)
    private CityNode cityNode;

    @lombok.EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    @Relationship(type = "HAS_INTEREST", direction = Relationship.Direction.OUTGOING)
    private java.util.Set<InterestNode> interests;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    @Relationship(type = "LIKES", direction = Relationship.Direction.OUTGOING)
    private java.util.Set<UserNode> likes;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @lombok.EqualsAndHashCode.Exclude
    @lombok.ToString.Exclude
    @Relationship(type = "DISLIKES", direction = Relationship.Direction.OUTGOING)
    private java.util.Set<UserNode> dislikes;
}