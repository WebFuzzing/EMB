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
import patio.user.domain.User;

/**
 * Input to know whether a user's voted in a given voting or not
 *
 * @see VotingFetcher
 */
public class DidIVoteInput {
  private final User user;
  private final UUID votingId;

  /**
   * Initializes with the user and voting to check against
   *
   * @param user the current {@link User}
   * @param votingId voting's id to check against
   */
  public DidIVoteInput(User user, UUID votingId) {
    this.user = user;
    this.votingId = votingId;
  }

  /**
   * Returns the user to check
   *
   * @return the user to check
   */
  public User getUser() {
    return user;
  }

  /**
   * Returns the voting's id to check against
   *
   * @return the voting's id to check against
   */
  public UUID getVotingId() {
    return votingId;
  }
}
