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
 * Input to change the password for a user
 *
 * @since 0.1.0
 */
public class ChangePasswordInput {

  private final String otpCode;
  private final String password;

  /**
   * Creates an input instance with the required dependencies
   *
   * @param otpCode the OTP code to change the password
   * @param password the new password
   */
  public ChangePasswordInput(String otpCode, String password) {
    this.otpCode = otpCode;
    this.password = password;
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

  /**
   * Returns the otp code used for changing the password
   *
   * @return the otp code used for changing the password
   */
  public String getOtpCode() {
    return otpCode;
  }
}
