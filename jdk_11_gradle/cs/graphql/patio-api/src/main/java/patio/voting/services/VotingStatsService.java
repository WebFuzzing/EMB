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
package patio.voting.services;

import patio.common.domain.utils.OffsetPaginationRequest;
import patio.common.domain.utils.OffsetPaginationResult;
import patio.common.domain.utils.PaginationResult;
import patio.group.domain.Group;
import patio.voting.domain.Voting;
import patio.voting.domain.VotingStats;
import patio.voting.graphql.GetStatsByGroupInput;

/**
 * Business logic contracts regarding persisted voting statistics
 *
 * @since 0.1.0
 */
public interface VotingStatsService {

  /**
   * Calculates the statistics for a voting, persisting its values
   *
   * @param voting the {@link Voting} from which calculates its statistics
   */
  void createVotingStat(Voting voting);

  /**
   * Calculates the average statistic for a voting, persisting its value
   *
   * @param voting the {@link Voting} from which calculate its average
   */
  void updateAverage(Voting voting);

  /**
   * Calculates the moving average statistic for a voting, persisting its value
   *
   * @param voting the {@link Voting} from which calculate its moving average
   */
  void updateMovingAverage(Voting voting);

  /**
   * Get the {@link Group}'s statistics according to its votings performed between the date times
   *
   * @param input the required input object to get the statistics
   * @param paginationRequest the {@link OffsetPaginationRequest} to present the recovered
   *     statistics
   * @return a {@link PaginationResult} of the {@link VotingStats} instances
   */
  OffsetPaginationResult<VotingStats> getVotingStatsByGroup(
      GetStatsByGroupInput input, OffsetPaginationRequest paginationRequest);
}
