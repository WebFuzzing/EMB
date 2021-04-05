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

/**
 * Input object to request for voting stats
 *
 * @see patio.voting.domain.Voting
 */
public class VotingStatsInput {
  private UUID votingId;

  /**
   * Returns the id of the voting
   *
   * @return the id of the user
   * @since 0.1.0
   */
  public UUID getVotingId() {
    return votingId;
  }

  /**
   * Sets voting id
   *
   * @param votingId the voting id
   */
  public void setVotingId(UUID votingId) {
    this.votingId = votingId;
  }

  /**
   * Returns true if there is a voting id false otherwise
   *
   * @return true if there is a voting id false otherwise
   */
  public boolean hasVoting() {
    return this.votingId != null;
  }

  /**
   * Creates a builder to build instances of type {@link VotingStatsInput}
   *
   * @return a {@link Builder} that creates instances of type {@link VotingStatsInput}
   * @since 0.1.0
   */
  public static Builder<VotingStatsInput> builder() {
    return Builder.build(VotingStatsInput::new);
  }
}
