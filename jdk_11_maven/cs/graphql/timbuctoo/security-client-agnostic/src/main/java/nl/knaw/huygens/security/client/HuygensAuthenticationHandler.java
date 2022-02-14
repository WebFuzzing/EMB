package nl.knaw.huygens.security.client;

/*
 * #%L
 * Security Client
 * =======
 * Copyright (C) 2013 - 2014 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.net.HttpHeaders;
import nl.knaw.huygens.security.client.model.HuygensSecurityInformation;
import nl.knaw.huygens.security.client.model.HuygensSessionImpl;
import nl.knaw.huygens.security.client.model.SecurityInformation;
import nl.knaw.huygens.security.core.model.HuygensSession;

import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;
import static nl.knaw.huygens.security.core.rest.API.REFRESH_PATH;
import static nl.knaw.huygens.security.core.rest.API.SESSION_AUTHENTICATION_URI;

public class HuygensAuthenticationHandler implements AuthenticationHandler {

    private final HttpCaller client;
    private final String authorizationUrl;
    private final String basicCredentials;

    public HuygensAuthenticationHandler(HttpCaller client, String authorizationUrl, String basicCredentials) {
        this.client = checkNotNull(client);
        this.basicCredentials = checkNotNull(basicCredentials);
        if (authorizationUrl == null || authorizationUrl.equals("")) {
            throw new IllegalArgumentException("Authorization url was empty");
        }

        this.authorizationUrl = authorizationUrl;
    }

    @Override
    public SecurityInformation getSecurityInformation(String sessionToken) throws UnauthorizedException, IOException {
        HuygensSession session = doSessionDetailsRequest(sessionToken);
        doSessionRefresh(sessionToken);

        return new HuygensSecurityInformation(session.getOwner());
    }

    private void doSessionRefresh(String sessionToken) throws UnauthorizedException, IOException {
        if (authorizationUrl == null || authorizationUrl.equals("")) {
            throw new UnauthorizedException();
        }
        client.call(sessionRequest("PUT", sessionToken).withExtraPath(REFRESH_PATH));
    }

    private HuygensSession doSessionDetailsRequest(String sessionToken) throws UnauthorizedException, IOException {
        if (authorizationUrl == null || authorizationUrl.equals("")) {
            throw new UnauthorizedException("No authorization url specified");
        }

        ActualResultWithBody<HuygensSession> response = client.call(sessionRequest("GET", sessionToken), HuygensSessionImpl.class);

        int statusType = response.getStatus();
        if (statusType == 200) {
            return response.getBody().get();
        }
        else {
            throw new UnauthorizedException("status was: " + statusType);
        }
    }

    private HttpRequest sessionRequest(String verb, String sessionToken) {
        HttpRequest resource = new HttpRequest(verb, authorizationUrl)
          .withExtraPath(SESSION_AUTHENTICATION_URI)
          .withExtraPath("/" + sessionToken)
          .withHeader(HttpHeaders.AUTHORIZATION, basicCredentials);

        return resource;
    }
}
