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
package patio.security.graphql;

/**
 * Authentication input. It contains credentials to authenticate a given user.
 *
 * @since 0.1.0
 */
public class LoginInput {

  private final String email;
  private final String password;

  /**
   * Initializes the input with the user's email and password
   *
   * @param email user's email
   * @param password user's password
   * @since 0.1.0
   */
  public LoginInput(String email, String password) {
    this.email = email;
    this.password = password;
  }

  /**
   * Returns input's email
   *
   * @return input's email
   * @since 0.1.0
   */
  public String getEmail() {
    return email;
  }

  /**
   * Returns the input's password
   *
   * @return input password
   * @since 0.1.0
   */
  public String getPassword() {
    return password;
  }
}
