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
package patio.voting.services;

/**
 * Scheduled tasks
 *
 * @since 0.1.0
 */
public interface VotingScheduling {

  /**
   * Checks whether a new voting should be created, and if so, creates the new {@link
   * patio.voting.domain.Voting} instance and sends a notification to all members of that voting's
   * group
   *
   * @since 0.1.0
   */
  void scheduleVoting();
}
