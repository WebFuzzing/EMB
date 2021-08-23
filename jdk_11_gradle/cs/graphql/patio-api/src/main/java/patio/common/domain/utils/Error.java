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

/**
 * Represents a business logic error. It should be used and passed as a value.
 *
 * @since 0.1.0
 */
public class Error {
  private final String code;
  private final String message;

  /**
   * Initializes the error with a code and a developer friendly message
   *
   * @param code i18n code
   * @param message developer friendly message
   * @since 0.1.0
   */
  public Error(String code, String message) {
    this.code = code;
    this.message = message;
  }

  /**
   * Returns error code
   *
   * @return a code that can be used as i18n code
   * @since 0.1.0
   */
  public String getCode() {
    return code;
  }

  /**
   * Returns error message
   *
   * @return the error developer friendly message
   * @since 0.1.0
   */
  public String getMessage() {
    return message;
  }
}
