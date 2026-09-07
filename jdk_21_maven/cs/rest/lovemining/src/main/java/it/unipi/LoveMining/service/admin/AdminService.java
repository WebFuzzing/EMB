package it.unipi.LoveMining.service.admin;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import it.unipi.LoveMining.repository.mongo.UserMongoRepository;
import it.unipi.LoveMining.repository.mongo.ReviewMongoRepository;
import it.unipi.LoveMining.repository.neo4j.UserNeo4jRepository;
import it.unipi.LoveMining.model.mongo.UserDocument;
import it.unipi.LoveMining.model.mongo.ReviewDocument;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// Service class for administrative operations like User deletion
public class AdminService {

    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final ReviewMongoRepository reviewMongoRepository;

    // Deletes a user by ID from both MongoDB (with associated reviews) and Neo4j
    public void deleteUser(String id) {

        UserDocument user = userMongoRepository.findById(id) // Retrieve the user
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + id));

        // Delete form neo4j first
        try {
            if (userNeo4jRepository.existsById(id)) {
                userNeo4jRepository.deleteById(id);
            }
        } catch (Exception e) {
            System.err.println("Could not delete node from Neo4j.");
            throw new RuntimeException("Deletion failed: Graph Database is unavailable.");
        }

        // Delete from MongoDB
        // 1. Delete Reviews MADE by the user
        if (user.getReviewsMade() != null && !user.getReviewsMade().isEmpty()) {
            try {
                // Extract the list of Review IDs from the user's summary list
                List<String> reviewIdsToDelete = user.getReviewsMade().stream()
                        .map(UserDocument.ReviewSummary::getReviewId).collect(Collectors.toList());

                // Delete all these reviews from the Review Collection in one go
                if (!reviewIdsToDelete.isEmpty()) {
                    reviewMongoRepository.deleteAllById(reviewIdsToDelete);
                }
            } catch (Exception e) {
                System.err.println("Warning: Failed to delete some reviews made by user: " + e.getMessage());
            }
        }

        // 2. Delete Reviews RECEIVED by the user (and update references)
        try {
            reviewMongoRepository.deleteByTargetId(id);
            // Remove references from authors' "reviewsMade" lists
            userMongoRepository.removeReviewReferences(id);
        } catch (Exception e) {
            System.err.println("Warning: Failed to clean up received reviews: " + e.getMessage());
        }

        // 3. Delete the User Document
        userMongoRepository.deleteById(id);
        System.out.println("User successfully deleted from both databases: " + id);
    }

    // Retrieves a user by their ID from MongoDB
    public UserDocument getUserById(String id) {
        return userMongoRepository.findById(id).orElse(null);
    }

    // Retrieves a review by its ID from MongoDB
    public ReviewDocument getReviewById(String id) {
        return reviewMongoRepository.findById(id).orElse(null);
    }

    // Analytics: Love Points
    public List<Map<String, Object>> getLovePointsStats(String state) {
        return userNeo4jRepository.getLovePointsAnalytic(state);
    }

    // Analytics: Glow-up
    public List<Map<String, Object>> getBestGlowUpUsers() {
        return reviewMongoRepository.findBestGlowUpUsers();
    }

    //Analytics: Unhappy Cities
    public List<Map<String, Object>> getUnhappyCities() {
        return userMongoRepository.findUnhappyCities();
    }

    // Analytics: Status by Age Group
    public List<Map<String, Object>> getStatusAnalytics() {
        return userMongoRepository.getStatusAnalytics();
    }

    // Analytics: Orientation by Age Group
    public List<Map<String, Object>> getOrientationAnalytics() {
        return userMongoRepository.getOrientationAnalytics();
    }
}
