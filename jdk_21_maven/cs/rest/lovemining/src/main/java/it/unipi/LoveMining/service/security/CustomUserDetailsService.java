package it.unipi.LoveMining.service.security;

import it.unipi.LoveMining.model.mongo.UserDocument;
import it.unipi.LoveMining.repository.mongo.UserMongoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMongoRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Find the user in the DB using the email
        UserDocument appUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // 2. Define the role: If isAdmin is true -> "ADMIN", otherwise "USER"
        String role = Boolean.TRUE.equals(appUser.getIsAdmin()) ? "ADMIN" : "USER";

        // 3. Return the user to Spring Security
        return User.builder()
                .username(appUser.getEmail())
                .password(appUser.getPassword()) // The password in the DB must be encrypted (BCrypt)
                .roles(role)
                .build();
    }
}