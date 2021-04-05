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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;
import patio.group.domain.Group;
import patio.user.domain.User;
import patio.voting.domain.VoteByMoodDTO;
import patio.voting.domain.Voting;

/**
 * Handles database operations over {@link Voting} instances
 *
 * @since 0.1.0
 */
@SuppressWarnings({"PMD.TooManyMethods"})
public interface VotingRepository extends PageableRepository<Voting, UUID> {

  /**
   * Finds a {@link Voting} by id of the voting and a given {@link User} who is supposed to belong
   * to that {@link Voting}
   *
   * @param votingId the id of the {@link Voting}
   * @param user the user who created it
   * @return the {@link Voting} instance
   */
  @Query(
      "SELECT v FROM Voting v JOIN v.group g1, UserGroup ug WHERE ug.group = g1 AND ug.user = :user AND v.id = :votingId ")
  Optional<Voting> findByIdAndVotingUser(UUID votingId, User user);

  /**
   * Lists votings on a group, from startDate to endDate
   *
   * @param group group identifier
   * @param startDate the date from which the votings are wanted
   * @param endDate the date to which the votings are wanted
   * @return a list of votings that belongs to a group
   * @since 0.1.0
   */
  Stream<Voting> findAllByGroupAndCreatedAtDateTimeBetween(
      Group group, OffsetDateTime startDate, OffsetDateTime endDate);

  /**
   * Finds the vote average from a {@link Voting}
   *
   * @param voting voting to get the average vote
   * @return the average value of the voting's scores
   */
  @Query("SELECT AVG(v.score) FROM Vote v WHERE v.voting = :voting")
  Integer findVoteAverage(Voting voting);

  /**
   * Finds the last voting that belongs to a group
   *
   * @param group the group to find the last voting from
   * @return the last group's voting
   */
  Optional<Voting> findByGroupOrderByCreatedAtDateTimeDesc(Group group);

  /**
   * Returns the aggregation of votes by mood of a given {@link Voting}
   *
   * @param voting the voting we want the aggregation from
   * @return a list of {@link VoteByMoodDTO} instances
   */
  @Query(
      "SELECT new patio.voting.domain.VoteByMoodDTO(COUNT(vo), vo.score) FROM Vote vo WHERE vo.voting = :voting GROUP BY vo.score ORDER BY vo.score DESC")
  List<VoteByMoodDTO> findAllVotesByMood(Voting voting);

  /**
   * Returns what is the number of people usually voting in the given {@link Voting} group
   *
   * @param voting the voting we want the average number of people voting
   * @return the average number of people voting in this voting's group
   */
  Optional<Long> getAvgVoteCountByVoting(Voting voting);

  /**
   * Returns the previous voting to a given date that belongs a group
   *
   * @param date the OffsetDateTime to be considered
   * @param group the Group to be considered
   * @return the previous voting
   */
  @Query(
      "SELECT v "
          + "FROM Voting v "
          + "WHERE v.group = :group "
          + "AND v.createdAtDateTime < :date "
          + "ORDER BY v.createdAtDateTime DESC")
  Optional<Voting> getPreviousVotingByGroupAndDate(Group group, OffsetDateTime date);

  /**
   * Returns the next voting to a given date that belongs a group
   *
   * @param date the OffsetDateTime to be considered
   * @param group the Group to be considered
   * @return the next Voting
   */
  @Query(
      "SELECT v "
          + "FROM Voting v "
          + "WHERE v.group = :group "
          + "AND v.createdAtDateTime > :date "
          + "ORDER BY v.createdAtDateTime ASC")
  Optional<Voting> getNextVotingByGroupAndDate(Group group, OffsetDateTime date);
}
