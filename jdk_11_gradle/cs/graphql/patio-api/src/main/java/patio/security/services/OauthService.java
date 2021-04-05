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
package patio.security.services;

import java.util.Optional;

/**
 * Service to complete authorization workflow. Workflow has started in the front-end getting the
 * authorization code and the service completes the flow by getting an access token from the
 * previous authorization code.
 *
 * @since 0.1.0
 */
public interface OauthService {

  /**
   * Gets an access token from a previously acquired authorization code
   *
   * @param authorizationCode authorization code acquired in oauth2 previous steps
   * @return an access token
   * @since 0.1.0
   */
  Optional<String> getAccessToken(String authorizationCode);
}
