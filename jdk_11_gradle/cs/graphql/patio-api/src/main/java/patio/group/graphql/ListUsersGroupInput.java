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
package patio.group.graphql;

import java.util.UUID;

/**
 * ListUsersGroupInput input. It contains the ids for a user and a group, and a boolean indicating
 * if the group has a visible member list
 *
 * @since 0.1.0
 */
public class ListUsersGroupInput {
  private final UUID userId;
  private final UUID groupId;

  /**
   * Returns the id of the user
   *
   * @return the id of the user
   * @since 0.1.0
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Returns the id of the group
   *
   * @return the id of the group
   * @since 0.1.0
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Initializes the input with the user id, the group id, and visibleMemberList
   *
   * @param userId the id of the user
   * @param groupId the id of the group
   * @since 0.1.0
   */
  public ListUsersGroupInput(UUID userId, UUID groupId) {
    this.userId = userId;
    this.groupId = groupId;
  }
}
