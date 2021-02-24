package se.devscout.scoutapi.auth.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

public class Checker {

    private static final Logger LOGGER = LoggerFactory.getLogger(Checker.class);
    private final List<String> acceptedApplicationIds;
    private final String mAudience;
    private final GoogleIdTokenVerifier mVerifier;
    private final JsonFactory mJFactory;
    public Checker(String audience, List<String> acceptedApplicationIds) {
        this.acceptedApplicationIds = acceptedApplicationIds;
        mAudience = audience;
        NetHttpTransport transport = new NetHttpTransport();
        mJFactory = new GsonFactory();
        mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
    }

    public GoogleIdToken.Payload check(String tokenString) {
        GoogleIdToken.Payload payload = null;
        try {
            GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
            if (mVerifier.verify(token)) {
                GoogleIdToken.Payload tempPayload = token.getPayload();
                if (!tempPayload.getAudience().equals(mAudience)) {
                    LOGGER.info("Audience mismatch");
                } else if (acceptedApplicationIds != null && !acceptedApplicationIds.isEmpty() && !acceptedApplicationIds.contains(tempPayload.getAuthorizedParty())) {
                    LOGGER.info("Client ID mismatch");
                } else {
                    payload = tempPayload;
                }
            }
        } catch (GeneralSecurityException e) {
            LOGGER.info("Security issue: " + e.getLocalizedMessage());
        } catch (IOException e) {
            LOGGER.info("Network problem: " + e.getLocalizedMessage());
        }
        return payload;
    }
}
