package it.unipi.LoveMining.repository.neo4j;

import it.unipi.LoveMining.model.neo4j.UserNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
// Repository interface for User nodes in Neo4j
public interface UserNeo4jRepository extends Neo4jRepository<UserNode, String> {

        @org.springframework.data.neo4j.repository.query.Query("MATCH (u:User {_id: $id}) SET u.age = $age, u.sex = $sex, u.orientation = $orientation")
        void updateBasicProfile(String id, Integer age, String sex, String orientation);

        @org.springframework.data.neo4j.repository.query.Query("MATCH (u:User {_id: $id}) " +
                "OPTIONAL MATCH (u)-[r:LIVES_IN]->() " +
                "DELETE r " +
                "WITH u " +
                "MERGE (s:State {name: $stateName}) " +
                "MERGE (c:City {name: $cityName}) " +
                "MERGE (c)-[:LOCATED_IN]->(s) " +
                "MERGE (u)-[:LIVES_IN]->(c)")
        void updateLocation(String id, String cityName, String stateName);

        @org.springframework.data.neo4j.repository.query.Query("MATCH (u:User {_id: $id}) " +
                "OPTIONAL MATCH (u)-[r:HAS_INTEREST]->() DELETE r " +
                "FOREACH (name IN $interestNames | " +
                "MERGE (i:Interest {name: name}) " +
                "MERGE (u)-[:HAS_INTEREST]->(i))")
        void updateInterests(String id, java.util.List<String> interestNames);

        // Check if User 'from' likes 'to'
        @org.springframework.data.neo4j.repository.query.Query("MATCH (a:User {_id: $fromId})-[r:LIKES]->(b:User {_id: $toId}) RETURN count(r) > 0")
        boolean hasLiked(String fromId, String toId);

        // Check if Matched
        @org.springframework.data.neo4j.repository.query.Query("MATCH (a:User {_id: $userId})-[:MATCHED]-(b:User {_id: $targetId}) RETURN count(b) > 0")
        boolean areMatched(String userId, String targetId);

        // Create LIKES (A -> B)
        @org.springframework.data.neo4j.repository.query.Query("MATCH (a:User {_id: $actorId}) " +
                "MATCH (b:User {_id: $targetId}) " +
                "MERGE (a)-[r:LIKES]->(b) " +
                "RETURN count(r)")
        Long createLikeRelationship(String actorId, String targetId);

        // Create DISLIKES (A -> B)
        @org.springframework.data.neo4j.repository.query.Query("MATCH (a:User {_id: $actorId}) " +
                "MATCH (b:User {_id: $targetId}) " +
                "MERGE (a)-[r:DISLIKES]->(b) " +
                "RETURN count(r)")
        Long createDislikeRelationship(String actorId, String targetId);

        // Cancel LIKE of 'target' to 'actor' and create MATCHED
        @org.springframework.data.neo4j.repository.query.Query("MATCH (a:User {_id: $actorId}) " +
                "MATCH (b:User {_id: $targetId}) " +
                "OPTIONAL MATCH (a)-[r:LIKES]-(b) " +
                "DELETE r " +
                "MERGE (a)-[m:MATCHED]-(b) " +
                "RETURN count(m)")
        Long transformLikeToMatch(String actorId, String targetId);

