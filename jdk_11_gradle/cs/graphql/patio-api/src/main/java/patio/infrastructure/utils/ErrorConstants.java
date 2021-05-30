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
package patio.infrastructure.utils;

import patio.common.domain.utils.Error;

/**
 * Class holding general error values
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.LongVariable")
public final class ErrorConstants {

  /**
   * Code used when an unauthenticated request tries to access an authenticated resource
   *
   * @since 0.1.0
   */
  public static final Error BAD_CREDENTIALS =
      new Error("API_ERRORS.BAD_CREDENTIALS", "Provided credentials are not valid");

  /**
   * Code used when a non-admin user tries to perform an admin action on a group
   *
   * @since 0.1.0
   */
  public static final Error NOT_AN_ADMIN =
      new Error("API_ERRORS.NOT_AN_ADMIN", "The user is not an admin on the group");

  /**
   * Code used when an element referenced by an id can be found
   *
   * @since 0.1.0
   */
  public static final Error NOT_FOUND =
      new Error("API_ERRORS.NOT_FOUND", "The element can be found");

  /**
   * Code used when somebody tries to add an user to a group in which the user was already a member
   *
   * @since 0.1.0
   */
  public static final Error USER_ALREADY_ON_GROUP = // NOPMD
      new Error("API_ERRORS.USER_ALREADY_ON_GROUP", "The user is already on the group");

  /**
   * Code used when somebody not belonging to a group tries to execute some operation over that
   * group
   *
   * @since 0.1.0
   */
  public static final Error USER_NOT_IN_GROUP =
      new Error("API_ERRORS.USER_NOT_IN_GROUP", "The user doesn't belong to group");

  /**
   * Generic code used when somebody is not allowed to do something
   *
   * @since 0.1.0
   */
  public static final Error NOT_ALLOWED = new Error("API_ERRORS.NOT_ALLOWED", "Not allowed");

  /**
   * Code used when somebody not belonging to a group tries to execute some operation over that
   * group
   *
   * @since 0.1.0
   */
  public static final Error USER_ALREADY_VOTE =
      new Error("API_ERRORS.USER_ALREADY_VOTE", "The user has already voted");

  /**
   * Code used when somebody tries to create a vote in a voting slot already expired
   *
   * @since 0.1.0
   */
  public static final Error VOTING_HAS_EXPIRED =
      new Error("API_ERRORS.VOTING_HAS_EXPIRED", "The voting has expired");

  /**
   * Code used when somebody tries to create a vote without a score between 1 and 5
   *
   * @since 0.1.0
   */
  public static final Error SCORE_IS_INVALID =
      new Error("API_ERRORS.SCORE_IS_INVALID", "The score must be an integer between 1 and 5");

  /**
   * Code used when try to vote anonymously on a group that doesn't admit it group
   *
   * @since 0.1.0
   */
  public static final Error VOTE_CANT_BE_ANONYMOUS =
      new Error("API_ERRORS.VOTE_CANT_BE_ANONYMOUS", "The group doesn't allow anonymous votes");

  /**
   * Code used when the unique admin of a group tries to leave it
   *
   * @since 0.1.0
   */
  public static final Error UNIQUE_ADMIN =
      new Error("API_ERRORS.UNIQUE_ADMIN", "The user is the unique admin of the group");

  /**
   * Error code used when a user is trying to reuse the exact password it's already defined group
   *
   * @since 0.1.0
   */
  public static final Error SAME_PASSWORD =
      new Error("API_ERRORS.PASSWORD_IS_THE_SAME", "The password is the same already defined");

  /**
   * Error code used when a non-blank string is left blank group
   *
   * @since 0.1.0
   */
  public static final Error BLANK_PASSWORD =
      new Error("API_ERRORS.PASSWORD_IS_BLANK", "The password cannot be left blank");

  /**
   * Error code used when an otp is expired
   *
   * @since 0.1.0
   */
  public static final Error OTP_EXPIRED_FOR_USER =
      new Error("API_ERRORS.OTP_EXPIRED_FOR_USER", "The otp has expired");

  private ErrorConstants() {
    /* empty */
  }
}
