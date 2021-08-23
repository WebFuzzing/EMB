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
package patio.voting.repositories.internal;

import io.micronaut.data.annotation.Repository;
import javax.persistence.EntityManager;
import patio.common.domain.utils.OffsetPaginationRequest;
import patio.common.domain.utils.OffsetPaginationResult;
import patio.group.domain.Group;
import patio.infrastructure.persistence.MicroBaseRepository;
import patio.voting.domain.VotingStats;
import patio.voting.repositories.VotingStatsRepository;

/** Persistence implementation access for {@link VotingStats} */
@Repository
public abstract class MicroVotingStatsRepository extends MicroBaseRepository
    implements VotingStatsRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroVotingStatsRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public OffsetPaginationResult<VotingStats> findStatsByGroup(
      Group group, OffsetPaginationRequest paginationRequest) {

    var value =
        "SELECT vs "
            + "FROM Voting v JOIN v.stats vs "
            + "WHERE v.group = :group "
            + "ORDER BY vs.createdAtDateTime DESC";

    var valueQuery =
        getEntityManager()
            .createQuery(value, VotingStats.class)
            .setParameter("group", group)
            .setFirstResult(paginationRequest.getOffset())
            .setMaxResults(paginationRequest.getMax());

    var count = "SELECT COUNT(vs) " + "FROM Voting v JOIN v.stats vs " + "WHERE v.group = :group ";

    var countQuery = getEntityManager().createQuery(count, Long.class).setParameter("group", group);

    return new OffsetPaginationResult<>(
        countQuery.getSingleResult().intValue(),
        paginationRequest.getOffset(),
        valueQuery.getResultList());
  }
}
