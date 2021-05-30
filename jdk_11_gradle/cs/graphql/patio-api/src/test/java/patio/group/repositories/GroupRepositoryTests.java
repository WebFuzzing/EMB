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
package patio.group.repositories;

import static org.junit.jupiter.api.Assertions.*;

import io.micronaut.test.annotation.MicronautTest;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.group.domain.Group;
import patio.infrastructure.tests.Fixtures;
import patio.user.domain.User;
import patio.voting.domain.Voting;

/**
 * Tests DATABASE integration regarding {@link User} persistence
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
public class GroupRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient GroupRepository repository;

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
  void testFindAllGroupsByVotingDayAndVotingTime() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindAllByDayOfWeekAndVotingTimeLessEq.sql");

    // and: some expected ids
    UUID votingToday1 = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");

    OffsetDateTime dateTime = OffsetDateTime.now();
    String dayOfWeek = dateTime.getDayOfWeek().toString();

    var groupStream = repository.findAllGroupsInVotingDayAndInVotingPeriod(dayOfWeek, dateTime);
    var idStream = groupStream.map(Group::getId);

    // then: we should get groups voting the expected date before or at the given time
    assertTrue(idStream.anyMatch(thisUUID(votingToday1)));
  }

  @Test
  void testFindAllGroupsByVotingDayAndNotOpenVotingTime() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindAllByDayOfWeekAndVotingTimeLessEq.sql");

    // and: some expected ids
    UUID votingToday1 = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");

    OffsetDateTime dateTime = OffsetDateTime.now().minusHours(24);
    String dayOfWeek = dateTime.getDayOfWeek().toString();

    var groupStream = repository.findAllGroupsInVotingDayAndInVotingPeriod(dayOfWeek, dateTime);
    var idStream = groupStream.map(Group::getId);

    // then: we should get groups voting the expected date before or at the given time
    assertFalse(idStream.anyMatch(thisUUID(votingToday1)));
  }

  @Test
  void testFindAllGroupsByVotingDayAndClosedVotingTime() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindAllByDayOfWeekAndVotingTimeLessEq.sql");

    // and: some expected ids
    UUID votingToday1 = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");

    OffsetDateTime dateTime = OffsetDateTime.now().plusHours(24);
    String dayOfWeek = dateTime.getDayOfWeek().toString();

    var groupStream = repository.findAllGroupsInVotingDayAndInVotingPeriod(dayOfWeek, dateTime);
    var idStream = groupStream.map(Group::getId);

    // then: we should get groups voting the expected date before or at the given time
    assertFalse(idStream.anyMatch(thisUUID(votingToday1)));
  }

  @Test
  void testFindAGroupWithVotingForCurrentVotingPeriod() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindAllByVotingCreatedAtBetween.sql");

    UUID expectedGroupId = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");

    var votedAlreadyStream = repository.findAllGroupsWithVotingInCurrentVotingPeriod();
    var idStream = votedAlreadyStream.map(Group::getId);

    assertTrue(idStream.anyMatch(thisUUID(expectedGroupId)));
  }

  @Test
  void testFindNoneWithVotingForCurrentVotingPeriod() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindAllByDayOfWeekAndVotingTimeLessEq.sql");

    var votedAlreadyStream = repository.findAllGroupsWithVotingInCurrentVotingPeriod();
    var idStream = votedAlreadyStream.map(Group::getId);

    assertFalse(idStream.findAny().isPresent());
  }

  @Test
  void testFindGroupsWithVotingsToExpire() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindExpiredVotingsGroup.sql");

    // and: a time with a group which its voting is out of its voting period
    OffsetDateTime dateTime = OffsetDateTime.now().plusHours(2);
    UUID expectedVotingId = UUID.fromString("953951f9-3f6f-421e-a12c-270cfcabb2d0");

    // when: asking to retrieve groups with votings to expire
    var votingToExpireStream = repository.findAllExpiredVotingsByTime(dateTime);
    var idStream = votingToExpireStream.map(Voting::getId);

    // the expected group is returned
    assertTrue(idStream.anyMatch(thisUUID(expectedVotingId)));
  }

  @Test
  void testFindGroupsWithNoVotingsToExpire() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindExpiredVotingsGroup.sql");

    // and: a time in both groups in valid voting periods
    OffsetDateTime dateTime = OffsetDateTime.now().plusMinutes(2);

    // when: asking to retrieve groups with votings to expire
    var votingToExpireStream = repository.findAllExpiredVotingsByTime(dateTime);
    var idStream = votingToExpireStream.map(Voting::getId);

    // then : no group is returned
    assertFalse(idStream.findAny().isPresent());
  }

  private Predicate<UUID> thisUUID(UUID uuid) {
    return uuid::equals;
  }

  @Test
  void testFindFavouriteGroup() {
    // given: a set of fixtures
    fixtures.load(GroupRepositoryTests.class, "testFindFavouriteGroup.sql");

    UUID userId = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    UUID expectedGroupId = UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93");

    // when: asking for the user's favourite group
    Optional<Group> favouriteGroup = repository.findMyFavouriteGroupByUserId(userId);

    // then: we should get the expected group
    assertTrue(favouriteGroup.isPresent());
    assertEquals(favouriteGroup.get().getId(), expectedGroupId);
  }
}
