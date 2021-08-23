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
package patio.user.repositories;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static patio.infrastructure.utils.IterableUtils.iterableToStream;

import io.micronaut.test.annotation.MicronautTest;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.infrastructure.tests.Fixtures;
import patio.user.domain.User;

/**
 * Tests DATABASE integration regarding {@link User} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
class UserRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient UserRepository repository;

  @Inject transient Fixtures fixtures;

  @BeforeEach
  void loadFixtures() {
    flyway.migrate();
  }

  @AfterEach
  void cleanFixtures() {
    flyway.clean();
  }

  @Test
  void testListUsersByIds() {
    // given: a pre-loaded fixtures
    fixtures.load(UserRepositoryTests.class, "testListUsersByIds.sql");

    // and: two selected user ids
    UUID sue = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID tony = UUID.fromString("3465094c-5545-4007-a7bc-da2b1a88d9dc");

    // when: asking for the list of specific users
    Iterable<User> userList = repository.findAllByIdInList(List.of(sue, tony));
    List<UUID> ids = iterableToStream(userList).map(User::getId).collect(Collectors.toList());

    // then: check there're the expected number of users
    assertThat(userList, iterableWithSize((2)));

    // and: list of users contains both selected user ids
    assertThat(ids, hasItems(sue, tony));
  }

  @Test
  void testFindOrCreateUser() {
    // given: a random user instance
    var user = random(User.class, "id");

    // when: trying to find it when it is not in db
    var created = repository.findByEmailOrCreate(user);

    // then: we create the entry
    assertEquals(created.get().getEmail(), user.getEmail());

    // when: we look for it again
    var found = repository.findByEmailOrCreate(user);

    // then: we get it from db
    assertEquals(found.get().getId(), created.get().getId());
  }
}
