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

import static patio.common.domain.utils.Check.checkIsTrue;

import io.micronaut.context.annotation.Value;
import java.time.OffsetDateTime;
import java.util.Optional;
import javax.inject.Singleton;
import patio.common.domain.utils.Check;
import patio.common.domain.utils.Result;
import patio.infrastructure.utils.ErrorConstants;
import patio.user.domain.User;

/**
 * This checker expects the user's otp has not expired, otherwise it will return a failing {@link
 * Result}
 *
 * @since 0.1.0
 */
@Singleton
public class OtpExpiredForUser {

  private final transient Integer otpExpiryMinutes;

  /**
   * Initializes the checker with required values from configuration
   *
   * @param otpExpiryMinutes minutes taken from configuration
   */
  public OtpExpiredForUser(@Value("${otp.expirytime.minutes:none}") Integer otpExpiryMinutes) {
    this.otpExpiryMinutes = otpExpiryMinutes;
  }

  /**
   * Checks the user's opt has not expired. Otherwise will build a failing {@link Result} containing
   * an error {@link ErrorConstants#OTP_EXPIRED_FOR_USER}
   *
   * @param user {@link User} to check her otp code for
   * @return a failing {@link Result} if the user's otp has expired
   * @since 0.1.0
   */
  public Check check(Optional<User> user) {
    var operationDate = OffsetDateTime.now();
    boolean hasExpired =
        user.map(User::getOtpCreationDateTime)
            .map(
                creationDate ->
                    operationDate.isBefore(creationDate.plusMinutes(this.otpExpiryMinutes)))
            .orElse(false);

    return checkIsTrue(hasExpired, ErrorConstants.OTP_EXPIRED_FOR_USER);
  }
}