        /*
         * Custom Query to find recommendations based on:
         * - Sex and Orientation
         * - No existing relationships (Likes/Dislikes/Matched)
         * - Geographic scope (City or State)
         * - Age range
         * - Ordered by common interests
         */
        @org.springframework.data.neo4j.repository.query.Query(
                        "MATCH (me:User {_id: $userId}) " +
                        "MATCH (me)-[:LIVES_IN]->(myCity:City)-[:LOCATED_IN]->(myState:State) " +
                        "MATCH (candidate:User)-[:LIVES_IN]->(candCity:City)-[:LOCATED_IN]->(candState:State) " +
                        "WHERE me._id <> candidate._id " +

                        // Constraint: No existing relationship between me and candidate
                        "AND NOT (me)-[:LIKES|DISLIKES|MATCHED]-(candidate) " +

                        // Constraint: Age Range
                        "AND candidate.age >= $minAge AND candidate.age <= $maxAge " +

                        // Constraint: Location Filter
                        // If input is 'City', we match the city.
                        // If input is 'State', we match the state.
                        "AND ( " +
                        "  ($filter = 'City' AND myCity = candCity) OR " + // Same node city
                        "  ($filter = 'State' AND myState = candState) " + // Same node state
                        ") " +

                        // Constraint: Sex & Orientation Compatibility
                        "AND ( " +
                        "  (me.orientation = 'straight' AND candidate.orientation = 'straight' AND me.sex <> candidate.sex) OR " +
                        "  (me.orientation = 'straight' AND candidate.orientation = 'bisexual' AND me.sex <> candidate.sex) OR " +
                        "  (me.orientation = 'gay' AND candidate.orientation = 'gay' AND me.sex = candidate.sex) OR " +
                        "  (me.orientation = 'gay' AND candidate.orientation = 'bisexual' AND me.sex = candidate.sex) OR " +
                        "  (me.orientation = 'bisexual' AND candidate.orientation = 'gay' AND me.sex = candidate.sex) OR" +
                        "  (me.orientation = 'bisexual' AND candidate.orientation = 'straight' AND me.sex <> candidate.sex) OR " +
                        "  (me.orientation = 'bisexual' AND candidate.orientation = 'bisexual')" +
                        ") " +

                        // Calculation: Common Interests
                        "WITH me, candidate " +
                        "OPTIONAL MATCH (me)-[:HAS_INTEREST]->(i:Interest)<-[:HAS_INTEREST]-(candidate) " +
                        "WITH candidate, count(i) AS commonInterests " +

                        // Return the top 10 candidates sorted by common interests
                        "RETURN candidate._id " +
                        "ORDER BY commonInterests DESC " +
                        "LIMIT 10")
        List<String> findRecommendations(@Param("userId") String userId, @Param("filter") String filter,
                        @Param("minAge") int minAge, @Param("maxAge") int maxAge);

        /*
         * ANALYTIC: Love Points (input: State)
         * 1. Finds Users in Cities of the State
         * 2. Counts internal LIKES (User A in City X -> User B in City X) = 1 Point
         * 3. Counts internal MATCHES (User A in City X <-> User B in City X) = 3 Points (check on ID to count the couple only once)
         * 4. Returns JSON with City, UserCount, LovePoints, LoveRatio.
         */
        @org.springframework.data.neo4j.repository.query.Query("MATCH (s:State {name: $stateName})<-[:LOCATED_IN]-(c:City)<-[:LIVES_IN]-(u:User) "
                        +

                        // 1. Internal Likes (User -> Target in the same City)
                        // Aggregate by user to avoid duplicates
                        "OPTIONAL MATCH (u)-[l:LIKES]->(target:User)-[:LIVES_IN]->(c) " +
                        "WITH c, u, count(l) as userLikes " +

                        // 2. Internal MATCHED (User <-> Partner in the same City)
                        // WHERE u._id < partner._id to count the couple only once
                        "OPTIONAL MATCH (u)-[m:MATCHED]-(partner:User)-[:LIVES_IN]->(c) " +
                        "WHERE u._id < partner._id " +
                        "WITH c, u, userLikes, count(m) as userMatches " +

                        // 3. Aggregation for each City
                        "WITH c.name AS city, " +
                        "     count(DISTINCT u) AS users, " +
                        "     sum(userLikes) AS totalLikes, " +
                        "     sum(userMatches) AS totalMatches " +

                        // 4. Points count
                        "WITH city, users, " +
                        "     (totalLikes * 1) + (totalMatches * 3) AS interactions " +

                        "WITH city, users, interactions, " +
                        "CASE WHEN users > 0 THEN toFloat(interactions) / users ELSE 0.0 END AS loveRatio " +
                        "RETURN { city: city, users: users, interactions: interactions, loveRatio: loveRatio } " +
                        "ORDER BY loveRatio DESC")
        List<java.util.Map<String, Object>> getLovePointsAnalytic(@Param("stateName") String stateName);

        @org.springframework.data.neo4j.repository.query.Query("MATCH (u:User {_id: $id})-[:MATCHED]-(m:User) RETURN m")
        java.util.List<it.unipi.LoveMining.model.neo4j.UserNode> findMatches(@Param("id") String id);

}
