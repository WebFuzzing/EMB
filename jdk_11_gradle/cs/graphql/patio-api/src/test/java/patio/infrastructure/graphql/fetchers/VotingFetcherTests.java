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
package patio.infrastructure.graphql.fetchers;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import io.micronaut.core.async.publisher.Publishers;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.dataloader.DataLoader;
import org.dataloader.DataLoaderOptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.common.domain.utils.PaginationRequest;
import patio.common.domain.utils.PaginationResult;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.infrastructure.graphql.dataloader.DataLoaderRegistryFactory;
import patio.infrastructure.graphql.fetchers.utils.FetcherTestUtils;
import patio.user.domain.User;
import patio.user.graphql.UserBatchLoader;
import patio.user.services.UserService;
import patio.user.services.internal.DefaultUserService;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;
import patio.voting.graphql.CreateVoteInput;
import patio.voting.graphql.CreateVotingInput;
import patio.voting.graphql.ListVotingsGroupInput;
import patio.voting.graphql.VotingFetcher;
import patio.voting.services.internal.DefaultVotingService;
import reactor.test.StepVerifier;

/**
 * Tests {@link VotingFetcher} class
 *
 * @since 0.1.0
 */
class VotingFetcherTests {

  @Test
  void testCreateVoting() {
    // given: some random data
    var authenticatedUser = random(User.class);
    var votingId = UUID.randomUUID();

    // and: mocked services
    var mockedService = Mockito.mock(DefaultVotingService.class);
    Mockito.when(mockedService.createVoting(any(CreateVotingInput.class)))
        .thenReturn(Result.result(random(Voting.class)));

    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(authenticatedUser, Map.of("votingId", votingId));

    // when: invoking the fetcher with correct data
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    DataFetcherResult<Voting> result = fetcher.createVoting(mockedEnvironment);

    // then: we should build no errors
    assertThat("There is no errors", result.getErrors().size(), is(0));

    // and: we should build the successful result
    assertNotNull("There is new voting", result.getData().getId());
  }

  @Test
  @DisplayName("createVote: create vote successfully")
  void testCreateVote() {
    // given: an authenticated user and a valid voting id
    var authenticatedUser = random(User.class);
    var votingId = UUID.randomUUID();

    // and: some mocked services
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            authenticatedUser, Map.of("votingId", votingId, "anonymous", false));

    var mockedService = Mockito.mock(DefaultVotingService.class);
    Mockito.when(mockedService.createVote(any(CreateVoteInput.class)))
        .thenReturn(Result.result(Vote.newBuilder().build()));

    // when: invoking vote creation
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    DataFetcherResult<Vote> vote = fetcher.createVote(mockedEnvironment);

    // then: the vote should have been created successfully
    assertNotNull("The vote should have been created successfully", vote.getData());
  }

  @Test
  void testListVotingsGroup() {
    // given: some random data
    var authenticatedUser = random(User.class);
    var group = random(Group.class);
    var startDate = OffsetDateTime.parse("2019-01-24T00:00:00Z");
    var endDate = OffsetDateTime.parse("2019-01-25T00:00:00Z");

    // and: mocked service
    var mockedService = Mockito.mock(DefaultVotingService.class);
    Mockito.when(mockedService.listVotingsGroup(any(ListVotingsGroupInput.class)))
        .thenReturn(List.of(random(Voting.class)));

    // and: mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            authenticatedUser, Map.of("startDateTime", startDate, "endDateTime", endDate));
    Mockito.when(mockedEnvironment.getSource()).thenReturn(group);

    // when: invoking the fetcher with correct data
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    List<Voting> result = fetcher.listVotingsGroup(mockedEnvironment);

    // then: we should build the successful result
    assertEquals("There are votings", result.size(), 1);
  }

  @Test
  void testGetVoting() {
    // given: an voting
    Voting voting = random(Voting.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(DefaultVotingService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.getVoting(any())).thenReturn(Result.result(voting));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(user, Map.of("id", voting.getId()));

    // when: fetching build voting invoking the service
    VotingFetcher fetchers = new VotingFetcher(mockedService);
    DataFetcherResult<Voting> result = fetchers.getVoting(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the voting is found", result.getData(), is(voting));
  }

  @Test
  void testListVotesVoting() {
    // given: some random data
    var authenticatedUser = random(User.class);
    var voting = random(Voting.class);

    // and: mocked service
    var mockedService = Mockito.mock(DefaultVotingService.class);
    var partialResult = List.of(random(Vote.class));
    var paginationResult =
        PaginationResult.newBuilder()
            .with(pr -> pr.setData(partialResult))
            .with(pr -> pr.setTotalCount(partialResult.size()))
            .build();
    Mockito.when(mockedService.listVotesVoting(any(), any(PaginationRequest.class)))
        .thenReturn(paginationResult);

    // and: mocked environment
    var mockedEnvironment = FetcherTestUtils.generateMockedEnvironment(authenticatedUser, Map.of());
    Mockito.when(mockedEnvironment.getSource()).thenReturn(voting);

    // when: invoking the fetcher with correct data
    VotingFetcher fetcher = new VotingFetcher(mockedService);
    PaginationResult<Vote> result = fetcher.listVotesVoting(mockedEnvironment);

    // then: we should build the successful result
    assertEquals("There are votes", result.getTotalCount(), 1);
  }

  @Test
  void testLListUserVotesInGroup() {
    // given: some mocked data
    var groupId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var startDate = OffsetDateTime.parse("2019-01-24T00:00:00Z");
    var endDate = OffsetDateTime.parse("2019-01-25T00:00:00Z");

    // and: some votes
    List<Vote> votes = List.of(random(Vote.class), random(Vote.class), random(Vote.class));

    // and: an user
    User currentUser = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(DefaultVotingService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.listUserVotesInGroup(any())).thenReturn(Result.result(votes));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            currentUser,
            Map.of(
                "userId",
                userId,
                "groupId",
                groupId,
                "startDateTime",
                startDate,
                "endDateTime",
                endDate));

    // when: fetching get voting invoking the service
    VotingFetcher fetchers = new VotingFetcher(mockedService);
    DataFetcherResult<List<Vote>> result = fetchers.listUserVotesInGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the votes are found", result.getData(), is(votes));
  }

  @Test
  void testGetVoteCreatedBy() {
    // given: a mocked user service
    User randomUser = random(User.class);
    UserService mockedService = Mockito.mock(DefaultUserService.class);
    Mockito.when(mockedService.listUsersByIds(anyListOf(UUID.class)))
        .thenReturn(List.of(randomUser));

    // and: a mocked environment
    Vote vote = Vote.newBuilder().with(v -> v.setCreatedBy(randomUser)).build();
    DataLoader<UUID, User> dataLoader =
        DataLoader.newDataLoader(
            new UserBatchLoader(mockedService),
            // IMPORTANT! for testing setBatchingEnabled(false) otherwise
            // data loader execution won't complete
            DataLoaderOptions.newOptions().setBatchingEnabled(false));

    DataFetchingEnvironment mockedEnvironment =
        FetcherTestUtils.create()
            .dataLoader(DataLoaderRegistryFactory.DL_USERS_BY_IDS, dataLoader)
            .source(vote)
            .build();

    // when: retrieving a user by using the data loader
    VotingFetcher fetcher = new VotingFetcher(null);
    CompletableFuture<User> completableFuture = fetcher.getVoteCreatedBy(mockedEnvironment);

    // then: we should get the expected user
    StepVerifier.create(Publishers.fromCompletableFuture(completableFuture))
        .expectNextMatches(user -> user.getId().equals(randomUser.getId()))
        .expectComplete()
        .verify();
  }
}
