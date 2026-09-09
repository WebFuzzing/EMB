package it.unipi.LoveMining.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.unipi.LoveMining.model.mongo.UserDocument;
import it.unipi.LoveMining.service.authentication.AuthenticationService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/authentication")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    // Registers a new user
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDocument user) {
        try {
            UserDocument savedUser = authenticationService.registerUser(user);
            return ResponseEntity.ok("User registered succesfully" + savedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
