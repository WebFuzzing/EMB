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

import patio.user.domain.User;

/**
 * Information delivered when a used authenticates successfully in the system
 *
 * @since 0.1.0
 */
public class Login {
  private final Tokens tokens;
  private final User user;

  /**
   * Initializes a login instance
   *
   * @param tokens the user's tokens
   * @param user the user's general information
   * @since 0.1.0
   */
  public Login(Tokens tokens, User user) {
    this.tokens = tokens;
    this.user = user;
  }

  /**
   * Returns the user's tokens
   *
   * @return the generated tokens for user
   * @since 0.1.0
   */
  public Tokens getTokens() {
    return tokens;
  }

  /**
   * @return the user's information
   * @since 0.1.0
   */
  public User getUser() {
    return user;
  }
}
