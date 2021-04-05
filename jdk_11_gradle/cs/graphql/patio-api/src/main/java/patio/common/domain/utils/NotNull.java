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

import static patio.common.domain.utils.Check.checkIsTrue;

import patio.infrastructure.utils.ErrorConstants;

/**
 * This checker expects the argument passed to be not null, otherwise it will return a failing
 * {@link Result}
 *
 * @since 0.1.0
 */
public class NotNull {

  /**
   * Checks that the argument passed as parameter is not null. Otherwise will build a failing {@link
   * Result} containing an error {@link ErrorConstants#NOT_FOUND}
   *
   * @param object the object checked
   * @return a failing {@link Result} if the object is null
   * @since 0.1.0
   */
  public Check check(Object object) {
    return checkIsTrue(object != null, ErrorConstants.NOT_FOUND);
  }
}
