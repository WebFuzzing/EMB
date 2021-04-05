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
package patio.group.services;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.domain.UserGroupKey;
import patio.group.graphql.AddUserToGroupInput;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.internal.DefaultUserGroupService;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

public class UserGroupServiceTests {

  @Test
  public void testAddUserToGroupSuccessfully() {
    // setup: mocked repositories
    var userRepository = mock(UserRepository.class);
    var groupRepository = mock(GroupRepository.class);
    var userGroupRepository = mock(UserGroupRepository.class);

    // and: dummy data
    var loggedUser = Optional.of(random(User.class));
    when(userRepository.findByEmail(any(String.class))).thenReturn(loggedUser);

    var group = Optional.of(random(Group.class, "users"));
    when(groupRepository.findById(any(UUID.class))).thenReturn(group);

    var userGroupAdmin = new UserGroup(loggedUser.get(), group.get());
    userGroupAdmin.setAdmin(true);
    when(userGroupRepository.findById(any(UserGroupKey.class)))
        .thenReturn(Optional.of(userGroupAdmin))
        .thenReturn(Optional.empty());

    // when: trying to add a user into a group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);
    var userToInvite = Optional.of(random(User.class));
    var input =
        new AddUserToGroupInput(
            loggedUser.get().getId(), userToInvite.get().getEmail(), group.get().getId());
    var result = userGroupService.addUserToGroup(input);

    // then: the result is successful
    assertTrue(result.isSuccess());

    // and: and the invocations are correct
    verify(userRepository, times(1)).findByEmail(any(String.class));
    verify(groupRepository, times(1)).findById(any(UUID.class));
    verify(userGroupRepository, times(2)).findById(any(UserGroupKey.class));
    verify(userGroupRepository, times(1)).save(any(UserGroup.class));
  }

  @Test
  public void testAddUserToGroupFailsBecauseIsNotAdmin() {
    // setup: mocked repositories
    var userRepository = mock(UserRepository.class);
    var groupRepository = mock(GroupRepository.class);
    var userGroupRepository = mock(UserGroupRepository.class);

    // and: dummy data
    var loggedUser = Optional.of(random(User.class));
    when(userRepository.findByEmail(any(String.class))).thenReturn(loggedUser);

    var group = Optional.of(random(Group.class, "users"));
    when(groupRepository.findById(any(UUID.class))).thenReturn(group);

    when(userGroupRepository.findById(any(UserGroupKey.class)))
        .thenReturn(Optional.of(new UserGroup(loggedUser.get(), group.get())))
        .thenReturn(Optional.empty());

    // when: trying to add a user into a group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);
    var userToInvite = Optional.of(random(User.class));
    var input =
        new AddUserToGroupInput(
            loggedUser.get().getId(), userToInvite.get().getEmail(), group.get().getId());
    var result = userGroupService.addUserToGroup(input);

    // then: the result is successful
    assertTrue(result.hasErrors());

    // and: and the invocations are correct
    verify(userRepository, times(1)).findByEmail(any(String.class));
    verify(groupRepository, times(1)).findById(any(UUID.class));
    verify(userGroupRepository, times(1)).findById(any(UserGroupKey.class));
  }

  @Test
  public void testAddUserToGroupFailsBecauseIsAlreadyInGroup() {
    // setup: mocked repositories
    var userRepository = mock(UserRepository.class);
    var groupRepository = mock(GroupRepository.class);
    var userGroupRepository = mock(UserGroupRepository.class);

    // and: dummy data
    var loggedUser = Optional.of(random(User.class));
    when(userRepository.findByEmail(any(String.class))).thenReturn(loggedUser);

    var group = Optional.of(random(Group.class, "users"));
    when(groupRepository.findById(any(UUID.class))).thenReturn(group);

    var userGroupAdmin = new UserGroup(loggedUser.get(), group.get());
    userGroupAdmin.setAdmin(true);
    when(userGroupRepository.findById(any(UserGroupKey.class)))
        .thenReturn(Optional.of(userGroupAdmin))
        .thenReturn(Optional.of(new UserGroup(random(User.class), random(Group.class))));

    // when: trying to add a user into a group
    var userGroupService =
        new DefaultUserGroupService(groupRepository, userRepository, userGroupRepository);
    var userToInvite = Optional.of(random(User.class));
    var input =
        new AddUserToGroupInput(
            loggedUser.get().getId(), userToInvite.get().getEmail(), group.get().getId());
    var result = userGroupService.addUserToGroup(input);

    // then: the result is successful
    assertTrue(result.hasErrors());

    // and: and the invocations are correct
    verify(userRepository, times(1)).findByEmail(any(String.class));
    verify(groupRepository, times(1)).findById(any(UUID.class));
    verify(userGroupRepository, times(2)).findById(any(UserGroupKey.class));
  }
}
