package it.unipi.LoveMining.repository.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import it.unipi.LoveMining.model.mongo.UserDocument;

@Repository
// Repository interface for User documents in MongoDB
public interface UserMongoRepository extends MongoRepository<UserDocument, String> {
    java.util.Optional<UserDocument> findByEmail(String email);

    @org.springframework.data.mongodb.repository.Query("{ 'reviewsMade.targetId': ?0 }")
    @org.springframework.data.mongodb.repository.Update("{ '$pull': { 'reviewsMade': { 'targetId': ?0 } } }")
    void removeReviewReferences(String targetId);

    // Analytics: Finds cities with the highest number of unhappy users
    @Aggregation(pipeline = {
            // 1. MATCH: Keep only users who have at least one review with a rating
            "{ '$match': { 'reviews_made.rating': { '$gt': 0 } } }",

            // 2. PROJECT: Calculate the average rating for each user
            "{ '$project': { 'city': 1, 'userPersonalAvg': { '$avg': '$reviews_made.rating' } } }",

            // 3. MATCH: Filter for "unhappy" users (average rating less than 3)
            "{ '$match': { 'userPersonalAvg': { '$lt': 3 } } }",

            // 4. GROUP: Group by City. Count total unhappy users and split by severity
            "{ '$group': { " +
                    "'_id': '$city', " +
                    "'totalUnhappyUsers': { '$sum': 1 }, " +
                    "'extremeHatersCount': { '$sum': { '$cond': [ { '$lt': ['$userPersonalAvg', 2] }, 1, 0 ] } }, " +
                    "'moderateUnhappyCount': { '$sum': { '$cond': [ { '$gte': ['$userPersonalAvg', 2] }, 1, 0 ] } }, " +
                    "'avgUnhappinessScore': { '$avg': '$userPersonalAvg' } } }",

            // 5. PROJECT: Format output and round the score to 2 decimal places
            "{ '$project': { " +
                    "'totalUnhappyUsers': 1, " +
                    "'extremeHatersCount': 1, " +
                    "'moderateUnhappyCount': 1, " +
                    "'avgUnhappinessScore': { '$round': ['$avgUnhappinessScore', 2] } } }",

            // 6. SORT: Order by the highest number of unhappy users (descending)
            "{ '$sort': { 'totalUnhappyUsers': -1 } }",

            // 7. LIMIT: Return only the top 5 cities
            "{ '$limit': 5 }"
    })
    List<Map<String, Object>> findTopUnhappyCities();

    default List<Map<String, Object>> findUnhappyCities() {
        return findTopUnhappyCities();
    }

    // Analytics: Groups users into 3 age buckets and calculates the percentage of singles
    @Aggregation(pipeline = {
            // 1. MATCH: Filter valid users with a known status
            "{ '$match': { 'status': { '$in': ['available', 'single', 'seeing someone', 'married'] } } }",

            // 2. PROJECT: Determine Age Group using $cond
            // Logic: IF (age <= 25) THEN '1. Young' ELSE (IF (age <= 40) THEN '2. Adult' ELSE '3. Senior')
            "{ '$project': { " +
                    "   '_id': 0, " +
                    "   'status': 1, " +
                    "   'ageGroup': { '$cond': [ { '$lte': ['$age', 25] }, '1. Young (18-25)', " +
                    "{ '$cond': [ { '$lte': ['$age', 40] }, '2. Adult (26-40)', " +
                    "'3. Senior (40+)' ] } ] } } }",

            // 3. GROUP: Group by the calculated 'ageGroup'
            "{ '$group': { " +
                    "   '_id': '$ageGroup', " +
                    "   'totalUsers': { '$sum': 1 }, " +
                    "   'singleCount': { '$sum': { '$cond': [ { '$in': ['$status', ['single', 'available']] }, 1, 0 ] } } } }",

            // 4. PROJECT: Calculate Percentage
            "{ '$project': { " +
                    "   '_id': 0, " +
                    "   'ageGroup': '$_id', " +
                    "   'totalUsers': 1, " +
                    "   'singlePercentage': { '$round': [ { '$multiply': [ { '$divide': ['$singleCount', '$totalUsers'] }, 100 ] }, 1 ] } } }", // Round to 1 decimal place

            // 5. SORT: Sort by group name (1 -> 2 -> 3)
            "{ '$sort': { 'ageGroup': 1} }"
    })
    List<Map<String, Object>> findSinglesByAgeGroup();

    default List<Map<String, Object>> getStatusAnalytics() {
        return findSinglesByAgeGroup();
    }

    // Analytics: Categorizes users into Groups by Orientation + AgeGroup
    @Aggregation(pipeline = {
            // 1. MATCH: Filter valid orientations
            "{ '$match': { 'orientation': { '$in': ['straight', 'gay', 'bisexual'] } } }",

            // 2. PROJECT: Calculate Age Group (Covered Query: _id excluded)
            "{ '$project': { " +
                    "   '_id': 0, " +
                    "   'orientation': 1, " +
                    "   'ageGroup': { '$cond': [ { '$lte': ['$age', 25] }, '1. Young (18-25)', " +
                    "{ '$cond': [ { '$lte': ['$age', 40] }, '2. Adult (26-40)', " +
                    "'3. Senior (40+)' ] } ] } } }",

            // 3. GROUP #1: Group by combination of Orientation and AgeGroup
            "{ '$group': { " +
                    "   '_id': { 'orientation': '$orientation', 'ageGroup': '$ageGroup' }, " +
                    "   'count': { '$sum': 1 } } }",

            // 4. SORT: Sort by group name (1 -> 2 -> 3)
            "{ '$sort': { '_id.ageGroup': 1 } }",

            // 5. GROUP #2: Group only by Orientation
            "{ '$group': { " +
                    "   '_id': '$_id.orientation', " +
                    "   'totalUsers': { '$sum': '$count' }, " +
                    "   'demographics': { '$push': { 'ageGroup': '$_id.ageGroup', 'usersCount': '$count' } } } }",

            // 6. SORT: Sort by total users
            "{ '$sort': { 'totalUsers': -1 } }"
    })
    List<Map<String, Object>> findOrientationDemographics();

    default List<Map<String, Object>> getOrientationAnalytics() {
        return findOrientationDemographics();
    }
}