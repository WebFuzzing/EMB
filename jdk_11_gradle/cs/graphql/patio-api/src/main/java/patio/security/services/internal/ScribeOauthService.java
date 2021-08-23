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

import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.oauth.OAuth20Service;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.security.services.OauthService;

/**
 * Default implementation of an Oauth2 service to get required access tokens from a previous step in
 * oauth2 authorization flow.
 *
 * @since 0.1.0
 */
@Singleton
public class ScribeOauthService implements OauthService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ScribeOauthService.class);
  private final transient OAuth20Service oAuth20Service;

  /**
   * Default constructor
   *
   * @param oAuth20Service implementation used
   * @since 0.1.0
   */
  @Inject
  public ScribeOauthService(OAuth20Service oAuth20Service) {
    this.oAuth20Service = oAuth20Service;
  }

  @Override
  public Optional<String> getAccessToken(String authorizationCode) {
    Optional<String> accessToken = Optional.empty();
    try {
      accessToken =
          Optional.of(oAuth20Service.getAccessToken(authorizationCode))
              .map(OAuth2AccessToken::getAccessToken);
    } catch (IOException | InterruptedException | ExecutionException e) {
      LOGGER.error("error while getting access token from auth code");
    }

    return accessToken;
  }
}
