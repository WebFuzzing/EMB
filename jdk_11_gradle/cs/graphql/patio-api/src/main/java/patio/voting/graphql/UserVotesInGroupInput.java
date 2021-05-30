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
package patio.voting.graphql;

import java.time.OffsetDateTime;
import java.util.UUID;
import patio.common.domain.utils.Builder;

/**
 * UserVotesInGroupInput input. It contains the id for a group, for an user, and the dates between
 * which the votes are wanted
 *
 * @since 0.1.0
 */
public final class UserVotesInGroupInput {
  private OffsetDateTime startDateTime;
  private OffsetDateTime endDateTime;
  private UUID groupId;
  private UUID userId;
  private UUID currentUserId;

  /**
   * Returns the startDateTime
   *
   * @return the startDateTime
   * @since 0.1.0
   */
  public OffsetDateTime getStartDateTime() {
    return startDateTime;
  }

  /**
   * Returns the endDateTime
   *
   * @return the endDateTime
   * @since 0.1.0
   */
  public OffsetDateTime getEndDateTime() {
    return endDateTime;
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
   * Sets start date time.
   *
   * @param startDateTime the start date time
   */
  public void setStartDateTime(OffsetDateTime startDateTime) {
    this.startDateTime = startDateTime;
  }

  /**
   * Sets end date time.
   *
   * @param endDateTime the end date time
   */
  public void setEndDateTime(OffsetDateTime endDateTime) {
    this.endDateTime = endDateTime;
  }

  /**
   * Sets group id.
   *
   * @param groupId the group id
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  /**
   * Sets user id.
   *
   * @param userId the user id
   */
  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  /**
   * Sets current user id.
   *
   * @param currentUserId the current user id
   */
  public void setCurrentUserId(UUID currentUserId) {
    this.currentUserId = currentUserId;
  }

  /**
   * Creates a builder to build instances of type {@link UserVotesInGroupInput}
   *
   * @return a {@link Builder} that creates instances of type {@link UserVotesInGroupInput}
   * @since 0.1.0
   */
  public static Builder<UserVotesInGroupInput> builder() {
    return Builder.build(UserVotesInGroupInput::new);
  }
}
