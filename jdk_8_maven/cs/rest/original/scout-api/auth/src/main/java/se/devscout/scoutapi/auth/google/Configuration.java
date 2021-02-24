package se.devscout.scoutapi.auth.google;

import java.util.List;

public class Configuration {
    private String clientId;
    private String clientSecret;
    private List<String> acceptedApplicationIds;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public List<String> getAcceptedApplicationIds() {
        return acceptedApplicationIds;
    }
}
