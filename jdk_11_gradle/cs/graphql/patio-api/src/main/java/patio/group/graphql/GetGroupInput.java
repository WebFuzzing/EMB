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
 * Input to build a group
 *
 * @since 0.1.0
 */
public class GetGroupInput {
  private final UUID groupId;
  private final UUID currentUserId;

  /**
   * Gets group id.
   *
   * @return the group id
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Gets current user id.
   *
   * @return the current user id
   */
  public UUID getCurrentUserId() {
    return currentUserId;
  }

  /**
   * Initializes the input
   *
   * @param groupId the group id
   * @param currentUserId the current user id
   */
  public GetGroupInput(UUID groupId, UUID currentUserId) {
    this.groupId = groupId;
    this.currentUserId = currentUserId;
  }

  /**
   * Creates a new builder to create a new instance of type {@link GetGroupInput}
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builds an instance of type {@link GetGroupInput}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient GetGroupInput input = new GetGroupInput(null, null);

    private Builder() {
      /* empty */
    }

    /**
     * Sets the currentUserId
     *
     * @param currentUserId the current user's id
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withCurrentUserId(UUID currentUserId) {
      this.input = new GetGroupInput(input.getGroupId(), currentUserId);
      return this;
    }

    /**
     * Sets the groupId
     *
     * @param groupId the group id
     * @return the builder
     * @since 0.1.0
     */
    public Builder withGroupId(UUID groupId) {
      this.input = new GetGroupInput(groupId, input.getCurrentUserId());
      return this;
    }

    /**
     * Returns the instance built with this builder
     *
     * @return an instance of type {@link GetGroupInput}
     * @since 0.1.0
     */
    public GetGroupInput build() {
      return this.input;
    }
  }
}
