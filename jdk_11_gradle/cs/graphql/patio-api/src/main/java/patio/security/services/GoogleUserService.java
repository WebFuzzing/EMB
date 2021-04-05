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
import patio.user.domain.User;

/**
 * Service to access user's Google basic information
 *
 * @since 0.1.0
 */
public interface GoogleUserService {

  /**
   * Loads a given's user information from a previously acquired access token
   *
   * @param accessToken Google's access token
   * @return a basic {@link User}'s information
   * @since 0.1.0
   */
  Optional<User> loadFromAccessToken(String accessToken);
}
