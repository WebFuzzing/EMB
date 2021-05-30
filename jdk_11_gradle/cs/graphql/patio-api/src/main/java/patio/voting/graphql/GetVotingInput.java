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
 * Input to build a voting
 *
 * @since 0.1.0
 */
public class GetVotingInput {
  private final UUID votingId;
  private final UUID currentUserId;

  /**
   * Gets voting id.
   *
   * @return the voting id
   */
  public UUID getVotingId() {
    return votingId;
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
   * @param votingId the voting id
   * @param currentUserId the current user id
   */
  public GetVotingInput(UUID votingId, UUID currentUserId) {
    this.votingId = votingId;
    this.currentUserId = currentUserId;
  }

  /**
   * Creates a new builder to create a new instance of type {@link GetVotingInput}
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Builds an instance of type {@link GetVotingInput}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient GetVotingInput input = new GetVotingInput(null, null);

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
      this.input = new GetVotingInput(input.getVotingId(), currentUserId);
      return this;
    }

    /**
     * Sets the votingId
     *
     * @param votingId the voting id
     * @return the builder
     * @since 0.1.0
     */
    public Builder withVotingId(UUID votingId) {
      this.input = new GetVotingInput(votingId, input.getCurrentUserId());
      return this;
    }

    /**
     * Returns the instance built with this builder
     *
     * @return an instance of type {@link GetVotingInput}
     * @since 0.1.0
     */
    public GetVotingInput build() {
      return this.input;
    }
  }
}
