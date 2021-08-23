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
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Inject;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import patio.common.domain.utils.OffsetPaginationRequest;
import patio.common.domain.utils.OffsetPaginationResult;
import patio.group.repositories.GroupRepository;
import patio.infrastructure.tests.Fixtures;

/**
 * Tests DATABASE integration regarding {@link patio.voting.domain.Vote} persistence.
 *
 * @since 0.1.0
 */
@MicronautTest
@Testcontainers
public class VotingStatsRepositoryTests {

  @Container
  @SuppressWarnings("unused")
  private static PostgreSQLContainer DATABASE = new PostgreSQLContainer();

  @Inject transient Flyway flyway;

  @Inject transient VotingStatsRepository votingStatsRepository;
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
  void testGetMovingAverageByGroupTwoDaysPeriod() {
    // given: pre-existent data
    fixtures.load(VotingStatsRepositoryTests.class, "testFindMovingAverageByGroup.sql");

    // when: asking to calculate the moving average from a period posterior to both voting 1 and 2
    OffsetDateTime dateTime = OffsetDateTime.parse("2020-06-01T12:00:01+01:00");

    var movingAverage =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map((group) -> votingStatsRepository.findMovingAverageByGroup(group, dateTime))
            .orElse(Optional.empty());

    // then: we should get the expected number, average of both averages
    assertEquals(movingAverage.get(), 2.19, 0.0);
  }

  @Test
  void testGetMovingAverageByGroupOneDayPeriod() {
    // given: pre-existent data
    fixtures.load(VotingStatsRepositoryTests.class, "testFindMovingAverageByGroup.sql");

    // when: asking to calculate the moving average from a period posterior to voting 1
    OffsetDateTime dateTime = OffsetDateTime.parse("2020-06-21T12:00:01+01:00");

    var movingAverage =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map((group) -> votingStatsRepository.findMovingAverageByGroup(group, dateTime))
            .orElse(Optional.empty());

    // then: we should get just the voting 2 average
    assertEquals(movingAverage.get(), 1.5, 0.0);
  }

  @Test
  void testGetMovingAverageByGroupOutOfPeriod() {
    // given: pre-existent data
    fixtures.load(VotingStatsRepositoryTests.class, "testFindMovingAverageByGroup.sql");

    // when: asking to calculate the moving average from a future period
    OffsetDateTime dateTime = OffsetDateTime.parse("2020-06-30T12:00:01+01:00");

    var movingAverage =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map((group) -> votingStatsRepository.findMovingAverageByGroup(group, dateTime))
            .orElse(Optional.empty());

    // then: we should get no average
    assertEquals(movingAverage, Optional.empty());
  }

  @Test
  void testGetGroupStatsFindAllVotingStats() {
    // given: pre-existent data
    fixtures.load(VotingStatsRepositoryTests.class, "testFindMovingAverageByGroup.sql");
    var request = new OffsetPaginationRequest(1, 1);

    // when: asking to calculate the moving average between two dates that comprise all data
    var statsPage =
        groupRepository
            .findById(UUID.fromString("d64db962-3455-11e9-b210-d663bd873d93"))
            .map((group) -> votingStatsRepository.findStatsByGroup(group, request))
            .orElse(OffsetPaginationResult.empty());

    // then: we should get all voting stats
    assertEquals(statsPage.getTotalCount(), 2);
  }
}
