package it.unipi.LoveMining.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;

import it.unipi.LoveMining.model.mongo.UserDocument;
import it.unipi.LoveMining.model.mongo.ReviewDocument;
import it.unipi.LoveMining.service.user.UserService;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.Authentication;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // Retrieves a user by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserDocument> getUser(@PathVariable String id) {
        UserDocument user = userService.getUser(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Retrieves the authenticated user's profile
    @GetMapping
    public ResponseEntity<?> getMe(Authentication authentication) {
        // 1. Check if user is ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.status(403).body("ADMINs cannot access user profiles.");
        }

        // 2. Retrieve user by Email (Principal in Basic Auth is the username/email)
        String email = authentication.getName();
        UserDocument user = userService.getUserByEmail(email);

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Retrieves the matches for the authenticated user
    @GetMapping("/matches")
    public ResponseEntity<?> getMatches(Authentication authentication) {
        // 1. Check if user is ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.status(403).body("ADMINs cannot access user matches.");
        }

        String email = authentication.getName();
        UserDocument user = userService.getUserByEmail(email); // Need ID from Mongo Doc first
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        java.util.Set<it.unipi.LoveMining.model.neo4j.UserNode> matches = userService.getUserMatches(user.getId());
        return ResponseEntity.ok(matches);
    }

    // Retrieves the reviews made by the authenticated user
    @GetMapping("/reviews")
    public ResponseEntity<?> getReviews(org.springframework.security.core.Authentication authentication) {
        // 1. Check if user is ADMIN
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return ResponseEntity.status(403).body("ADMINs cannot access user reviews.");
        }

        String email = authentication.getName();
        UserDocument user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        java.util.List<UserDocument.ReviewSummary> reviews = userService.getUserReviews(user.getId());

        if (reviews == null) {
            return ResponseEntity.ok(java.util.Collections.emptyList());
        }
        return ResponseEntity.ok(reviews);
    }

    // Update user profile
    @PatchMapping("/me")
    public ResponseEntity<?> updateUser(Authentication authentication,
                                        @RequestBody UserDocument inputUser) {
        String email = authentication.getName();

        // Retrieve current user to check role/id
        UserDocument currentUser = userService.getUserByEmail(email);

        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }

        if (currentUser.getIsAdmin() != null && currentUser.getIsAdmin()) {
            return ResponseEntity.status(403).body("Admins cannot have a profile to update.");
        }

        try {
            userService.updateUser(currentUser.getId(), inputUser);
            return ResponseEntity.ok("User profile updated successfully.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Update failed: " + e.getMessage());
        }
    }

    // Create a review
    @PostMapping("/{id}/review")
    public ResponseEntity<?> createReview(Authentication authentication, @PathVariable String id, @RequestBody ReviewDocument review) {

        // 1. Check
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) {
            return ResponseEntity.status(403).body("ADMINs cannot create reviews.");
        }

        // 2. Author
        String email = authentication.getName();
        UserDocument author = userService.getUserByEmail(email);

        if (author == null) { return ResponseEntity.notFound().build(); }

        try {
            // 3. Call logic (Match check + Insert)
            userService.addReview(author.getId(), id, review);
            return ResponseEntity.ok("Review created successfully.");
        } catch (Exception e) {
            // Errors (es. "Not matched", "User not found")
            return ResponseEntity.badRequest().body("Error creating review: " + e.getMessage());
        }
    }

    // Likes
    @PostMapping("/{id}/like")
    public ResponseEntity<?> likeUser(Authentication authentication, @PathVariable String id) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return ResponseEntity.status(403).body("ADMINs cannot like users.");

        String email = authentication.getName();
        UserDocument author = userService.getUserByEmail(email);
        if (author == null) return ResponseEntity.notFound().build();

        try {
            String result = userService.likeUser(author.getId(), id);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error in like: " + e.getMessage());
        }
    }

    // Dislikes
    @PostMapping("/{id}/dislike")
    public ResponseEntity<?> dislikeUser(Authentication authentication, @PathVariable String id) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return ResponseEntity.status(403).body("ADMINs cannot dislike users.");

        String email = authentication.getName();
        UserDocument author = userService.getUserByEmail(email);
        if (author == null) return ResponseEntity.notFound().build();

        try {
            userService.dislikeUser(author.getId(), id);
            return ResponseEntity.ok("Dislike send.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error in dislike: " + e.getMessage());
        }
    }

    // Recommendations with filters
    @GetMapping("/recommendations/{filters}")
    public ResponseEntity<?> getRecommendations(
            Authentication authentication,
            @RequestParam String locationFilter,
            @RequestParam int minAge,
            @RequestParam int maxAge
    ) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return ResponseEntity.status(403).body("ADMINs cannot dislike users.");

        if (!locationFilter.equals("City") && !locationFilter.equals("State")) { // Expects "City" or "State"
            return ResponseEntity.badRequest().body("Invalid filter. Use 'City' or 'State'.");
        }

        String email = authentication.getName();
        UserDocument user = userService.getUserByEmail(email);
        if (user == null) return ResponseEntity.notFound().build();

        List<String> recommendations = userService.getRecommendations(user.getId(), locationFilter, minAge, maxAge);

        if (recommendations.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(recommendations);
    }
}
