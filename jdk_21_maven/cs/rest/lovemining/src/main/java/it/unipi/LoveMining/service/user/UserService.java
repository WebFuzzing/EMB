package it.unipi.LoveMining.service.user;

import org.springframework.stereotype.Service;

import it.unipi.LoveMining.model.mongo.ReviewDocument;
import it.unipi.LoveMining.model.mongo.UserDocument;
import it.unipi.LoveMining.repository.mongo.ReviewMongoRepository;
import it.unipi.LoveMining.repository.mongo.UserMongoRepository;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
// Service class for managing User retrieval operations
public class UserService {

    private final UserMongoRepository userMongoRepository;
    private final ReviewMongoRepository reviewMongoRepository;
    private final it.unipi.LoveMining.repository.neo4j.UserNeo4jRepository userNeo4jRepository;
    private final it.unipi.LoveMining.service.utility.InterestExtractorService interestExtractorService;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    // Retrieves a user by their ID from MongoDB
    public UserDocument getUser(String id) {
        return userMongoRepository.findById(id).orElse(null);
    }

    // Retrieves a user by their Email from MongoDB
    public UserDocument getUserByEmail(String email) {
        return userMongoRepository.findByEmail(email).orElse(null);
    }

    // Retrieves matches (UserNodes) from Neo4j for a given User ID
    public java.util.Set<it.unipi.LoveMining.model.neo4j.UserNode> getUserMatches(String id) {
        return new java.util.HashSet<>(userNeo4jRepository.findMatches(id));
    }

    // Retrieves reviews made by the user from MongoDB
    public java.util.List<UserDocument.ReviewSummary> getUserReviews(String id) {
        UserDocument user = getUser(id);
        if (user != null) {
            return user.getReviewsMade();
        }
        return java.util.Collections.emptyList();
    }

    // Updates a user in MongoDB and Neo4j
    @org.springframework.transaction.annotation.Transactional
    public void updateUser(String id, UserDocument inputUser) throws Exception {

        UserDocument userDoc = getUser(id);
        if (userDoc == null)
            throw new Exception("User not found in MongoDB");

        // Check if user exists in Neo4j
        if (!userNeo4jRepository.existsById(id)) {
            throw new Exception("User node not found in Graph");
        }

        // Flags to track what needs to be synced to Neo4j
        boolean updateBasicInfo = false;
        boolean updateInterests = false;
        boolean updateLocation = false;

        // Validation (Age, Sex, Orientation, Status)
        // Use inputUser as the source of "new" values
        Integer newAge = inputUser.getAge();
        String newSex = inputUser.getSex();
        String newOrientation = inputUser.getOrientation();

        if (newAge != null) { // age validation
            if (newAge < 18 || newAge > 100)
                throw new Exception("Age must be between 18 and 100");
            userDoc.setAge(newAge);
            updateBasicInfo = true;
        }

        if (newSex != null) { // sex validation
            if (!newSex.equalsIgnoreCase("m") && !newSex.equalsIgnoreCase("f"))
                throw new Exception("Sex must be 'm' or 'f'");
            userDoc.setSex(newSex);
            updateBasicInfo = true;
        }

        if (newOrientation != null) { // orientation validation
            if (!newOrientation.equalsIgnoreCase("straight") &&
                    !newOrientation.equalsIgnoreCase("gay") &&
                    !newOrientation.equalsIgnoreCase("bisexual"))
                throw new Exception("Orientation must be 'straight', 'gay', or 'bisexual'");
            userDoc.setOrientation(newOrientation);
            updateBasicInfo = true;
        }

        if (inputUser.getStatus() != null) { // status validation
            if (!inputUser.getStatus().equalsIgnoreCase("available") &&
                    !inputUser.getStatus().equalsIgnoreCase("single") &&
                    !inputUser.getStatus().equalsIgnoreCase("seeing someone") &&
                    !inputUser.getStatus().equalsIgnoreCase("married") &&
                    !inputUser.getStatus().equalsIgnoreCase("unknown"))
                throw new Exception("Status must be 'available', 'single', 'seeing someone', 'married' or 'unknown'");
            userDoc.setStatus(inputUser.getStatus().toLowerCase().trim());
        }

        // Password update
        if (inputUser.getPassword() != null && !inputUser.getPassword().isEmpty()) {
            if (inputUser.getPassword().length() < 4) {
                throw new Exception("New password must be at least 4 characters long.");
            }
            userDoc.setPassword(passwordEncoder.encode(inputUser.getPassword()));
        }

        // Simple Fields Update (Mongo only)
        if (inputUser.getBodyType() != null)
            userDoc.setBodyType(inputUser.getBodyType());
        if (inputUser.getDiet() != null)
            userDoc.setDiet(inputUser.getDiet());
        if (inputUser.getDrinks() != null)
            userDoc.setDrinks(inputUser.getDrinks());
        if (inputUser.getEducation() != null)
            userDoc.setEducation(inputUser.getEducation());
        if (inputUser.getEthnicity() != null)
            userDoc.setEthnicity(inputUser.getEthnicity());
        if (inputUser.getHeight() != null)
            userDoc.setHeight(inputUser.getHeight());
        if (inputUser.getIncome() != null)
            userDoc.setIncome(inputUser.getIncome());
        if (inputUser.getJob() != null)
            userDoc.setJob(inputUser.getJob());
        if (inputUser.getOffspring() != null)
            userDoc.setOffspring(inputUser.getOffspring());
        if (inputUser.getPets() != null)
            userDoc.setPets(inputUser.getPets());
        if (inputUser.getSmokes() != null)
            userDoc.setSmokes(inputUser.getSmokes());
        if (inputUser.getSpeaks() != null)
            userDoc.setSpeaks(inputUser.getSpeaks());

        // Essay & Interest Matching Logic
        String newEssay = inputUser.getEssay0();
        if (newEssay != null && !newEssay.trim().isEmpty()) {
            userDoc.setEssay0(newEssay); // Essay

            // Extract new interests
            java.util.List<String> foundInterests = new java.util.ArrayList<>(
                    interestExtractorService.extractInterestsFromText(newEssay));

            userDoc.setInterests(foundInterests);
            updateInterests = true;
        }

        // City & State Logic
        String newCity = inputUser.getCity();
        String newState = inputUser.getState();
        boolean cityChanged = newCity != null && !newCity.trim().isEmpty();
        boolean stateChanged = newState != null && !newState.trim().isEmpty();

        if (cityChanged || stateChanged) {
            String targetCityName = cityChanged ? newCity : userDoc.getCity();
            String targetStateName = stateChanged ? newState : userDoc.getState();

            if (targetCityName == null || targetStateName == null) {
                throw new Exception("City and State must be both present to update location.");
            }

            userDoc.setCity(targetCityName);
            userDoc.setState(targetStateName);
            updateLocation = true;
        }

        // Update Neo4j first
        try {
            if (updateBasicInfo) {
                userNeo4jRepository.updateBasicProfile(id, userDoc.getAge(), userDoc.getSex(),
                        userDoc.getOrientation());
            }
            if (updateInterests) {
                userNeo4jRepository.updateInterests(id, userDoc.getInterests());
            }
            if (updateLocation) {
                userNeo4jRepository.updateLocation(id, userDoc.getCity(), userDoc.getState());
            }
        } catch (Exception e) {
            System.err.println("CRITICAL: Graph Sync Failed. Aborting MongoDB update. Error: " + e.getMessage());
            throw new Exception("Failed to update profile due to Graph Database error.");
        }
        // Save to Mongo
        userMongoRepository.save(userDoc);
    }

