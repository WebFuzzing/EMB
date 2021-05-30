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
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.repository.PageableRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import patio.group.domain.Group;
import patio.user.domain.User;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;

/** Handles database access for {@link Vote} entities */
public interface VoteRepository extends PageableRepository<Vote, UUID> {

  /**
   * Returns the average voting score of a given voting
   *
   * @param voting the voting this vote belongs to
   * @return the avg score value
   */
  Double findAvgScoreByVoting(Voting voting);

  /**
   * Finds a vote created by some {@link User} in some {@link Voting}
   *
   * @param createdBy the user who created the vote
   * @param voting the voting the vote belongs
   * @return an optional {@link Vote}
   */
  Optional<Vote> findByCreatedByAndVoting(User createdBy, Voting voting);

  /**
   * Finds all votes a given {@link User} in a given {@link Group} between two dates
   *
   * @param user the user the votes you want to get
   * @param group the group the votes are created about
   * @param fromDate date lower range limit
   * @param toDate date upper range limit
   * @return a {@link Stream} of {@link Vote} of the user
   */
  @Query(
      "SELECT v FROM Vote v JOIN v.voting vo WHERE "
          + "v.createdBy = :user AND "
          + "vo.group = :group AND "
          + "v.createdAtDateTime BETWEEN :fromDate AND :toDate")
  Stream<Vote> findAllByUserAndGroupAndCreatedAtBetween(
      User user, Group group, OffsetDateTime fromDate, OffsetDateTime toDate);

  /**
   * Finds all votes of a given voting
   *
   * @param votingId the id of the voting to get the votes from
   * @param pageable the information to paginate over the result set
   * @return a paginated result of {@link Vote} instances from the given {@link Voting}
   */
  Page<Vote> findByVotingOrderByCreatedAtDateTimeDesc(Voting votingId, Pageable pageable);

  /**
   * Returns the maximum number of votes there could be in a voting
   *
   * @param voting the voting we are asking the result for
   * @return the maximum number of votes there could be in a voting
   */
  @Query("SELECT COUNT(u) FROM Voting v JOIN v.group.users u WHERE v = :voting")
  Long getMaxExpectedVoteCountByVoting(Voting voting);

  /**
   * Returns the current number of votes of a given {@link Voting}
   *
   * @param voting the voting we want the count from
   * @return the current number of votes of a given {@link Voting}
   */
  @Query("SELECT COUNT(vo) FROM Vote vo WHERE vo.voting = :voting")
  Long getVoteCountByVoting(Voting voting);
}
