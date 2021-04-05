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

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import patio.common.domain.utils.PaginationRequest;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.domain.UserGroupKey;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.infrastructure.utils.ErrorConstants;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.voting.domain.Vote;
import patio.voting.domain.VoteByMoodDTO;
import patio.voting.domain.Voting;
import patio.voting.graphql.CreateVoteInput;
import patio.voting.graphql.CreateVotingInput;
import patio.voting.graphql.GetLastVotingInput;
import patio.voting.graphql.GetVotingInput;
import patio.voting.graphql.ListVotingsGroupInput;
import patio.voting.graphql.VotingStatsInput;
import patio.voting.repositories.VoteRepository;
import patio.voting.repositories.VotingRepository;
import patio.voting.services.internal.DefaultVotingService;

/**
 * Tests {@link DefaultVotingService}
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class VotingServiceTests {

  @Test
  @DisplayName("Creating a group voting successfully")
  void testCreateVotingSuccessfully() {
    // given: a group and a user who wants to create a new voting slot
    var group = random(Group.class);
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), group))))
            .build();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var userGroupRepository = Mockito.mock(UserGroupRepository.class);

    // and: there're user and voting available
    when(userGroupRepository.findById(any(UserGroupKey.class)))
        .thenReturn(Optional.of(new UserGroup(user, group)));

    // when: invoking the service
    var votingService =
        new DefaultVotingService(votingRepository, null, null, userGroupRepository, null, null);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we should build the expected result
    assertNotNull(votingResult);
    assertEquals(0, votingResult.getErrorList().size());

    // and: that the voting repository creation has been invoked
    verify(userGroupRepository, times(1)).findById(any(UserGroupKey.class));
    verify(votingRepository, times(1)).save(any(Voting.class));
  }

  @Test
  @DisplayName("createVote: create vote successfully")
  void testCreateVoteSuccessfully() {
    // given: some mocked data
    var group = random(Group.class);
    var user =
        User.builder()
            .with(u -> u.setId(UUID.randomUUID()))
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), group))))
            .build();
    var voting =
        Voting.newBuilder()
            .with(v -> v.setId(UUID.randomUUID()))
            .with(v -> v.setGroup(group))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(5, ChronoUnit.MINUTES)))
            .with(v -> v.setExpired(false))
            .build();
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(user.getId())
            .withVotingId(voting.getId())
            .withHueMood("COVID 19")
            .withComment("The worst day ever")
            .withScore(1)
            .withAnonymous(false)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));
    when(voteRepository.save(any(Vote.class))).thenReturn(random(Vote.class));

    // when: invoking the service
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, userRepository, null);

    Result<Vote> vote = votingService.createVote(input);

    // then: vote has been created
    assertNotNull(vote.getSuccess(), "Successfully created vote");

    // and voting statistics are updated
    verify(votingStatsService, times(1)).updateMovingAverage(any());

    // and: all checkers have been called plus the creation
    verify(userRepository, times(1)).findById(any());
    verify(votingRepository, times(1)).findById(any());
    verify(voteRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Failing to create a group voting because user is not in group")
  void testCreateVotingFailureNotInGroup() {
    // given: a group and a user who wants to create a new voting slot
    var group = random(Group.class);
    var user = random(User.class);

    // and: some mocked repositories
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.save(any(Voting.class))).thenReturn(random(Voting.class));

    var userGroupRepository = mock(UserGroupRepository.class);
    when(userGroupRepository.findById(any())).thenReturn(Optional.empty());

    // when: invoking the service
    var votingService =
        new DefaultVotingService(
            votingRepository, null, votingStatsService, userGroupRepository, null, null);
    var votingInput =
        CreateVotingInput.newBuilder().withUserId(user.getId()).withGroupId(group.getId()).build();
    var votingResult = votingService.createVoting(votingInput);

    // then: we shouldn't build any successful
    assertNull(votingResult.getSuccess());

    // and: we should build errors
    assertEquals(1, votingResult.getErrorList().size());
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP, votingResult.getErrorList().get(0));

    // and: that the voting repository creation is NOT invoked
    verify(votingRepository, times(0)).save(any(Voting.class));

    // and no voting statistics are updated
    verify(votingStatsService, times(0)).updateMovingAverage(any());
  }

  @Test
  @DisplayName("createVote: user has already voted")
  void testCreateVoteFailsBecauseUserHasAlreadyVoted() {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var score = 1;
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var userRepository = Mockito.mock(UserRepository.class);
    var previousVote = Optional.of(Vote.newBuilder().build());

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Voting()));

    // and: user voted already in this voting
    when(voteRepository.findByCreatedByAndVoting(any(), any())).thenReturn(previousVote);

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_ALREADY_VOTE.getCode(), vote.getErrorList().get(0).getCode());

    // and no voting statistics are updated
    verify(votingStatsService, times(0)).updateMovingAverage(any());

    // and: just one checker has been called an no vote has been created
    verify(voteRepository, times(1)).findByCreatedByAndVoting(any(), any());
    verify(voteRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("createVote: voting has expired")
  void testCreateVoteFailsBecauseVotingExpired() {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var score = 1;
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voting =
        Voting.newBuilder()
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(2, ChronoUnit.DAYS)))
            .with(v -> v.setExpired(true))
            .build();

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));

    // and: user voted already in this voting
    when(voteRepository.findByCreatedByAndVoting(any(), any())).thenReturn(Optional.empty());

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.VOTING_HAS_EXPIRED.getCode(), vote.getErrorList().get(0).getCode());

    // and no voting statistics are updated
    verify(votingStatsService, times(0)).updateMovingAverage(any());

    // and: just two checker has been called an no vote has been created
    verify(userRepository, times(1)).findById(any(UUID.class));
    verify(votingRepository, times(1)).findById(any(UUID.class));
    verify(voteRepository, times(1)).findByCreatedByAndVoting(any(User.class), any(Voting.class));
    verify(votingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("createVote: user doesn't belong to group")
  void testCreateVoteFailsBecauseUserNotInGroup() {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var score = 1;
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    var validGroup = new Group();
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), new Group()))))
            .build();
    var voting =
        Voting.newBuilder()
            .with(v -> v.setGroup(validGroup))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(2, ChronoUnit.MINUTES)))
            .with(v -> v.setExpired(false))
            .build();

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP.getCode(), vote.getErrorList().get(0).getCode());

    // and no voting statistics are updated
    verify(votingStatsService, times(0)).updateMovingAverage(any());

    // and: three checkers has been called an no vote has been created
    verify(userRepository, times(1)).findById(any(UUID.class));
    verify(votingRepository, times(1)).findById(any(UUID.class));
    verify(voteRepository, times(1)).findByCreatedByAndVoting(any(User.class), any(Voting.class));
    verify(votingRepository, times(0)).save(any());
  }

  @ParameterizedTest(name = "Test createVote: invalid score [{0}]")
  @MethodSource("testCreateVoteFailBecauseInvalidScoreDataProvider")
  void testCreateVoteFailBecauseInvalidScore(Integer score) {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withVotingId(votingId)
            .withScore(score)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var votingRepository = Mockito.mock(VotingRepository.class);

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Voting()));

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then: vote can't be created
    assertNull(vote.getSuccess(), "No vote");
    assertEquals(1, vote.getErrorList().size(), "There is one error");
    assertEquals(ErrorConstants.SCORE_IS_INVALID.getCode(), vote.getErrorList().get(0).getCode());

    // and: no database checker has been called an no vote has been created
    verify(userRepository, times(1)).findById(any(UUID.class));
    verify(votingRepository, times(1)).findById(any(UUID.class));
    verify(voteRepository, times(0)).save(any(Vote.class));

    // and no voting statistics are updated
    verify(votingStatsService, times(0)).updateMovingAverage(any());
  }

  private static Stream<Integer> testCreateVoteFailBecauseInvalidScoreDataProvider() {
    return Stream.of(null, 0, 6);
  }

  @ParameterizedTest(name = "Vote can be created because vote can't be anonymous")
  @MethodSource("testCreateVoteFailBecauseAnonymousSource")
  void testCreateVoteFailBecauseAnonymous(
      boolean inputAnonymous, boolean groupAnonymous, boolean correct) {
    // given: some mocked data
    var votingId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var input =
        CreateVoteInput.newBuilder()
            .withUserId(userId)
            .withScore(2)
            .withVotingId(votingId)
            .withAnonymous(inputAnonymous)
            .build();

    // and: mocked repository calls
    var userRepository = Mockito.mock(UserRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var voteRepository = Mockito.mock(VoteRepository.class);

    var validGroup = Group.builder().with(g -> g.setAnonymousVote(groupAnonymous)).build();
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), validGroup))))
            .build();
    var voting =
        Voting.newBuilder()
            .with(v -> v.setGroup(validGroup))
            .with(v -> v.setCreatedAtDateTime(OffsetDateTime.now().minus(2, ChronoUnit.MINUTES)))
            .with(v -> v.setExpired(false))
            .build();

    // and: there're user and voting available
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(voting));
    when(voteRepository.save(any(Vote.class))).thenReturn(random(Vote.class));

    // when: invoking the vote creation
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, userRepository, null);
    Result<Vote> vote = votingService.createVote(input);

    // then:
    assertEquals(vote.getErrorList().isEmpty(), correct, "Check is expected result");
  }

  private static Stream<Arguments> testCreateVoteFailBecauseAnonymousSource() {
    return Stream.of(
        Arguments.of(true, false, false),
        Arguments.of(true, true, true),
        Arguments.of(false, true, true),
        Arguments.of(false, false, true));
  }

  @Test
  @DisplayName("listVotingsGroup: success")
  void testListVotingsGroupSuccessfully() {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var input =
        ListVotingsGroupInput.newBuilder()
            .withGroupId(groupId)
            .withStartDate(OffsetDateTime.parse("2011-12-03T10:15:30Z"))
            .withEndDate(OffsetDateTime.parse("2011-12-03T10:15:30Z"))
            .build();

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var groupRepository = Mockito.mock(GroupRepository.class);

    when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(new Group()));
    when(votingRepository.findAllByGroupAndCreatedAtDateTimeBetween(any(), any(), any()))
        .thenReturn(Stream.of(random(Voting.class), random(Voting.class), random(Voting.class)));

    // when: invoking the voting listing
    var votingService =
        new DefaultVotingService(
            votingRepository, null, votingStatsService, null, null, groupRepository);
    List<Voting> votings = votingService.listVotingsGroup(input);

    // then: the votings are returned
    assertEquals(votings.size(), 3, "Successfully listed votings");
  }

  @Test
  void testGetVoting() {
    // given: a mocked voting repository
    var votingRepository = mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var userRepository = mock(UserRepository.class);

    // and: mocked calls
    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(random(Voting.class)));
    when(votingRepository.findByIdAndVotingUser(any(UUID.class), any(User.class)))
        .thenReturn(Optional.of(new Voting()));

    // when: getting a voting by id
    var votingService =
        new DefaultVotingService(
            votingRepository, null, votingStatsService, null, userRepository, null);
    var input =
        GetVotingInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withVotingId(UUID.randomUUID())
            .build();
    Result<Voting> result = votingService.getVoting(input);

    // then: we should build it
    assertNotNull(result.getSuccess());
  }

  @Test
  void testGetVotingFailIfVotingDoesntExists() {
    // given: a mocked voting repository
    var votingRepository = mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var userRepository = mock(UserRepository.class);

    when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(new User()));
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

    // when: getting a voting by id
    var votingService =
        new DefaultVotingService(
            votingRepository, null, votingStatsService, null, userRepository, null);
    var input =
        GetVotingInput.newBuilder()
            .withCurrentUserId(UUID.randomUUID())
            .withVotingId(UUID.randomUUID())
            .build();
    Result<Voting> result = votingService.getVoting(input);

    // then: we should build an error
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
    assertEquals(ErrorConstants.NOT_FOUND, result.getErrorList().get(0));
  }

  @Test
  @DisplayName("listVotesVoting: success")
  void testListVotesVotingSuccessfully() {
    // given: some mocked data
    var voteList =
        List.of(Vote.newBuilder().build(), Vote.newBuilder().build(), Vote.newBuilder().build());

    // and: mocked repository calls
    var voteRepository = Mockito.mock(VoteRepository.class);
    when(voteRepository.findByVotingOrderByCreatedAtDateTimeDesc(
            any(Voting.class), any(Pageable.class)))
        .thenReturn(Page.of(voteList, Pageable.from(0), voteList.size()));

    var votingRepository = Mockito.mock(VotingRepository.class);
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(random(Voting.class)));

    var votingStatsService = Mockito.mock(VotingStatsService.class);

    // when: invoking the vote listing
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, null, null);
    var paginatedVotes =
        votingService.listVotesVoting(UUID.randomUUID(), PaginationRequest.from(10, 0));

    // then: the votes are returned
    assertEquals(paginatedVotes.getTotalCount(), 3, "Successfully listed votes");

    // and: only one method has been called
    verify(voteRepository, times(1))
        .findByVotingOrderByCreatedAtDateTimeDesc(any(Voting.class), any(Pageable.class));
  }

  @Test
  @DisplayName("getLastVotingByGroup: success")
  void testGetLastVotingSuccessfully() {
    // given: a user that belong to a group with its last voting
    var group = random(Group.class);
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), group))))
            .build();
    var lastVoting = random(Voting.class);

    // given: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var userRepository = mock(UserRepository.class);
    var groupRepository = mock(GroupRepository.class);

    // and: mocked calls
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));
    when(votingRepository.findByGroupOrderByCreatedAtDateTimeDesc(group))
        .thenReturn(Optional.of(lastVoting));

    // when: getting the last voting from a group
    var votingService =
        new DefaultVotingService(
            votingRepository, null, votingStatsService, null, userRepository, groupRepository);
    var input =
        GetLastVotingInput.newBuilder()
            .with(i -> i.setCurrentUserId(user.getId()))
            .with(i -> i.setGroupId(group.getId()))
            .build();
    Result<Voting> result = votingService.getLastVotingByGroup(input);

    // then: we should build a valid result and return the last voting
    assertEquals(result.getSuccess(), lastVoting, "Successfully returned the last voting");

    // and: both user and group are recover from db to perform some checks
    verify(userRepository, times(1)).findById(user.getId());
    verify(groupRepository, times(1)).findById(group.getId());
  }

  @Test
  void testGetLastVotingFailsIfUserNotInGroup() {
    // given: a user that doesn't belongs to the group
    var group = random(Group.class);
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), random(Group.class)))))
            .build();

    // given: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var userRepository = mock(UserRepository.class);
    var groupRepository = mock(GroupRepository.class);

    // and: some mocked calls
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    // when: getting the last voting from a group
    var votingService =
        new DefaultVotingService(
            votingRepository, null, votingStatsService, null, userRepository, groupRepository);
    var input =
        GetLastVotingInput.newBuilder()
            .with(i -> i.setCurrentUserId(user.getId()))
            .with(i -> i.setGroupId(group.getId()))
            .build();
    Result<Voting> result = votingService.getLastVotingByGroup(input);

    // then: we should build an error
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
    assertEquals(ErrorConstants.USER_NOT_IN_GROUP, result.getErrorList().get(0));
  }

  @Test
  void testGetLastVotingFailsIfGroupDoesNotExist() {
    // and: a user that belong to a group
    var group = random(Group.class);
    var user =
        User.builder()
            .with(u -> u.setName("john"))
            .with(u -> u.setGroups(Set.of(new UserGroup(new User(), group))))
            .build();

    // given: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var userRepository = mock(UserRepository.class);
    var groupRepository = mock(GroupRepository.class);

    // and: mocked calls
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(groupRepository.findById(any())).thenReturn(Optional.empty());
    when(votingRepository.findByGroupOrderByCreatedAtDateTimeDesc(any()))
        .thenReturn(Optional.empty());

    // when: getting the last voting from a group
    var votingService =
        new DefaultVotingService(
            votingRepository, null, votingStatsService, null, userRepository, groupRepository);
    var input = GetLastVotingInput.newBuilder().with(i -> i.setCurrentUserId(user.getId())).build();
    Result<Voting> result = votingService.getLastVotingByGroup(input);

    // then: we should build an error
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
    assertEquals(ErrorConstants.NOT_FOUND, result.getErrorList().get(0));
  }

  @ParameterizedTest
  @MethodSource("getDidUserVotedInVotingData")
  void testDidUserVotedInVoting(Vote vote, Boolean voted) {
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.findById(any(UUID.class)))
        .thenReturn(Optional.ofNullable(Voting.newBuilder().build()));

    var voteRepository = mock(VoteRepository.class);
    when(voteRepository.findByCreatedByAndVoting(any(User.class), any(Voting.class)))
        .thenReturn(Optional.ofNullable(vote));

    var votingStatsService = Mockito.mock(VotingStatsService.class);

    var user = random(User.class);
    var votingId = UUID.randomUUID();
    var votingService =
        new DefaultVotingService(
            votingRepository, voteRepository, votingStatsService, null, null, null);

    Result<Boolean> didUserVote = votingService.didUserVotedInVoting(user, votingId);

    assertEquals(didUserVote.getSuccess(), voted);
  }

  private static Stream<Arguments> getDidUserVotedInVotingData() {
    return Stream.of(
        Arguments.of(Vote.newBuilder().build(), Boolean.TRUE), Arguments.of(null, Boolean.FALSE));
  }

  @Test
  void testGetVotingStats() {
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(random(Voting.class)));
    when(votingRepository.getAvgVoteCountByVoting(any(Voting.class))).thenReturn(Optional.of(4L));

    var voteRepository = mock(VoteRepository.class);
    when(voteRepository.getVoteCountByVoting(any(Voting.class))).thenReturn(8L);
    when(voteRepository.getMaxExpectedVoteCountByVoting(any(Voting.class))).thenReturn(16L);

    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, null, null, null);
    var input =
        VotingStatsInput.builder().with(inner -> inner.setVotingId(UUID.randomUUID())).build();
    var result = votingService.getVotingStats(input);
    var stats = result.getSuccess();

    assertTrue(result.isSuccess());
    assertEquals(stats.get("maxVoteCountExpected"), 16L);
    assertEquals(stats.get("voteCount"), 8L);
    assertEquals(stats.get("voteCountAverage"), 4L);
    assertNotEquals(stats.get("average"), null);
    assertNotEquals(stats.get("movingAverage"), null);
    assertNotEquals(stats.get("standardDeviation"), null);
  }

  @Test
  void testGetVotingStatsVotesByMood() {
    var votingRepository = mock(VotingRepository.class);
    var votesByMood = List.of(new VoteByMoodDTO(1, 2), new VoteByMoodDTO(5, 3));

    when(votingRepository.findById(any(UUID.class))).thenReturn(Optional.of(random(Voting.class)));
    when(votingRepository.findAllVotesByMood(any(Voting.class))).thenReturn(votesByMood);

    var voteRepository = mock(VoteRepository.class);
    when(voteRepository.getVoteCountByVoting(any(Voting.class))).thenReturn(8L);
    when(voteRepository.getMaxExpectedVoteCountByVoting(any(Voting.class))).thenReturn(16L);

    var votingService =
        new DefaultVotingService(votingRepository, voteRepository, null, null, null, null);
    var input =
        VotingStatsInput.builder().with(inner -> inner.setVotingId(UUID.randomUUID())).build();
    var result = votingService.getVotingStats(input);
    var stats = result.getSuccess();

    List<VoteByMoodDTO> resultList = (List<VoteByMoodDTO>) stats.get("votesByMood");

    assertTrue(result.isSuccess());

    // then: despite there were only two records from database there should always be 5
    assertEquals(resultList.size(), 5);

    // and: those weren't coming from database should be set to 0
    assertEquals(0, resultList.get(0).getCount());
    assertEquals(0, resultList.get(1).getCount());
    assertEquals(5, resultList.get(2).getCount());
    assertEquals(1, resultList.get(3).getCount());
    assertEquals(0, resultList.get(4).getCount());
  }

  @Test
  @DisplayName("getNextVoting: success")
  void testGetNextVotingSuccessfully() {
    // given: a pre-existing data
    var votingGroup = random(Group.class);
    var votingDate = OffsetDateTime.now();
    var voting = random(Voting.class);
    voting.setCreatedAtDateTime(votingDate);
    voting.setGroup(votingGroup);

    // and: the next voting to be returned
    var nextVoting = random(Voting.class);

    // given: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.findById(voting.getId())).thenReturn(Optional.of(voting));
    when(votingRepository.getNextVotingByGroupAndDate(
            votingGroup, votingDate.plus(1, ChronoUnit.SECONDS)))
        .thenReturn(Optional.of(nextVoting));

    // when: asking the service to retrieve the data
    var votingService = new DefaultVotingService(votingRepository, null, null, null, null, null);
    Result<Voting> result = votingService.getNextVoting(voting.getId());

    // then: we should build a valid result and return the previous voting
    assertEquals(result.getSuccess(), nextVoting, "Successfully returned the next voting");

    // and: some repositories are called
    verify(votingRepository, times(1)).findById(voting.getId());
    verify(votingRepository, times(1))
        .getNextVotingByGroupAndDate(votingGroup, votingDate.plus(1, ChronoUnit.SECONDS));
  }

  @Test
  @DisplayName("getNextVotingIfLast: error")
  void testGetNextVotingIfLast() {
    // given: a pre-existing data
    var voting = random(Voting.class);

    // given: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.findById(any())).thenReturn(Optional.of(voting));
    when(votingRepository.getNextVotingByGroupAndDate(any(), any())).thenReturn(Optional.empty());

    // when: asking the service to retrieve the data
    var votingService = new DefaultVotingService(votingRepository, null, null, null, null, null);
    Result<Voting> result = votingService.getNextVoting(voting.getId());

    // then: a null value is ok because is expected to have nothing after the last voting
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
  }

  @Test
  @DisplayName("getPreviousVoting: success")
  void testGetPreviousVotingSuccessfully() {
    // given: a pre-existing data
    var votingGroup = random(Group.class);
    var votingDate = OffsetDateTime.now();
    var voting = random(Voting.class);
    voting.setCreatedAtDateTime(votingDate);
    voting.setGroup(votingGroup);

    // and: the previous voting to be returned
    var previousVoting = random(Voting.class);

    // given: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.findById(voting.getId())).thenReturn(Optional.of(voting));
    when(votingRepository.getPreviousVotingByGroupAndDate(votingGroup, votingDate))
        .thenReturn(Optional.of(previousVoting));

    // when: asking the service to retrieve the data
    var votingService = new DefaultVotingService(votingRepository, null, null, null, null, null);
    Result<Voting> result = votingService.getPreviousVoting(voting.getId());

    // then: we should build a valid result and return the previous voting
    assertEquals(result.getSuccess(), previousVoting, "Successfully returned the previous voting");

    // and: some repositories are called
    verify(votingRepository, times(1)).findById(voting.getId());
    verify(votingRepository, times(1)).getPreviousVotingByGroupAndDate(votingGroup, votingDate);
  }

  @Test
  @DisplayName("getPreviousVotingIfFirst: error")
  void testGetPreviousVotingIfFirst() {
    // given: a pre-existing data
    var voting = random(Voting.class);

    // given: some mocked repositories
    var votingRepository = mock(VotingRepository.class);
    when(votingRepository.findById(any())).thenReturn(Optional.of(voting));
    when(votingRepository.getPreviousVotingByGroupAndDate(any(), any()))
        .thenReturn(Optional.empty());

    // when: asking the service to retrieve the data
    var votingService = new DefaultVotingService(votingRepository, null, null, null, null, null);
    Result<Voting> result = votingService.getPreviousVoting(voting.getId());

    // then: a null value is ok because we expect to have nothing before the first voting
    assertNotNull(result.getErrorList());
    assertNull(result.getSuccess());
  }
}
