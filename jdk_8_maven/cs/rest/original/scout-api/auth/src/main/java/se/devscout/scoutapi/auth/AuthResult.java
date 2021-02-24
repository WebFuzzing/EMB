package se.devscout.scoutapi.auth;

import se.devscout.scoutapi.model.User;

import java.security.Principal;

public class AuthResult implements Principal {
    private String authenticator;
    private User user;

    public AuthResult(String authenticator, User user) {
        this.authenticator = authenticator;
        this.user = user;
    }

    public String getAuthenticator() {
        return authenticator;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getName() {
        return user.getName();
    }
}
