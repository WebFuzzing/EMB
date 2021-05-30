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
 * AddUserToGroupInput input. It contains the ids for a user and a group
 *
 * @since 0.1.0
 */
public class AddUserToGroupInput {
  private final String email;
  private final UUID groupId;
  private final UUID currentUserId;

  /**
   * Returns the email of the user
   *
   * @return the email of the user
   * @since 0.1.0
   */
  public String getEmail() {
    return email;
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
   * Returns the id of the current user
   *
   * @return the id of the current user
   * @since 0.1.0
   */
  public UUID getCurrentUserId() {
    return currentUserId;
  }

  /**
   * Initializes the input with the user email and the group id
   *
   * @param currentUserId the id of the current user
   * @param email the email of the user
   * @param groupId the id of the group
   * @since 0.1.0
   */
  public AddUserToGroupInput(UUID currentUserId, String email, UUID groupId) {
    this.currentUserId = currentUserId;
    this.email = email;
    this.groupId = groupId;
  }
}
