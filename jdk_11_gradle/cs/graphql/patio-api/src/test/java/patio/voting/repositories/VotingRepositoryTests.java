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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static patio.infrastructure.utils.IterableUtils.iterableToStream;

import io.micronaut.data.model.Pageable;
import io.micronaut.test.annotation.MicronautTest;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.group.repositories.GroupRepository;
import patio.infrastructure.tests.Fixtures;
import patio.infrastructure.utils.OptionalUtils;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.voting.domain.Vote;
import patio.voting.domain.Voting;

/**
 * Tests DATABASE integration regarding {@link Voting} persistence.
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
public class VotingRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient UserRepository userRepository;
  @Inject transient VoteRepository voteRepository;
  @Inject transient VotingRepository votingRepository;
  @Inject transient GroupRepository groupRepository;

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
  void testListUsersByIdsSameOrderAsListUsers() {
    // given: a pre-loaded fixtures
    fixtures.load(VotingRepositoryTests.class, "testListUsersByIdsSameOrderAsListUsers.sql");

    // and: two selected user ids
    List<UUID> ids =
        List.of(
                "3465094c-5545-4007-a7bc-da2b1a88d9dc",
                "1998c588-d93b-4db6-92e2-a9dbb4cf03b5",
                "84d48a35-7659-4710-ad13-4c47785a0e9d",
                "c2a771bc-f8c5-4112-a440-c80fa4c8e382",
                "486590a3-fcc1-4657-a9ed-5f0f95dadea6")
            .stream()
            .map(UUID::fromString)
            .collect(Collectors.toList());

    // when: asking for the list of users
    Optional<Voting> voting =
        votingRepository.findById(UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd"));

    Comparator<User> comparator = Comparator.comparing((User user) -> ids.indexOf(user.getId()));
    List<User> userList1 =
        userRepository.findAllByIdInList(ids).stream()
            .sorted(comparator)
            .collect(Collectors.toList());
    List<User> userList2 =
        voting.stream()
            .flatMap(
                v ->
                    voteRepository.findByVotingOrderByCreatedAtDateTimeDesc(v, Pageable.from(0))
                        .getContent().stream())
            .map(Vote::getCreatedBy)
            .collect(Collectors.toList());

    // then: check there're the expected number of users
    assertThat(userList1, iterableWithSize(5));
    assertThat(userList2, iterableWithSize(5));

    List<UUID> result1 = iterableToStream(userList1).map(User::getId).collect(Collectors.toList());

    // and: both list serve users in the same order
    assertThat(
        result1,
        IsIterableContainingInOrder.contains(
            iterableToStream(userList2).map(User::getId).toArray()));
  }

  @Test
  void testFindByIdAndVotingUser() {
    // given: a pre-loaded fixtures
    fixtures.load(VotingRepositoryTests.class, "testFindByIdAndVotingUser.sql");

    // and:
    UUID votingID = UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd");
    Optional<Voting> voting = votingRepository.findById(votingID);

    UUID userID = UUID.fromString("486590a3-fcc1-4657-a9ed-5f0f95dadea6");
    Optional<User> user = userRepository.findById(userID);

    // when:
    Optional<Voting> result =
        OptionalUtils.combine(voting, user)
            .flatmapInto((v, u) -> votingRepository.findByIdAndVotingUser(v.getId(), u));

    assertTrue(result.isPresent());
  }

  @Test
  void testFindAllVotesByMood() {
    // given: some pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testFindAllVotesByMood.sql");

    // when: asking for a given votes aggregated by mood
    var optionalVoting =
        votingRepository.findById(UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd"));
    var votesByMood = optionalVoting.map(votingRepository::findAllVotesByMood).orElse(List.of());

    // then: we should get the expected groups
    assertEquals(5, votesByMood.size());

    // and: every group should have the expected vote count
    // and: records are order by score desc
    assertEquals(votesByMood.get(0).getCount(), 2);
    assertEquals(votesByMood.get(1).getCount(), 1);
    assertEquals(votesByMood.get(2).getCount(), 1);
    assertEquals(votesByMood.get(3).getCount(), 2);
    assertEquals(votesByMood.get(4).getCount(), 2);
  }

  @Test
  void testGetAvgVoteCountByVoting() {
    // given: pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testGetAvgVoteCountByVoting.sql");

    // when: asking for vote count average of a group passing any group voting's id
    var optionalVoting =
        votingRepository.findById(UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd"));
    var avgVoteCount = optionalVoting.flatMap(votingRepository::getAvgVoteCountByVoting).orElse(0L);

    // then: we should get the expected average
    assertEquals(6L, avgVoteCount.longValue());
  }

  @Test
  void testGetNextVoting() {
    // given: pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testFindMovingAverageByGroup.sql");
    var votingDate = OffsetDateTime.parse("2020-06-21T00:00:01+01:00");

    // when: asking to get the next voting to a date and group
    var nextVoting =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map(
                (votingGroup) ->
                    votingRepository.getNextVotingByGroupAndDate(votingGroup, votingDate))
            .orElse(null);

    // then: we should get the next voting in time
    Assert.assertEquals(
        nextVoting.get().getId(), UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957038fd"));
  }

  @Test
  void testGetNextVotingWhenLastOne() {
    // given: pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testFindMovingAverageByGroup.sql");
    var votingDate = OffsetDateTime.parse("2020-07-01T00:00:01+01:00");

    // when: asking to get the next voting when there's no more votings
    var nextVoting =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map(
                (votingGroup) ->
                    votingRepository.getNextVotingByGroupAndDate(votingGroup, votingDate))
            .orElse(null);

    // then: we should get no voting
    Assert.assertEquals(nextVoting, Optional.empty());
  }

  @Test
  void testGetPreviousVoting() {
    // given: pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testFindMovingAverageByGroup.sql");
    var votingDate = OffsetDateTime.parse("2020-06-21T00:00:01+01:00");

    // when: asking to get the previous voting to a date and group
    var nextVoting =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map(
                (votingGroup) ->
                    votingRepository.getPreviousVotingByGroupAndDate(votingGroup, votingDate))
            .orElse(null);

    // then: we should get the previous voting in time
    Assert.assertEquals(
        nextVoting.get().getId(), UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd"));
  }

  @Test
  void testGetPreviousVotingWhenFirstOne() {
    // given: pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testFindMovingAverageByGroup.sql");
    var votingDate = OffsetDateTime.parse("2020-06-01T00:00:01+01:00");

    // when: asking to get the previous voting when there's no previous votings
    var nextVoting =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map(
                (votingGroup) ->
                    votingRepository.getPreviousVotingByGroupAndDate(votingGroup, votingDate))
            .orElse(null);

    // then: we should get no voting
    Assert.assertEquals(nextVoting, Optional.empty());
  }
}
