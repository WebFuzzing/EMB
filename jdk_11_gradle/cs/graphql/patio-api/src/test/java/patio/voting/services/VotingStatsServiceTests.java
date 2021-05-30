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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.common.domain.utils.OffsetPaginationRequest;
import patio.common.domain.utils.OffsetPaginationResult;
import patio.group.domain.Group;
import patio.group.repositories.GroupRepository;
import patio.voting.domain.Voting;
import patio.voting.domain.VotingStats;
import patio.voting.graphql.GetStatsByGroupInput;
import patio.voting.repositories.VoteRepository;
import patio.voting.repositories.VotingRepository;
import patio.voting.repositories.VotingStatsRepository;
import patio.voting.services.internal.DefaultVotingService;
import patio.voting.services.internal.DefaultVotingStatsService;

/**
 * Tests {@link DefaultVotingService}
 *
 * @since 0.1.0
 */
public class VotingStatsServiceTests {

  @Test
  @DisplayName("Creating voting stats successfully")
  void testCreateVotingStatSuccess() {
    // given: a voting
    var voting = random(Voting.class);

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);

    // when: the service method is executed
    var votingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    votingStatsService.createVotingStat(voting);

    // then: the changes are persisted
    verify(votingStatRepository, times(1)).save(any());
    verify(votingRepository, times(1)).save(any());
  }

  @Test
  @DisplayName("Updating voting stats successfully")
  void testUpdateVotingStatSuccess() {
    // given: a voting
    var voting = random(Voting.class);

    // and: mocked repository calls
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);

    // when: the service method is executed
    var votingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    votingStatsService.updateMovingAverage(voting);

    // then: the changes are persisted
    verify(votingStatRepository, times(1)).save(any());
    verify(votingRepository, times(0)).save(any());
  }

  @Test
  @DisplayName("Get all stats for a group successfully")
  void testGetVotingStatsByGroupSuccess() {
    // given: the mocked data input
    var group = Group.builder().with(g -> g.setId(UUID.randomUUID())).build();
    var offsetPaginationRequest = new OffsetPaginationRequest(1, 5);
    var input = GetStatsByGroupInput.newBuilder().with(i -> i.setGroupId(group.getId())).build();

    // and: some mocked repositories
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    // and: the expected result
    var expectedResults = new ArrayList<VotingStats>();
    expectedResults.add(random(VotingStats.class));
    expectedResults.add(random(VotingStats.class));
    expectedResults.add(random(VotingStats.class));
    var paginationResult =
        new OffsetPaginationResult<VotingStats>(expectedResults.size(), 1, expectedResults);

    // and: the main repository returning the expected when called with the right parameters
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);

    when(votingStatRepository.findStatsByGroup(group, offsetPaginationRequest))
        .thenReturn(paginationResult);

    // when: the service method is executed
    var defaultVotingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    var paginatedVotingStats =
        defaultVotingStatsService.getVotingStatsByGroup(input, offsetPaginationRequest);

    // then: we get the expected results
    assertEquals(paginatedVotingStats.getData(), expectedResults);
    assertEquals(paginatedVotingStats.getTotalCount(), expectedResults.size());
    verify(votingStatRepository, times(1)).findStatsByGroup(group, offsetPaginationRequest);
  }

  @Test
  @DisplayName("Get all stats for a non existing group")
  void testGetVotingStatsByGroupWhenBadGroupId() {
    // given: a wrong data input
    var group = Group.builder().with(g -> g.setId(UUID.randomUUID())).build();

    var wrongGroupId = random(UUID.class);
    var input = GetStatsByGroupInput.newBuilder().with(i -> i.setGroupId(wrongGroupId)).build();

    // and: some mocked repositories
    var votingRepository = Mockito.mock(VotingRepository.class);
    var voteRepository = Mockito.mock(VoteRepository.class);
    var groupRepository = Mockito.mock(GroupRepository.class);
    when(groupRepository.findById(group.getId())).thenReturn(Optional.of(group));

    // and: not-expected results to be returned
    var offsetPaginationRequest = new OffsetPaginationRequest(1, 5);
    var votingStatsResult = new ArrayList<VotingStats>();
    votingStatsResult.add(random(VotingStats.class));
    votingStatsResult.add(random(VotingStats.class));
    votingStatsResult.add(random(VotingStats.class));
    var offsetPaginationResult =
        new OffsetPaginationResult<VotingStats>(votingStatsResult.size(), 1, votingStatsResult);

    // and: the main repository returning what's expected when called with the right parameters
    var votingStatRepository = Mockito.mock(VotingStatsRepository.class);
    when(votingStatRepository.findStatsByGroup(group, offsetPaginationRequest))
        .thenReturn(offsetPaginationResult);

    // when: the service method is executed with the wrong parameters
    var defaultVotingStatsService =
        new DefaultVotingStatsService(
            votingStatRepository, votingRepository, voteRepository, groupRepository);
    var paginatedVotingStats =
        defaultVotingStatsService.getVotingStatsByGroup(input, offsetPaginationRequest);

    // then: we get the expected results
    var noResults = new ArrayList<>();
    assertEquals(paginatedVotingStats.getData(), noResults);
    assertEquals(paginatedVotingStats.getTotalCount(), noResults.size());
    verify(votingStatRepository, times(0)).findStatsByGroup(group, offsetPaginationRequest);
  }
}
