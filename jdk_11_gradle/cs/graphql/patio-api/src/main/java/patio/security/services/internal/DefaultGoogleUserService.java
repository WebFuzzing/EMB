/*
 * Copyright (C) 2019 Kaleidos Open Source SL
 *
 * This file is part of PATIO.
 * PATIO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * PATIO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with PATIO.  If not, see <https://www.gnu.org/licenses/>
 */
package patio.security.services.internal;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.oauth2.model.Userinfoplus;
import java.io.IOException;
import java.util.Optional;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.security.services.GoogleUserService;
import patio.user.domain.User;

/**
 * Default implementation of the {@link GoogleUserService} by using Google's java api
 *
 * @since 0.1.0
 */
@Singleton
public class DefaultGoogleUserService implements GoogleUserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultGoogleUserService.class);

  @Override
  public Optional<User> loadFromAccessToken(String accessToken) {
    GoogleCredential credential = new GoogleCredential().setAccessToken(accessToken);
    Oauth2 oauth2 =
        new Oauth2.Builder(new NetHttpTransport(), new JacksonFactory(), credential).build();

    Optional<User> user = Optional.empty();

    try {
      Userinfoplus userinfoplus = oauth2.userinfo().get().execute();
      user =
          Optional.of(
              User.builder()
                  .with(u -> u.setEmail(userinfoplus.getEmail()))
                  .with(u -> u.setName(userinfoplus.getName()))
                  .build());
    } catch (IOException e) {
      LOGGER.error("error while getting user's information", e);
    }

    return user;
  }
}
