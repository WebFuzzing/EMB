package it.unipi.LoveMining.service.authentication;

import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.unipi.LoveMining.model.mongo.UserDocument;
import it.unipi.LoveMining.model.neo4j.CityNode;
import it.unipi.LoveMining.model.neo4j.InterestNode;
import it.unipi.LoveMining.model.neo4j.StateNode;
import it.unipi.LoveMining.model.neo4j.UserNode;
import it.unipi.LoveMining.repository.mongo.UserMongoRepository;
import it.unipi.LoveMining.repository.neo4j.CityNeo4jRepository;
import it.unipi.LoveMining.repository.neo4j.InterestNeo4jRepository;
import it.unipi.LoveMining.repository.neo4j.StateNeo4jRepository;
import it.unipi.LoveMining.repository.neo4j.UserNeo4jRepository;
import it.unipi.LoveMining.service.utility.InterestExtractorService; // Interests
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
// Service class for User registration and authentication
public class AuthenticationService {

    private final UserMongoRepository userMongoRepository;
    private final UserNeo4jRepository userNeo4jRepository;
    private final InterestNeo4jRepository interestNeo4jRepository;
    private final CityNeo4jRepository cityNeo4jRepository;
    private final StateNeo4jRepository stateNeo4jRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterestExtractorService interestExtractorService;

    // Registers a new user in both MongoDB and Neo4j databases
    public UserDocument registerUser(UserDocument user) {

        user.setId(null); // id null
        user.setIsAdmin(false); // isAdmin false
        user.setReviewsMade(null); // reviews null
        user.setInterests(null); // interests null
        if (user.getEmail() == null || !user.getEmail().contains("@")) { // email
            throw new IllegalArgumentException("Email is missing or invalid.");
        }
        if (userMongoRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use.");
        }
        if (user.getPassword() == null || user.getPassword().length() < 4) { // password
            throw new IllegalArgumentException("Password must be at least 4 characters long.");
        }
        if (user.getAge() == null || user.getAge() < 18 || user.getAge() > 100) { // age
            throw new IllegalArgumentException("Age must be between 18 and 100.");
        }
        if (user.getSex() == null || (!user.getSex().equals("m") && !user.getSex().equals("f"))) { // ( "m" / "f")
            throw new IllegalArgumentException("Sex must be exactly 'm' or 'f'.");
        }
        if (user.getOrientation() == null || // ("straight", "gay", "bisexual")
                (!user.getOrientation().equals("straight") &&
                        !user.getOrientation().equals("gay") &&
                        !user.getOrientation().equals("bisexual"))) {
            throw new IllegalArgumentException("Orientation must be 'straight', 'gay', or 'bisexual'.");
        }
        // status: not mandatory (unknown if is null), but if it is present: one of the following
        if(user.getStatus() != null) {
            if (!user.getStatus().equalsIgnoreCase("available") &&
                    !user.getStatus().equalsIgnoreCase("single") &&
                    !user.getStatus().equalsIgnoreCase("seeing someone") &&
                    !user.getStatus().equalsIgnoreCase("married") &&
                    !user.getStatus().equalsIgnoreCase("unknown"))
                throw new IllegalArgumentException("Status must be 'available', 'single', 'seeing someone', 'married' or 'unknown'");
        }
        else
            user.setStatus("unknown");

        if (user.getCity() == null || user.getCity().trim().isEmpty()) { // City
            throw new IllegalArgumentException("The 'city' field is mandatory.");
        }
        if (user.getState() == null || user.getState().trim().isEmpty()) { // State
            throw new IllegalArgumentException("The 'state' field is mandatory.");
        }

        // city-state lowercase
        user.setCity(user.getCity().trim().toLowerCase());
        user.setState(user.getState().trim().toLowerCase());

        // Interest Extraction from essay0
        Set<String> totalInterests = new HashSet<>();

        // Analyze Essay0 to find keyword
        if (user.getEssay0() != null && !user.getEssay0().isEmpty()) {
            Set<String> extractedInterests = interestExtractorService.extractInterestsFromText(user.getEssay0());
            totalInterests.addAll(extractedInterests);
        }
        user.setInterests(new ArrayList<>(totalInterests));

        user.setPassword(passwordEncoder.encode(user.getPassword())); // password Bcrypt
        user.setId(new org.bson.types.ObjectId().toHexString()); // id mongo assign

        // MongoDB
        UserDocument savedUser = userMongoRepository.save(user);

        // Sync to Neo4j
        try {
            UserNode userNode = new UserNode();
            userNode.setId(savedUser.getId());
            userNode.setAge(savedUser.getAge());
            userNode.setSex(savedUser.getSex());
            userNode.setOrientation(savedUser.getOrientation());

            // Handle Interests
            if (savedUser.getInterests() != null) {
                Set<InterestNode> interestNodes = new HashSet<>();
                for (String interestName : savedUser.getInterests()) {
                    InterestNode interestNode = interestNeo4jRepository.findById(interestName)
                            .orElse(new InterestNode(interestName));
                    interestNodes.add(interestNode);
                }
                userNode.setInterests(interestNodes);
            }

            // Handle City and State
            if (savedUser.getCity() != null && !savedUser.getCity().isEmpty()) {
                CityNode cityNode = cityNeo4jRepository.findById(savedUser.getCity())
                        .orElse(new CityNode(savedUser.getCity(), null));

                if (savedUser.getState() != null && !savedUser.getState().isEmpty()) {
                    StateNode stateNode = stateNeo4jRepository.findById(savedUser.getState())
                            .orElse(new StateNode(savedUser.getState()));

                    stateNode = stateNeo4jRepository.save(stateNode);
                    cityNode.setState(stateNode);
                }

                cityNode = cityNeo4jRepository.save(cityNode);
                userNode.setCityNode(cityNode);
            }

            userNeo4jRepository.save(userNode);

        } catch (Exception e) {
            // Rollback
            System.err.println("Rollback: Neo4j sync failed. Deleting user from MongoDB. Error: " + e.getMessage());
            userMongoRepository.deleteById(savedUser.getId());
            throw new RuntimeException("Registration failed due to database synchronization error. Please try again.");
        }
        return savedUser;
    }
}
