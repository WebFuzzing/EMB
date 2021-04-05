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
package patio.user.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyListOf;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.user.services.internal.DefaultUserService;

/**
 * Tests {@link DefaultUserService}
 *
 * @since 0.1.0
 */
public class UserServiceTests {

  @Test
  void testListUsers() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findAll()).thenReturn(randomListOf(4, User.class));

    // when: invoking service listUsers()
    var userService = new DefaultUserService(userRepository);
    var userList = userService.listUsers();

    // then: we should build the expected number of users
    assertThat(userList, iterableWithSize(4));
  }

  @Test
  void testListUsersByIds() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findAllByIdInList(anyListOf(UUID.class)))
        .thenReturn(randomListOf(1, User.class));

    // when: invoking service listUsersByIds with some ids()
    var userService = new DefaultUserService(userRepository);
    var userList = userService.listUsersByIds(List.of(UUID.randomUUID()));

    // then: we should build the expected number of users
    assertThat(userList, iterableWithSize(1));
  }

  @Test
  void testGetUser() {
    // given: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(random(User.class)));

    // when: getting a user by id
    var userService = new DefaultUserService(userRepository);
    var user = userService.getUser(UUID.randomUUID());

    // then: we should build it
    assertTrue(user.isPresent());
  }
}
