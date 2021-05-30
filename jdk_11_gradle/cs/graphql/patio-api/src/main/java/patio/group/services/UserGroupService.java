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

import java.util.UUID;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.graphql.AddUserToGroupInput;
import patio.group.graphql.LeaveGroupInput;
import patio.group.graphql.ListUsersGroupInput;
import patio.user.domain.User;

/**
 * Business logic contracts regarding user groups
 *
 * @since 0.1.0
 */
public interface UserGroupService {

  /**
   * Adds an user to a group, if the current user is admin of the group
   *
   * @param input user and group information
   * @return an instance of {@link Result} (Boolean | {@link Error})
   * @since 0.1.0
   */
  Result<Boolean> addUserToGroup(AddUserToGroupInput input);

  /**
   * Fetches the list of users in a Group. ifMatches the user is not allowed to build them, returns
   * an empty list
   *
   * @param input a {@link AddUserToGroupInput} with the user and the group
   * @return a list of {@link User} instances
   * @since 0.1.0
   */
  Iterable<User> listUsersGroup(ListUsersGroupInput input);

  /**
   * Make the current user leave the specified group
   *
   * @param input required data to retrieve a {@link Group}
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  Result<Boolean> leaveGroup(LeaveGroupInput input);

  /**
   * Checks whether the user is admin is a given group or not
   *
   * @param userId user's id
   * @param groupId group's id
   * @return true if the user is admin in the given group
   */
  boolean isAdmin(UUID userId, UUID groupId);
}
