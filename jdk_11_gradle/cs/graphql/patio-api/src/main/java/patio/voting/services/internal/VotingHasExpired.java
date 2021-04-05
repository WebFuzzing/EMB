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

import static patio.common.domain.utils.Check.checkIsFalse;
import static patio.infrastructure.utils.ErrorConstants.VOTING_HAS_EXPIRED;

import java.util.Optional;
import patio.common.domain.utils.Check;
import patio.common.domain.utils.Result;
import patio.voting.domain.Voting;

/**
 * Checks whether a voting has expired or not
 *
 * @since 0.1.0
 */
public class VotingHasExpired {

  /**
   * Checks if the voting has expired or not
   *
   * @param voting the voting
   * @return a failing {@link Result} if the voting has expired
   */
  public Check check(Optional<Voting> voting) {
    boolean hasExpired = voting.map(Voting::getExpired).orElse(true);

    return checkIsFalse(hasExpired, VOTING_HAS_EXPIRED);
  }
}
