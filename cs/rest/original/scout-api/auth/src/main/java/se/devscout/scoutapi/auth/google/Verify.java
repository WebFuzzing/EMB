/*
 * Copyright 2013 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package se.devscout.scoutapi.auth.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Tokeninfo;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.List;

/**
 * Simple server to demonstrate token verification.
 *
 * @author cartland@google.com (Chris Cartland)
 */
public class Verify {
    /**
     * Replace this with the client ID you got from the Google APIs console.
     */
    private String clientId = "563244170192-0cqja2c5pcm0m0beiro2e3r72jvogqkq.apps.googleusercontent.com";
    /**
     * Default HTTP transport to use to make HTTP requests.
     */
    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    /**
     * Default JSON factory to use to deserialize JSON.
     */
    private static final JacksonFactory JSON_FACTORY = new JacksonFactory();
    /**
     * Gson object to serialize JSON responses to requests to this servlet.
     */
    private static final Gson GSON = new Gson();

    private List<String> acceptedApplicationIds;

    public Verify(String clientId, List<String> acceptedApplicationIds) {
        this.clientId = clientId;
        this.acceptedApplicationIds = acceptedApplicationIds;
    }

    public VerificationResponse verify(String idToken, String accessToken) {
        TokenStatus idStatus = new TokenStatus();
        if (idToken != null) {
            // Check that the ID Token is valid.

            Checker checker = new Checker(clientId, acceptedApplicationIds);
            GoogleIdToken.Payload jwt = checker.check(idToken);

            if (jwt == null) {
                // This is not a valid token.
                idStatus.setValid(false);
                idStatus.setId("");
                idStatus.setMessage("Invalid ID Token.");
            } else {
                idStatus.setValid(true);
                String gplusId = (String) jwt.get("sub");
                idStatus.setId(gplusId);
                idStatus.setEmail(jwt.getEmail());
                idStatus.setName((String) jwt.get("name"));
                idStatus.setMessage("ID Token is valid.");
            }
        } else {
            idStatus.setMessage("ID Token not provided");
        }

        TokenStatus accessStatus = new TokenStatus();
        if (accessToken != null) {
            // Check that the Access Token is valid.
            try {
                GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
                Oauth2 oauth2 = new Oauth2.Builder(TRANSPORT, JSON_FACTORY, credential).build();
                Tokeninfo tokenInfo = oauth2.tokeninfo()
                        .setAccessToken(accessToken).execute();
                if (tokenInfo.containsKey("error")) {
                    // This is not a valid token.
                    accessStatus.setValid(false);
                    accessStatus.setId("");
                    accessStatus.setMessage("Invalid Access Token.");
                } else if (!tokenInfo.getIssuedTo().equals(clientId)) {
                    // This is not meant for this app. It is VERY important to check
                    // the client ID in order to prevent man-in-the-middle attacks.
                    accessStatus.setValid(false);
                    accessStatus.setId("");
                    accessStatus.setMessage("Access Token not meant for this app.");
                } else {
                    accessStatus.setValid(true);
                    accessStatus.setId(tokenInfo.getUserId());
                    accessStatus.setMessage("Access Token is valid.");
                }
            } catch (IOException e) {
                accessStatus.setValid(false);
                accessStatus.setId("");
                accessStatus.setMessage("Invalid Access Token.");
            }
        } else {
            accessStatus.setMessage("Access Token not provided");
        }

        VerificationResponse tokenStatus = new VerificationResponse(idStatus, accessStatus);
        return tokenStatus;
    }

    /**
     * JSON representation of a token's status.
     */
    public static class TokenStatus {
        public boolean valid;
        public String gplus_id;
        public String message;
        private String email;
        private String name;

        public TokenStatus() {
            valid = false;
            gplus_id = "";
            message = "";
        }

        public void setValid(boolean v) {
            this.valid = v;
        }

        public void setId(String gplus_id) {
            this.gplus_id = gplus_id;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    /**
     * JSON response to verification request.
     * <p/>
     * Example JSON response:
     * {
     * "id_token_status": {
     * "info": "12345",
     * "valid": True
     * },
     * "access_token_status": {
     * "Access Token not meant for this app.",
     * "valid": False
     * }
     * }
     */
    public static class VerificationResponse {
        public TokenStatus id_token_status;
        public TokenStatus access_token_status;

        private VerificationResponse(TokenStatus _id_token_status, TokenStatus _access_token_status) {
            this.id_token_status = _id_token_status;
            this.access_token_status = _access_token_status;
        }

        public static VerificationResponse newVerificationResponse(TokenStatus id_token_status,
                                                                   TokenStatus access_token_status) {
            return new VerificationResponse(id_token_status,
                    access_token_status);
        }
    }
}