    // Add a review in MongoDB
    @org.springframework.transaction.annotation.Transactional
    public void addReview(String authorId, String targetId, ReviewDocument reviewInput) throws Exception {

        // Check input
        if (authorId.equals(targetId)) {
            throw new Exception("Cannot review himself.");
        }

        // Check if targetId is in MATCHEDs
        boolean isMatched = userNeo4jRepository.areMatched(authorId, targetId);

        if (!isMatched) {
            throw new Exception("Cannot review an User not matched.");
        }

        // Check if target user is already been reviewed
        UserDocument authorDoc = userMongoRepository.findById(authorId)
                .orElseThrow(() -> new Exception("MongoDB User profile not found."));

        if (authorDoc.getReviewsMade() != null) {
            for (UserDocument.ReviewSummary summary : authorDoc.getReviewsMade()) {
                if (summary.getTargetId().equals(targetId)) {
                    throw new Exception("Already reviewed. Cannot review a user twice.");
                }
            }
        }

        Integer score = reviewInput.getRating();

        // Check if rating is valid
        if (score == null || score < 1 || score > 5) {
            throw new Exception("Rating must be between 1 and 5.");
        }

        // Save on Collection 'reviews'
        reviewInput.setId(null); // Genera nuovo ID
        reviewInput.setTargetId(targetId);
        reviewInput.setDate(LocalDateTime.now());

        ReviewDocument savedReview = reviewMongoRepository.save(reviewInput);

        // Update UserDocument (add a 'reviewsMade')
        UserDocument.ReviewSummary summary = new UserDocument.ReviewSummary();
        summary.setReviewId(savedReview.getId());
        summary.setTargetId(targetId);
        summary.setRating(savedReview.getRating());

        if (authorDoc.getReviewsMade() == null) {
            authorDoc.setReviewsMade(new java.util.ArrayList<>());
        }

        authorDoc.getReviewsMade().add(summary);

        // Save Mongo
        userMongoRepository.save(authorDoc);
    }

    // Likes a User
    @org.springframework.transaction.annotation.Transactional
    public String likeUser(String actorId, String targetId) throws Exception {
        if (actorId.equals(targetId)) {
            throw new Exception("Cannot like himself.");
        }
        if (userNeo4jRepository.areMatched(actorId, targetId)) {
            return "Already matched.";
        }

        boolean targetLikesActor = userNeo4jRepository.hasLiked(targetId, actorId); // bi-directional like ?
        if (targetLikesActor) {
            // MATCH!
            userNeo4jRepository.transformLikeToMatch(actorId, targetId);
            return "It's a Match!";
        } else {
            userNeo4jRepository.createLikeRelationship(actorId, targetId);
            return "Like send.";
        }
    }

    // Dislikes a User
    @org.springframework.transaction.annotation.Transactional
    public void dislikeUser(String actorId, String targetId) throws Exception {
        if (actorId.equals(targetId)) {
            throw new Exception("Cannot dislike himself.");
        }

        userNeo4jRepository.createDislikeRelationship(actorId, targetId);
    }

    // Recommendations with filters on location ('City' or 'State') and age
    public List<String> getRecommendations(String userId, String filter, int minAge, int maxAge) {
        // We pass the filter string directly ('City' or 'State') to the query
        return userNeo4jRepository.findRecommendations(userId, filter, minAge, maxAge);
    }
}
