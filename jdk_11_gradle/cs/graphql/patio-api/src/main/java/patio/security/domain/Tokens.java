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
package patio.security.domain;

import patio.common.domain.utils.Builder;

/**
 * Represents a normal JWT login response with an authentication and a refresh tokens
 *
 * @since 0.1.0
 */
public class Tokens {
  private String authenticationToken;
  private String refreshToken;

  /**
   * Creates a builder to build a {@link Tokens} instance
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder<Tokens> builder() {
    return Builder.build(Tokens::new);
  }

  /**
   * Returns the authentication token
   *
   * @return the authentication token
   * @since 0.1.0
   */
  public String getAuthenticationToken() {
    return authenticationToken;
  }

  /**
   * Returns the refresh token
   *
   * @return the refresh token
   * @since 0.1.0
   */
  public String getRefreshToken() {
    return refreshToken;
  }

  /**
   * Sets the authentication token
   *
   * @param refreshToken the refresh token
   * @since 0.1.0
   */
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  /**
   * Sets the authentication token
   *
   * @param authenticationToken the authentication token
   * @since 0.1.0
   */
  public void setAuthenticationToken(String authenticationToken) {
    this.authenticationToken = authenticationToken;
  }
}
