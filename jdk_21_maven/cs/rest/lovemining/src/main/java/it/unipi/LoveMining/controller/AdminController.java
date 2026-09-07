package it.unipi.LoveMining.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import it.unipi.LoveMining.model.mongo.UserDocument;
import it.unipi.LoveMining.model.mongo.ReviewDocument;
import it.unipi.LoveMining.service.admin.AdminService;

import java.util.Map;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // Deletes a user by ID
    @DeleteMapping("/users/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable String id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok("[OK] User deleted successfully");
    }

    // Search a user by ID
    @GetMapping("/user/{id}")
    public ResponseEntity<UserDocument> getUser(@PathVariable String id) {
        UserDocument user = adminService.getUserById(id); // Call the service to get the user

        // Check if the user exists
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build(); // Return "404 Not Found" if the user doesn't exist
        }
    }

    // Search a review by ID
    @GetMapping("/userReviews/{id}")
    public ResponseEntity<ReviewDocument> getReview(@PathVariable String id) {
        ReviewDocument review = adminService.getReviewById(id); // Call the service to get the review

        // Check if the review exists
        if (review != null) {
            return ResponseEntity.ok(review);
        } else {
            return ResponseEntity.notFound().build(); // Return "404 Not Found" if the review doesn't exist
        }
    }

    //Analytics: Love Points
    @GetMapping("/analytics/love-points")
    public ResponseEntity<List<Map<String, Object>>> getLovePointsAnalytic(@RequestParam String state) {
        List<Map<String, Object>> stats = adminService.getLovePointsStats(state);
        if (stats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(stats);
    }

    //Analytics: Glow-up
    @GetMapping("/analytics/glow-up")
    public ResponseEntity<List<Map<String, Object>>> getGlowUpAnalytics() {

        List<Map<String, Object>> bestUsers = adminService.getBestGlowUpUsers();

        if (bestUsers.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(bestUsers);
    }

    //Analytics: Unhappy Cities
    @GetMapping("/analytics/unhappy-cities")
    public ResponseEntity<List<Map<String, Object>>> getUnhappyCitiesAnalytics() {

        List<Map<String, Object>> bestCities = adminService.getUnhappyCities();

        if (bestCities.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(bestCities);
    }

    // Analytics: Status by Age Group
    @GetMapping("/analytics/status-by-age-group")
    public ResponseEntity<List<Map<String, Object>>> getStatusAnalytics() {

        List<Map<String, Object>> stats = adminService.getStatusAnalytics();

        if (stats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(stats);
    }

    // Analytics: Orientation by Age Group
    @GetMapping("/analytics/orientation-by-age-group")
    public ResponseEntity<List<Map<String, Object>>> getOrientationAnalytics() {

        List<Map<String, Object>> stats = adminService.getOrientationAnalytics();

        if (stats.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(stats);
    }
}

