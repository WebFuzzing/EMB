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
package patio.common.domain.utils;

import java.util.Optional;

/**
 * Abstraction to chain several assertions and return when the first error happens
 *
 * @since 0.1.0
 */
public final class Check {
  private final transient Error error;

  private Check(Error error) {
    this.error = error;
  }

  /**
   * ifMatches the condition is not true, then the resulting {@link Check} will contain the error
   * passed as an argument.
   *
   * @param cond expected condition to be true
   * @param error the result in case the condition is not met
   * @return an instance of {@link Check}
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.NullAssignment")
  public static Check checkIsTrue(boolean cond, Error error) {
    return new Check(cond ? null : error);
  }

  /**
   * ifMatches the condition is not false, then the resulting {@link Check} will contain the error
   * passed as an argument.
   *
   * @param cond expected condition to be false
   * @param error the result in case the condition is not met
   * @return an instance of {@link Check}
   * @since 0.1.0
   */
  public static Check checkIsFalse(boolean cond, Error error) {
    return checkIsTrue(!cond, error);
  }

  /**
   * Returns the wrapped error
   *
   * @return the wrapped {@link Error}
   * @since 0.1.0
   */
  public Error getError() {
    return this.error;
  }

  /**
   * Returns whether or not it has a wrapped error
   *
   * @return true if it's an error false otherwise
   * @since 0.1.0
   */
  public boolean hasError() {
    return Optional.ofNullable(error).isPresent();
  }
}
