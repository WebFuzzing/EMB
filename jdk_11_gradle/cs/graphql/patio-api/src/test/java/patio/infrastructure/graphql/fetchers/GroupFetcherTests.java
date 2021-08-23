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
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import graphql.execution.DataFetcherResult;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.graphql.GroupFetcher;
import patio.group.services.internal.DefaultGroupService;
import patio.infrastructure.graphql.fetchers.utils.FetcherTestUtils;
import patio.user.domain.User;

/**
 * Tests {@link GroupFetcher} class
 *
 * @since 0.1.0
 */
class GroupFetcherTests {

  @Test
  void testListGroups() {
    // given: a mocking service
    var mockedService = Mockito.mock(DefaultGroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.listGroups()).thenReturn(randomListOf(2, Group.class));

    // when: fetching group list invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    Iterable<Group> groupList = fetchers.listGroups(null);

    // then: check certain assertions should be met
    assertThat("there're only a certain values of groups", groupList, iterableWithSize(2));
  }

  @Test
  void testListMyGroups() {
    // given: an user
    User user = random(User.class);

    // and: a mocked environment
    var mockedEnvironment = FetcherTestUtils.generateMockedEnvironment(user, Map.of());

    // and: a mockedservice
    var mockedService = Mockito.mock(DefaultGroupService.class);

    // and: mocked service's behavior
    Mockito.when(mockedService.listGroupsUser(any())).thenReturn(randomListOf(2, Group.class));

    // when: fetching group list invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    List<Group> groupList = fetchers.listMyGroups(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("there're only a certain values of groups", groupList.size(), is(2));
  }

  @Test
  void testCreateGroup() {
    // given: a group
    Group group = random(Group.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(DefaultGroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.createGroup(any())).thenReturn(group);

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            user,
            Map.of(
                "name",
                group.getName(),
                "anonymousVote",
                group.isAnonymousVote(),
                "votingDays",
                group.getVotingDays(),
                "votingTime",
                group.getVotingTime(),
                "votingDuration",
                group.getVotingDuration()));

    // when: creating a group invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    Group result = fetchers.createGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the group is created", result, is(group));
    assertThat(
        "days of week are the expected",
        result.getVotingDays().size(),
        is(group.getVotingDays().size()));
    assertNotNull("time is present", result.getVotingTime());
    assertNotNull("duration time is present", result.getVotingDuration());
  }

  @Test
  void testGetGroup() {
    // given: an group
    Group group = random(Group.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(DefaultGroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.getGroup(any())).thenReturn(Result.result(group));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(user, Map.of("id", group.getId()));

    // when: fetching build group invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    DataFetcherResult<Group> result = fetchers.getGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the group is found", result.getData(), is(group));
  }

  @Test
  void testUpdateGroup() {
    // given: a group
    Group group = random(Group.class);

    // and: an user
    User user = random(User.class);

    // and: a mocking service
    var mockedService = Mockito.mock(DefaultGroupService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.updateGroup(any())).thenReturn(Result.result(group));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(
            user,
            Map.of(
                "name",
                group.getName(),
                "anonymousVote",
                group.isAnonymousVote(),
                "votingDays",
                group.getVotingDays(),
                "votingTime",
                group.getVotingTime(),
                "votingDuration",
                group.getVotingDuration()));

    // when: creating a group invoking the service
    GroupFetcher fetchers = new GroupFetcher(mockedService);
    DataFetcherResult<Group> result = fetchers.updateGroup(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the group is returned", result.getData(), is(group));
  }
}
