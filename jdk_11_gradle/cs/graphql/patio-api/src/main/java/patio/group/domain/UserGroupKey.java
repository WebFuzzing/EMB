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
package patio.group.domain;

import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/** Represents the id of a {@link UserGroup} instance */
@Embeddable
public class UserGroupKey implements Serializable {

  private static final long serialVersionUID = 0;

  @Column(name = "user_id")
  private UUID userId;

  @Column(name = "group_id")
  private UUID groupId;

  /** Default constructor */
  public UserGroupKey() {
    /* empty */
  }

  /**
   * Initializes a {@link UserGroupKey} with the user's id and the group's id
   *
   * @param userId user's id
   * @param groupId group's id
   */
  public UserGroupKey(UUID userId, UUID groupId) {
    this.userId = userId;
    this.groupId = groupId;
  }

  /**
   * Returns id of the user
   *
   * @return id of the user
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Sets the user's id
   *
   * @param userId the user's id
   */
  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  /**
   * Returns the group's id
   *
   * @return the group's id
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Sets the group's id
   *
   * @param groupId the group's id
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }
}
