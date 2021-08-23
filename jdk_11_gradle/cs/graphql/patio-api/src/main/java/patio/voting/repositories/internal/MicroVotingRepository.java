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
import java.math.BigDecimal;
import java.util.Optional;
import javax.persistence.EntityManager;
import patio.infrastructure.persistence.MicroBaseRepository;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;
import patio.voting.repositories.VotingRepository;

/** Persistence implementation access for {@link Voting} and {@link Vote} */
@Repository
public abstract class MicroVotingRepository extends MicroBaseRepository
    implements VotingRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroVotingRepository(EntityManager entityManager) {
    super(entityManager);
  }

  @Override
  public Optional<Long> getAvgVoteCountByVoting(Voting voting) {
    var subquery =
        "select "
            + "count(vo.*) as counter, "
            + "v.id "
            + "from voting v join vote vo on "
            + "vo.voting_id = v.id "
            + "join groups g on "
            + "v.group_id = g.id "
            + "where g.id = ? "
            + "group by v.id";

    var query = " select round(avg(x.counter)) from (" + subquery + ") x";
    var nativeQuery = getEntityManager().createNativeQuery(query);

    BigDecimal bigDecimal =
        (BigDecimal) nativeQuery.setParameter(1, voting.getGroup().getId()).getSingleResult();

    return Optional.ofNullable(bigDecimal).map(BigDecimal::longValue);
  }
}
