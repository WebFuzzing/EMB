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
package patio.group.services.internal;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.common.domain.utils.Builder;
import patio.common.domain.utils.NotPresent;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.graphql.GetGroupInput;
import patio.group.graphql.UpsertGroupInput;
import patio.group.repositories.GroupRepository;
import patio.group.repositories.UserGroupRepository;
import patio.group.services.GroupService;
import patio.infrastructure.utils.OptionalUtils;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/**
 * Business logic regarding {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultGroupService implements GroupService {

  private final transient GroupRepository groupRepository;
  private final transient UserGroupRepository userGroupRepository;
  private final transient UserRepository userRepository;

  /**
   * Initializes service by using the database repositories
   *
   * @param groupRepository an instance of {@link GroupRepository}
   * @param userRepository an instance of {@link UserRepository}
   * @param userGroupRepository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public DefaultGroupService(
      GroupRepository groupRepository,
      UserRepository userRepository,
      UserGroupRepository userGroupRepository) {
    this.groupRepository = groupRepository;
    this.userRepository = userRepository;
    this.userGroupRepository = userGroupRepository;
  }

  @Override
  public Iterable<Group> listGroups() {
    return groupRepository.findAll();
  }

  @Override
  public List<Group> listGroupsUser(UUID userId) {
    return userRepository.findById(userId).stream()
        .map(User::getGroups)
        .flatMap(Set::stream)
        .map(UserGroup::getGroup)
        .sorted(Comparator.comparing(Group::getName))
        .collect(Collectors.toList());
  }

  @Override
  public Group createGroup(UpsertGroupInput input) {
    Group groupToSave =
        Group.builder()
            .with(g -> g.setName(input.getName()))
            .with(g -> g.setAnonymousVote(input.isAnonymousVote()))
            .with(g -> g.setVotingDays(input.getVotingDays()))
            .with(g -> g.setVotingTime(input.getVotingTime()))
            .with(g -> g.setVotingDuration(input.getVotingDuration()))
            .build();

    Optional<User> user = userRepository.findById(input.getCurrentUserId());
    Optional<Group> group = Optional.of(groupRepository.save(groupToSave));

    return OptionalUtils.combine(user, group)
        .into(this::createUserGroupAdmin)
        .map(userGroupRepository::save)
        .map(UserGroup::getGroup)
        .orElse(null);
  }

  private UserGroup createUserGroupAdmin(User user, Group group) {
    UserGroup userGroup = new UserGroup(user, group);
    userGroup.setAdmin(true);
    return userGroup;
  }

  @Override
  public Result<Group> updateGroup(UpsertGroupInput input) {
    UserIsGroupAdmin userIsGroupAdmin = new UserIsGroupAdmin(userGroupRepository);

    return Result.<Group>create()
        .thenCheck(() -> userIsGroupAdmin.check(input.getCurrentUserId(), input.getGroupId()))
        .then(() -> updateGroupIfSuccess(input));
  }

  private Group updateGroupIfSuccess(UpsertGroupInput input) {
    return groupRepository
        .findById(input.getGroupId())
        .map(g -> Builder.build(() -> g))
        .map(b -> b.with(g -> g.setName(input.getName())))
        .map(b -> b.with(g -> g.setAnonymousVote(input.isAnonymousVote())))
        .map(b -> b.with(g -> g.setVotingDays(input.getVotingDays())))
        .map(b -> b.with(g -> g.setVotingTime(input.getVotingTime())))
        .map(b -> b.with(g -> g.setVotingDuration(input.getVotingDuration())))
        .map(Builder::build)
        .map(groupRepository::update)
        .orElse(null);
  }

  @Override
  public Result<Group> getGroup(GetGroupInput input) {
    Optional<Group> group = groupRepository.findById(input.getGroupId());
    Optional<User> currentUser = userRepository.findById(input.getCurrentUserId());

    NotPresent notPresent = new NotPresent();
    UserIsInGroup userIsInGroup = new UserIsInGroup();

    return Result.<Group>create()
        .thenCheck(() -> notPresent.check(group))
        .thenCheck(() -> userIsInGroup.check(currentUser, group))
        .then(() -> group.get());
  }

  @Override
  public Result<Group> getMyFavouriteGroup(UUID userId) {
    return Result.from(groupRepository.findMyFavouriteGroupByUserId(userId));
  }
}
