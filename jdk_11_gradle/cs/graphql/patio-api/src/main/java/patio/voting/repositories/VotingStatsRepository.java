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
package patio.voting.repositories;

import io.micronaut.data.annotation.Query;
import io.micronaut.data.repository.PageableRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import patio.common.domain.utils.OffsetPaginationRequest;
import patio.common.domain.utils.OffsetPaginationResult;
import patio.group.domain.Group;
import patio.voting.domain.VotingStats;

/**
 * Handles database operations over {@link VotingStats} instances
 *
 * @since 0.1.0
 */
public interface VotingStatsRepository extends PageableRepository<VotingStats, UUID> {

  /**
   * Gets the moving average for a {@link Group}
   *
   * @param group the group to get its moving average from
   * @param interval the interval from which the average is referred to
   * @return the moving average
   */
  @Query(
      "SELECT AVG(vs.average) "
          + "FROM Voting v JOIN v.stats vs "
          + "WHERE v.group = :group "
          + "AND v.createdAtDateTime > :interval "
          + "AND vs.average is not null")
  Optional<Double> findMovingAverageByGroup(Group group, OffsetDateTime interval);

  /**
   * Finds all statistics for a given group between a time interval
   *
   * @param group the id of the group to get its statistics from
   * @param paginationRequest pagination information
   * @return a paginated result of {@link VotingStats} instances from the given {@link Group}
   */
  OffsetPaginationResult<VotingStats> findStatsByGroup(
      Group group, OffsetPaginationRequest paginationRequest);
}
