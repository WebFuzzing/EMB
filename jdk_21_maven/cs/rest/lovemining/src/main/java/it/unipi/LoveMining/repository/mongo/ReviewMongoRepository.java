package it.unipi.LoveMining.repository.mongo;

import it.unipi.LoveMining.model.mongo.ReviewDocument;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public interface ReviewMongoRepository extends MongoRepository<ReviewDocument, String> {

    void deleteByTargetId(String targetId);

    @Aggregation(pipeline = {
            // 1: GROUP by user
            // Sum ratings and count reviews for Recent (>= ?0) and Past (< ?0) periods.
            "{ $group: { " +
                    "   _id: '$target_id', " +
                    "   recentRatingSum: { $sum: { $cond: [{ $gte: ['$review_date', ?0] }, '$rating', 0] } }, " +
                    "   recentCount: { $sum: { $cond: [{ $gte: ['$review_date', ?0] }, 1, 0] } }, " +
                    "   pastRatingSum: { $sum: { $cond: [{ $lt: ['$review_date', ?0] }, '$rating', 0] } }, " +
                    "   pastCount: { $sum: { $cond: [{ $lt: ['$review_date', ?0] }, 1, 0] } }, " +
                    "   totalCount: { $sum: 1 } " +
                    "} }",

            // 2: MATCH
            // Filter users: must have activity in both periods and at least 3 reviews total.
            "{ $match: { recentCount: { $gt: 0 },  pastCount: { $gt: 0 },   totalCount: { $gte: 3 } } }",

            // 3: PROJECT
            // Calculate averages. 'glowUpIndex' is the improvement (Recent Avg - Past Avg).
            "{ $project: { " +
                    "   recentAvg: { $divide: ['$recentRatingSum', '$recentCount'] }, " +
                    "   pastAvg: { $divide: ['$pastRatingSum', '$pastCount'] }, " +
                    "   glowUpIndex: { $subtract: [ " +
                    "       { $divide: ['$recentRatingSum', '$recentCount'] }, " +
                    "       { $divide: ['$pastRatingSum', '$pastCount'] } " +
                    "   ] } " +
                    "} }",

            // 4: SORT by highest first
            "{ $sort: { glowUpIndex: -1 } }",

            // 5: LIMIT by 3
            "{ $limit: 3 }"
    })
    List<Map<String, Object>> findGlowUpRaw(Date cutoffDate);

    default List<Map<String, Object>> findBestGlowUpUsers() {
        Date sixMonthsAgo = Date.from(LocalDateTime.now()
                .minusMonths(6)
                .atZone(ZoneId.systemDefault())
                .toInstant());

        return findGlowUpRaw(sixMonthsAgo);
    }
}