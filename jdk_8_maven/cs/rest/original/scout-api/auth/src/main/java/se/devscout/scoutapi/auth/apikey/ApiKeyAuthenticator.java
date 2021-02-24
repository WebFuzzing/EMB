package se.devscout.scoutapi.auth.apikey;


import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.model.IdentityType;
import se.devscout.scoutapi.model.User;

import java.util.Optional;

public class ApiKeyAuthenticator extends AbstractAuthenticator implements Authenticator<String, AuthResult> {

    public static final String ID = "ApiKey";
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyAuthenticator.class);

    public ApiKeyAuthenticator(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Optional<AuthResult> authenticate(String credentials) throws AuthenticationException {
        LOGGER.info("Request authentication started");
        User user = getUserDao().readUserByIdentity(IdentityType.API, credentials);
        Optional<AuthResult> result;
        if (user != null) {
            LOGGER.debug("Authenticated user " + user.getName() + " using API key " + credentials);
//            result = Optional.fromNullable(new AuthResult(ID, user));
            result = Optional.ofNullable(new AuthResult(ID, user));
        } else {
            LOGGER.info("Failed to authenticate user using API key " + credentials);
//            result = Optional.absent();
            result = Optional.empty();
        }
        closeSessionIfNecessary();
        return result;
    }
}
