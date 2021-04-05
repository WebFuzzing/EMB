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
package patio.voting.services.internal;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static io.github.benas.randombeans.api.EnhancedRandom.randomSetOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.repositories.GroupRepository;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailService;
import patio.infrastructure.email.services.internal.EmailComposerService;
import patio.infrastructure.email.services.internal.templates.URLResolverService;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;
import patio.voting.domain.Voting;
import patio.voting.repositories.VotingRepository;
import patio.voting.services.VotingStatsService;

public class VotingSchedulingServiceTests {

  @Test
  @SuppressWarnings("unchecked")
  void testNotifyNewVotingToMembers() {
    // given: mocked services
    var groupRepository = Mockito.mock(GroupRepository.class);
    var votingRepository = Mockito.mock(VotingRepository.class);
    var votingStatsService = Mockito.mock(VotingStatsService.class);
    var emailComposerService = Mockito.mock(EmailComposerService.class);
    var emailService = Mockito.mock(EmailService.class);
    var urlResolverService = Mockito.mock(URLResolverService.class);
    var userRepository = Mockito.mock(UserRepository.class);

    // and: mocking behaviors
    var group1Users = randomSetOf(2, UserGroup.class);
    var group1 =
        Group.builder().with(g -> g.setName("eligible")).with(g -> g.setUsers(group1Users)).build();
    var closedVotingGroups = randomListOf(4, Group.class);
    var groupsVotingToday = new ArrayList<Group>(closedVotingGroups);
    groupsVotingToday.add(group1);

    Mockito.when(groupRepository.findAllGroupsInVotingDayAndInVotingPeriod(any(), any()))
        .thenReturn(groupsVotingToday.stream());

    Mockito.when(groupRepository.findAllGroupsWithVotingInCurrentVotingPeriod())
        .thenReturn(closedVotingGroups.stream());

    var voting = Voting.newBuilder().with(v -> v.setGroup(group1)).build();
    Mockito.when(votingRepository.save(any(Voting.class))).thenReturn(voting);

    Mockito.when(emailComposerService.composeEmail(any(), any(), any(), any()))
        .thenReturn(random(Email.class));
    Mockito.when(emailComposerService.getTodayMessage()).thenReturn(random(String.class));

    Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(User.builder().build()));
    User user = User.builder().with(u -> u.setName("john")).build();
    UserGroup userGroup = new UserGroup(user, group1);
    Group mockedGroup = Group.builder().with(g -> g.setUsers(Set.of(userGroup))).build();
    Mockito.when(groupRepository.findById(any(UUID.class))).thenReturn(Optional.of(mockedGroup));

    // and: creating an instance of scheduling service
    var schedulingService =
        new VotingSchedulingService(
            "/groups/{0}/votings/{1}/vote",
            groupRepository,
            votingRepository,
            votingStatsService,
            emailComposerService,
            emailService,
            urlResolverService);

    // when: executing scheduling task
    schedulingService.scheduleVoting();

    // then: it lists the groups that should be notified
    verify(groupRepository, times(1)).findAllGroupsWithVotingInCurrentVotingPeriod();

    verify(groupRepository, times(1)).findAllGroupsInVotingDayAndInVotingPeriod(any(), any());

    // and: takes each group details
    verify(votingRepository, times(1)).save(any());

    // and: voting statistics are initialized
    verify(votingStatsService, times(1)).createVotingStat(any());

    // and: verifies the existence of votings to be expired
    verify(groupRepository, times(1)).findAllExpiredVotingsByTime(any());

    // and: composes an email for each user
    verify(emailComposerService, atLeast(4)).getMessage(any());
    verify(emailComposerService, atLeast(4)).getMessage(any(), any());
    verify(emailComposerService, atLeast(2)).composeEmail(any(), any(), any(), any());

    // and: sends an email for each user
    verify(emailService, atLeast(2)).send(any(Email.class));
  }
}
