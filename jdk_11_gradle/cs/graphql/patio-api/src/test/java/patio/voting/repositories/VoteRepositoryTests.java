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

import static org.junit.Assert.assertEquals;

import io.micronaut.test.annotation.MicronautTest;
import java.util.UUID;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.infrastructure.tests.Fixtures;

/**
 * Tests DATABASE integration regarding {@link patio.voting.domain.Vote} persistence.
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
public class VoteRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient VoteRepository voteRepository;
  @Inject transient VotingRepository votingRepository;

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
  void testGetMaxExpectedVoteCountByVoting() {
    // given: pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testGetMaxExpectedVoteCountByVoting.sql");

    // when: asking for how many people is expected to vote in a group by passing a specific voting
    var maxExpected =
        votingRepository
            .findById(UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd"))
            .map(voteRepository::getMaxExpectedVoteCountByVoting)
            .orElse(0L);

    // then: we should get the expected number
    assertEquals(maxExpected.longValue(), 8L);
  }

  @Test
  void testGetVoteCountByVoting() {
    // given: pre-existent data
    fixtures.load(VotingRepositoryTests.class, "testGetVoteCountByVoting.sql");

    // when: asking for how many people voted in a given voting
    var voteCount =
        votingRepository
            .findById(UUID.fromString("7772e35c-5a87-4ba3-ab93-da8a957037fd"))
            .map(voteRepository::getVoteCountByVoting)
            .orElse(0L);

    // then: we should get the expected number of votes
    assertEquals(voteCount.longValue(), 5L);
  }
}
