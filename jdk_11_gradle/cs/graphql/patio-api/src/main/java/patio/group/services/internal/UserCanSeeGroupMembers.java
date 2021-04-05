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

import static patio.common.domain.utils.Check.checkIsTrue;
import static patio.infrastructure.utils.ErrorConstants.NOT_ALLOWED;

import java.util.Optional;
import java.util.UUID;
import patio.common.domain.utils.Check;
import patio.common.domain.utils.Result;
import patio.group.domain.UserGroup;
import patio.group.domain.UserGroupKey;
import patio.group.repositories.UserGroupRepository;

/**
 * Checks if a given user is allowed to see members of a given group
 *
 * @since 0.1.0
 */
public class UserCanSeeGroupMembers {

  private final transient UserGroupRepository repository;

  /**
   * Constructor receiving a repository to access the underlying datastore
   *
   * @param repository an instance of {@link UserGroupRepository}
   * @since 0.1.0
   */
  public UserCanSeeGroupMembers(UserGroupRepository repository) {
    this.repository = repository;
  }

  /**
   * Checks that the
   *
   * @param userId the user that wants to see the member list
   * @param groupId the group we want to see its members from
   * @param isVisibleMemberList whether the group allowed users to see member list or not
   * @return a failing {@link Result} if the group didn't allowed to see its members or if allowing
   *     the user is not an admin
   * @since 0.1.0
   */
  public Check check(UUID userId, UUID groupId, boolean isVisibleMemberList) {
    Optional<UserGroup> userGroup = repository.findById(new UserGroupKey(userId, groupId));
    boolean isAdmin = userGroup.map(UserGroup::isAdmin).orElse(false);
    return checkIsTrue(isAdmin || isVisibleMemberList, NOT_ALLOWED);
  }
}
