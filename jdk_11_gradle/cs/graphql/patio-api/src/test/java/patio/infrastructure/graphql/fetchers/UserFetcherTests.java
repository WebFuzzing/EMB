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

import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.infrastructure.graphql.fetchers.utils.FetcherTestUtils;
import patio.user.domain.User;
import patio.user.graphql.UserFetcher;
import patio.user.services.internal.DefaultUserService;

/**
 * Tests {@link UserFetcher} class
 *
 * @since 0.1.0
 */
public class UserFetcherTests {

  @Test
  void testListUsers() {
    // given: a mocking service
    var mockedService = Mockito.mock(DefaultUserService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.listUsers()).thenReturn(randomListOf(2, User.class));

    // when: fetching user list invoking the service
    UserFetcher fetchers = new UserFetcher(mockedService);
    Iterable<User> userList = fetchers.listUsers(null);

    // then: check certain assertions should be met
    assertThat("there're only a certain number of users", userList, iterableWithSize(2));
  }

  @Test
  void testGetUser() {
    // given: an user
    User user = random(User.class);
    // and: a mocking service
    var mockedService = Mockito.mock(DefaultUserService.class);

    // and: mocking service's behavior
    Mockito.when(mockedService.getUser(user.getId())).thenReturn(Optional.of(user));

    // and: a mocked environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(null, Map.of("id", user.getId()));

    // when: fetching build user invoking the service
    UserFetcher fetchers = new UserFetcher(mockedService);
    Optional<User> result = fetchers.getUser(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the user is found", result.get(), is(user));
  }

  @Test
  void testGetCurrentUser() {
    // given: an user
    User user = random(User.class);

    // and: a mocked environment
    var mockedEnvironment = FetcherTestUtils.generateMockedEnvironment(user, Map.of());

    // when: fetching build user invoking the service
    UserFetcher fetchers = new UserFetcher(null);
    User result = fetchers.getCurrentUser(mockedEnvironment);

    // then: check certain assertions should be met
    assertThat("the user is found", result, is(user));
  }
}
