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

import static patio.infrastructure.utils.OptionalUtils.combine;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.common.domain.utils.NotPresent;
import patio.common.domain.utils.PaginationRequest;
import patio.common.domain.utils.PaginationResult;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.domain.UserGroupKey;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.internal.UserIsInGroup;
import patio.infrastructure.utils.ErrorConstants;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.voting.domain.Vote;
import patio.voting.domain.VoteByMoodDTO;
import patio.voting.domain.Voting;
import patio.voting.domain.VotingStats;
import patio.voting.graphql.CreateVoteInput;
import patio.voting.graphql.CreateVotingInput;
import patio.voting.graphql.GetLastVotingInput;
import patio.voting.graphql.GetVotingInput;
import patio.voting.graphql.ListVotingsGroupInput;
import patio.voting.graphql.UserVotesInGroupInput;
import patio.voting.graphql.VotingStatsInput;
import patio.voting.repositories.VoteRepository;
import patio.voting.repositories.VotingRepository;
import patio.voting.services.VotingService;
import patio.voting.services.VotingStatsService;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultVotingService implements VotingService {
  private final transient VotingRepository votingRepository;
  private final transient VoteRepository voteRepository;
  private final transient VotingStatsService votingStatsService;
  private final transient UserGroupRepository userGroupRepository;
  private final transient UserRepository userRepository;
  private final transient GroupRepository groupRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param votingRepository an instance of {@link VotingRepository}
   * @param voteRepository an instance of {@link VoteRepository}
   * @param votingStatsService an instance of {@link VotingStatsService}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param groupRepository an instance of {@link GroupRepository}
   * @since 0.1.0
   */
  public DefaultVotingService(
      VotingRepository votingRepository,
      VoteRepository voteRepository,
      VotingStatsService votingStatsService,
      UserGroupRepository userGroupRepository,
      UserRepository userRepository,
      GroupRepository groupRepository) {
    this.votingRepository = votingRepository;
    this.voteRepository = voteRepository;
    this.votingStatsService = votingStatsService;
    this.userGroupRepository = userGroupRepository;
    this.userRepository = userRepository;
    this.groupRepository = groupRepository;
  }

  @Override
  public Result<Voting> createVoting(CreateVotingInput input) {
    var userGroupKey = new UserGroupKey(input.getUserId(), input.getGroupId());
    var userGroupOptional = userGroupRepository.findById(userGroupKey);

    NotPresent notPresent = new NotPresent();

    return Result.<Voting>create()
        .thenCheck(() -> notPresent.check(userGroupOptional, ErrorConstants.USER_NOT_IN_GROUP))
        .then(() -> createVotingIfSuccess(userGroupOptional));
  }

  private Voting createVotingIfSuccess(Optional<UserGroup> userGroup) {
    Optional<Voting> voting =
        userGroup.map(
            (UserGroup ug) -> {
              return Voting.newBuilder()
                  .with(v -> v.setGroup(ug.getGroup()))
                  .with(v -> v.setCreatedBy(ug.getUser()))
                  .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now()))
                  .build();
            });

    return voting.map(votingRepository::save).orElse(null);
  }

  @Override
  public Result<Vote> createVote(CreateVoteInput input) {
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<Voting> voting = votingRepository.findById(input.getVotingId());
    Optional<Group> group = voting.map(Voting::getGroup);
    Boolean isGroupAnonymous = group.map(Group::isAnonymousVote).orElse(false);

    var voteScoreBoundaries = new VoteScoreBoundaries();
    var userOnlyVotedOnce = new UserOnlyVotedOnce(voteRepository);
    var votingHasExpired = new VotingHasExpired();
    var notPresent = new NotPresent();
    var userIsInGroup = new UserIsInGroup();
    var anonymousAllowed = new VoteAnonymousAllowedInGroup();

    return Result.<Vote>create()
        .thenCheck(() -> voteScoreBoundaries.check(input.getScore()))
        .thenCheck(() -> userOnlyVotedOnce.check(user, voting))
        .thenCheck(() -> votingHasExpired.check(voting))
        .thenCheck(() -> notPresent.check(group))
        .thenCheck(() -> userIsInGroup.check(user, group))
        .thenCheck(() -> anonymousAllowed.check(input.isAnonymous(), isGroupAnonymous))
        .then(createVote(voting, user, input))
        .sideEffect(
            (v) -> {
              votingStatsService.updateAverage(v.getVoting());
              votingStatsService.updateMovingAverage(v.getVoting());
            });
  }

  private Supplier<Vote> createVote(
      Optional<Voting> voting, Optional<User> user, CreateVoteInput input) {
    return () ->
        voting
            .map(
                (Voting slot) ->
                    Vote.newBuilder()
                        .with(v -> v.setVoting(slot))
                        .with(v -> v.setCreatedBy(user.orElse(null)))
                        .with(v -> v.setComment(input.getComment()))
                        .with(v -> v.setHueMood(input.getHueMood()))
                        .with(v -> v.setScore(input.getScore()))
                        .build())
            .map(voteRepository::save)
            .orElse(null);
  }

  @Override
  public List<Voting> listVotingsGroup(ListVotingsGroupInput input) {
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    OffsetDateTime fromDate = input.getStartDate();
    OffsetDateTime toDate = input.getEndDate();

    return group.stream()
        .flatMap(
            g -> votingRepository.findAllByGroupAndCreatedAtDateTimeBetween(g, fromDate, toDate))
        .collect(Collectors.toList());
  }

  @Override
  public PaginationResult<Vote> listVotesVoting(UUID votingId, PaginationRequest pagination) {
    var pageable = Pageable.from(pagination.getPage(), pagination.getMax());
    var votingOptional = votingRepository.findById(votingId);
    var page =
        votingOptional
            .map(
                voting -> voteRepository.findByVotingOrderByCreatedAtDateTimeDesc(voting, pageable))
            .orElse(Page.empty());

    return PaginationResult.from(page);
  }

  @Override
  public Result<Voting> getVoting(GetVotingInput input) {
    Optional<User> user = userRepository.findById(input.getCurrentUserId());
    Optional<UUID> votingId = votingRepository.findById(input.getVotingId()).map(Voting::getId);
    Optional<Voting> votingFound =
        combine(votingId, user).flatmapInto(votingRepository::findByIdAndVotingUser);

    NotPresent notPresent = new NotPresent();

    return Result.<Voting>create()
        .thenCheck(() -> notPresent.check(votingFound))
        .then(votingFound::get);
  }

  @Override
  public Result<Voting> getLastVotingByGroup(GetLastVotingInput input) {
    Optional<User> user = userRepository.findById(input.getCurrentUserId());
    Optional<Group> group = groupRepository.findById(input.getGroupId());

    Optional<Voting> votingFound = getLastVoting(group);
    var userIsInGroup = new UserIsInGroup();
    var notPresent = new NotPresent();

    return Result.<Voting>create()
        .thenCheck(() -> notPresent.check(group))
        .thenCheck(() -> userIsInGroup.check(user, group))
        .thenCheck(() -> notPresent.check(votingFound))
        .then(votingFound::get);
  }

  @Override
  public Optional<Voting> getLastVoting(Optional<Group> group) {
    return group.flatMap(votingRepository::findByGroupOrderByCreatedAtDateTimeDesc);
  }

  @Override
  public Result<List<Vote>> listUserVotesInGroup(UserVotesInGroupInput input) {
    Optional<User> currentUser = userRepository.findById(input.getCurrentUserId());
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<Group> group = groupRepository.findById(input.getGroupId());

    UserIsInGroup userIsInGroup = new UserIsInGroup();

    return Result.<List<Vote>>create()
        .thenCheck(() -> userIsInGroup.check(currentUser, group))
        .thenCheck(() -> userIsInGroup.check(user, group))
        .then(() -> listUserVotesInGroupIfSuccess(input));
  }

  @Override
  public Result<Boolean> didUserVotedInVoting(User user, UUID votingId) {
    boolean voted =
        votingRepository
            .findById(votingId)
            .flatMap(voting -> voteRepository.findByCreatedByAndVoting(user, voting))
            .isPresent();

    return Result.result(voted);
  }

  @Override
  public Result<Map<String, Object>> getVotingStats(VotingStatsInput input) {
    var optionalVoting = votingRepository.findById(input.getVotingId());

    var voteByMoodDTOList =
        optionalVoting
            .map(votingRepository::findAllVotesByMood)
            .flatMap((list) -> Optional.of(completeList(list)))
            .orElse(List.of());

    var maxExpectedVotes =
        optionalVoting.map(voteRepository::getMaxExpectedVoteCountByVoting).orElse(0L);

    var voteCountByVoting = optionalVoting.map(voteRepository::getVoteCountByVoting).orElse(0L);
    var voteCountAverage =
        optionalVoting.flatMap(votingRepository::getAvgVoteCountByVoting).orElse(0L);
    var optionalStats = optionalVoting.map(Voting::getStats);
    var votingAverage = optionalStats.map(VotingStats::getAverage);
    var votingMovingAverage = optionalStats.map(VotingStats::getMovingAverage);
    var standardDeviation = getStandardDeviation(votingAverage, votingMovingAverage);
    var votingStatsDate = optionalStats.map(VotingStats::getCreatedAtDateTime);

    Map<String, Object> votingStats =
        Map.of(
            "votesByMood",
            voteByMoodDTOList,
            "maxVoteCountExpected",
            maxExpectedVotes,
            "voteCount",
            voteCountByVoting,
            "voteCountAverage",
            voteCountAverage,
            "average",
            votingAverage,
            "movingAverage",
            votingMovingAverage,
            "createdAtDateTime",
            votingStatsDate,
            "standardDeviation",
            standardDeviation);

    return Result.result(votingStats);
  }

  @Override
  public Result<Voting> getNextVoting(UUID votingId) {
    Optional<Voting> optionalVoting = votingRepository.findById(votingId);
    Optional<OffsetDateTime> votingDateTime =
        optionalVoting.map(v -> v.getCreatedAtDateTime().plus(1, ChronoUnit.SECONDS));
    Optional<Group> votingGroup = optionalVoting.map(Voting::getGroup);
    Optional<Voting> nextVoting =
        combine(votingGroup, votingDateTime)
            .flatmapInto(votingRepository::getNextVotingByGroupAndDate);

    return Result.from(nextVoting);
  }

  @Override
  public Result<Voting> getPreviousVoting(UUID votingId) {
    Optional<Voting> voting = votingRepository.findById(votingId);
    Optional<OffsetDateTime> votingDateTime = voting.map(Voting::getCreatedAtDateTime);
    Optional<Group> votingGroup = voting.map(Voting::getGroup);
    Optional<Voting> previousVoting =
        combine(votingGroup, votingDateTime)
            .flatmapInto(votingRepository::getPreviousVotingByGroupAndDate);

    return Result.from(previousVoting);
  }

  private List<VoteByMoodDTO> completeList(List<VoteByMoodDTO> fromDatabase) {
    var votesByMoodMap =
        fromDatabase.stream()
            .collect(Collectors.toMap(VoteByMoodDTO::getMood, Function.identity()));

    return Stream.of(1, 2, 3, 4, 5)
        .map((Integer idx) -> votesByMoodMap.getOrDefault(idx, new VoteByMoodDTO(0, idx)))
        .sorted(Comparator.comparingInt(VoteByMoodDTO::getMood).reversed())
        .collect(Collectors.toList());
  }

  private List<Vote> listUserVotesInGroupIfSuccess(UserVotesInGroupInput input) {
    Optional<User> user = userRepository.findById(input.getUserId());
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    OffsetDateTime fromDate = input.getStartDateTime();
    OffsetDateTime toDate = input.getEndDateTime();

    return combine(user, group)
        .into(
            (u, g) -> {
              return voteRepository.findAllByUserAndGroupAndCreatedAtBetween(
                  u, g, fromDate, toDate);
            })
        .stream()
        .flatMap(voteStream -> voteStream)
        .collect(Collectors.toList());
  }

  private Optional<Object> getStandardDeviation(
      Optional<Double> average, Optional<Double> movingAverage) {
    return movingAverage.map(
        movAvg ->
            average.map(
                avg -> {
                  var percentage = (avg / movAvg - 1) * 100;
                  return Math.round(percentage * 100) / 100d;
                }));
  }
}
