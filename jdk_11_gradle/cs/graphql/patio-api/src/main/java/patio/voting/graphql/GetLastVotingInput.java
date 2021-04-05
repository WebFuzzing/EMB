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

import java.util.UUID;
import patio.common.domain.utils.Builder;
import patio.voting.domain.Voting;

/**
 * Input to get the last voting for a group
 *
 * @since 0.1.0
 */
public class GetLastVotingInput {
  private UUID groupId;
  private UUID currentUserId;

  /**
   * Creates a new fluent builder to build instances of type {@link Voting}
   *
   * @return an instance of the voting builder
   * @since 0.1.0
   */
  public static Builder<GetLastVotingInput> newBuilder() {
    return Builder.build(GetLastVotingInput::new);
  }

  /**
   * Gets group id.
   *
   * @return the group id
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Gets the current user.
   *
   * @return the current user
   */
  public UUID getCurrentUserId() {
    return currentUserId;
  }

  /**
   * Sets the group id
   *
   * @param groupId the group id
   * @since 0.1.0
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  /**
   * Sets the group id
   *
   * @param currentUserId the group id
   * @since 0.1.0
   */
  public void setCurrentUserId(UUID currentUserId) {
    this.currentUserId = currentUserId;
  }
}
