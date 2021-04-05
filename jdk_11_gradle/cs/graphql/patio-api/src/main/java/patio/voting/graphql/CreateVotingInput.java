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

/**
 * Input to create a new voting slot
 *
 * @since 0.1.0
 */
public class CreateVotingInput {
  private final UUID userId;
  private final UUID groupId;

  private CreateVotingInput(UUID userId, UUID groupId) {
    this.userId = userId;
    this.groupId = groupId;
  }

  /**
   * Creates a new builder to create an instance of type {@link CreateVotingInput}
   *
   * @return a new instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Returns the user id
   *
   * @return the id of the user creating the voting
   * @since 0.1.0
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * Returns the group the voting slot belongs
   *
   * @return the id of the group this voting belongs
   * @since 0.1.0
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Responsible for building instances of type {@link CreateVotingInput}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient CreateVotingInput input = new CreateVotingInput(null, null);

    private Builder() {
      /* empty */
    }

    /**
     * Sets input user's id
     *
     * @param userId id of the user
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withUserId(UUID userId) {
      this.input = new CreateVotingInput(userId, input.getGroupId());
      return this;
    }

    /**
     * Sets the voting group id
     *
     * @param groupId the id of the group the voting belongs
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withGroupId(UUID groupId) {
      this.input = new CreateVotingInput(input.getUserId(), groupId);
      return this;
    }

    /**
     * Returns the instance of type {@link CreateVotingInput} created with this builder
     *
     * @return instance of type {@link CreateVotingInput}
     * @since 0.1.0
     */
    public CreateVotingInput build() {
      return this.input;
    }
  }
}
