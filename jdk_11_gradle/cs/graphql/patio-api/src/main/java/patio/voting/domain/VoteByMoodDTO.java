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
package patio.voting.domain;

/**
 * Represents the aggregation count of mood types in a given voting
 *
 * @see Voting
 */
public class VoteByMoodDTO {

  private final int mood;
  private final long count;

  /**
   * Initializes a new {@link VoteByMoodDTO}
   *
   * @param count how many people did vote this mood
   * @param mood the type of mood
   */
  public VoteByMoodDTO(long count, int mood) {
    this.mood = mood;
    this.count = count;
  }

  /**
   * Returns the type of mood
   *
   * @return the type of mood
   */
  public int getMood() {
    return mood;
  }

  /**
   * Returns how many people did vote a given type of mood
   *
   * @return how many people did vote a given type of mood
   */
  public long getCount() {
    return count;
  }
}
