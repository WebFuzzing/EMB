package se.devscout.scoutapi.auth.google;

//import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.devscout.scoutapi.auth.AuthResult;
import se.devscout.scoutapi.auth.apikey.AbstractAuthenticator;
import se.devscout.scoutapi.model.IdentityType;
import se.devscout.scoutapi.model.User;

import java.util.Optional;

public class GoogleAuthenticator extends AbstractAuthenticator implements Authenticator<String, AuthResult> {

    public static final String ID = "Google";
    private static final Logger LOGGER = LoggerFactory.getLogger(GoogleAuthenticator.class);
    private final Configuration configuration;

    public GoogleAuthenticator(SessionFactory sessionFactory, Configuration configuration) {
        super(sessionFactory);
        this.configuration = configuration;
    }

    @Override
    public Optional<AuthResult> authenticate(String credentials) throws AuthenticationException {
        LOGGER.info("Request authentication started");
        String idToken = credentials;
        String accessToken = null;

        Verify.VerificationResponse idStatus = new Verify(
                configuration.getClientId(),
                configuration.getAcceptedApplicationIds()
        ).verify(idToken, accessToken);

        LOGGER.info("Id token verification result: {}", idStatus.id_token_status.message);

        Optional<AuthResult> result;

        if (idStatus.id_token_status.valid) {
            LOGGER.info("Google id token is valid");
            Transaction tx = createTransaction();
            String gplusId = idStatus.id_token_status.gplus_id;
            User user = getUserDao().readUserByIdentity(IdentityType.GOOGLE, gplusId);
            if (user == null) {
                LOGGER.info("User needs to be added to system.");
                User newUser = new User();
                String name = idStatus.id_token_status.getName();
                String email = idStatus.id_token_status.getEmail();
                newUser.setName(name != null ? name : (email != null ? email : "scout"));
                newUser.setEmailAddress(email);
                newUser.addIdentity(IdentityType.GOOGLE, gplusId);
                getUserDao().create(newUser);

                user = newUser;
                LOGGER.info("New user will be added to system.");
            }
            if (!user.isApiKeySet()) {
                user.addIdentity(IdentityType.API, RandomStringUtils.randomAlphanumeric(50));
                getUserDao().update(user);
                LOGGER.info("API key created for user {}.", user.getName());
            }
            tx.commit();
//            result = Optional.fromNullable(new AuthResult(ID, user));
            result = Optional.ofNullable(new AuthResult(ID, user));
        } else {
            LOGGER.info("Invalid id token");
//            result = Optional.absent();
            result = Optional.empty();
        }
        closeSessionIfNecessary();
        return result;
    }
}
