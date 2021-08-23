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
package patio.voting.services.internal;

import static patio.common.domain.utils.Check.checkIsTrue;

import patio.common.domain.utils.Check;
import patio.common.domain.utils.Result;
import patio.infrastructure.utils.ErrorConstants;

/**
 * Checks that a given vote's score is within the expected values
 *
 * @since 0.1.0
 */
public class VoteScoreBoundaries {

  /**
   * Checks if a given vote matches the score rules
   *
   * @param score the vote's score
   * @return a failing {@link Result} if the score doesn't match the rule
   */
  public Check check(Integer score) {
    return checkIsTrue(score != null && score >= 1 && score <= 5, ErrorConstants.SCORE_IS_INVALID);
  }
}
