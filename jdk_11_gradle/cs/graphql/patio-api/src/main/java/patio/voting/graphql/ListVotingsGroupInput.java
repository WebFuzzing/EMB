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

/**
 * ListVotingsGroupInput input. It contains the id for a group, and the dates between which the
 * votings are wanted
 *
 * @since 0.1.0
 */
public class ListVotingsGroupInput {
  private final OffsetDateTime startDate;
  private final OffsetDateTime endDate;
  private final UUID groupId;

  /**
   * Returns the startDate
   *
   * @return the startDate
   * @since 0.1.0
   */
  public OffsetDateTime getStartDate() {
    return startDate;
  }

  /**
   * Returns the endDate
   *
   * @return the endDate
   * @since 0.1.0
   */
  public OffsetDateTime getEndDate() {
    return endDate;
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
   * Creates a new builder to create a new instance of type {@link ListVotingsGroupInput}
   *
   * @return an instance of {@link Builder}
   * @since 0.1.0
   */
  public static Builder newBuilder() {
    return new Builder();
  }

  /**
   * Initializes the input with the user email and the group id
   *
   * @param groupId the id of the group
   * @param startDate the startDate
   * @param endDate the endDate
   * @since 0.1.0
   */
  public ListVotingsGroupInput(UUID groupId, OffsetDateTime startDate, OffsetDateTime endDate) {
    this.groupId = groupId;
    this.startDate = startDate;
    this.endDate = endDate;
  }

  /**
   * Builds an instance of type {@link CreateVoteInput}
   *
   * @since 0.1.0
   */
  public static class Builder {

    private transient ListVotingsGroupInput input = new ListVotingsGroupInput(null, null, null);

    private Builder() {
      /* empty */
    }

    /**
     * Sets the groupId
     *
     * @param groupId the group's id
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withGroupId(UUID groupId) {
      this.input = new ListVotingsGroupInput(groupId, input.getStartDate(), input.getEndDate());
      return this;
    }

    /**
     * Sets the startDate
     *
     * @param startDate the startDate
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withStartDate(OffsetDateTime startDate) {
      this.input = new ListVotingsGroupInput(input.getGroupId(), startDate, input.getEndDate());
      return this;
    }

    /**
     * Sets the endDate
     *
     * @param endDate the endDate
     * @return current builder instance
     * @since 0.1.0
     */
    public Builder withEndDate(OffsetDateTime endDate) {
      this.input = new ListVotingsGroupInput(input.getGroupId(), input.getStartDate(), endDate);
      return this;
    }

    /**
     * Returns the instance built with this builder
     *
     * @return an instance of type {@link ListVotingsGroupInput}
     * @since 0.1.0
     */
    public ListVotingsGroupInput build() {
      return this.input;
    }
  }
}
