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

import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import patio.common.domain.utils.Builder;

/**
 * UpsertGroupInput input. It contains the fields for a Group
 *
 * @since 0.1.0
 */
public class UpsertGroupInput {
  private UUID groupId;
  private String name;
  private boolean anonymousVote;
  private List<DayOfWeek> votingDays;
  private OffsetTime votingTime;
  private int votingDuration;
  private UUID currentUserId;

  /**
   * Creates a new builder to create a new instance of type {@link UpsertGroupInput}
   *
   * @return an instance of UpsertGroupInput builder
   */
  public static Builder<UpsertGroupInput> newBuilder() {
    return Builder.build(UpsertGroupInput::new);
  }

  /**
   * Returns the name of the group
   *
   * @return the name of the group
   * @since 0.1.0
   */
  public String getName() {
    return name;
  }

  /**
   * Returns whether the vote is allowed to be anonymous in this group or not
   *
   * @return true if it's allowed false otherwise
   * @since 0.1.0
   */
  public boolean isAnonymousVote() {
    return anonymousVote;
  }

  /**
   * Returns the days of the week as an array of type {@link DayOfWeek}
   *
   * @return the days of the week
   * @since 0.1.0
   */
  public List<DayOfWeek> getVotingDays() {
    return this.votingDays;
  }

  /**
   * Returns the votingTime of the day when the reminder is sent
   *
   * @return an instance of type {@link OffsetTime}
   * @since 0.1.0
   */
  public OffsetTime getVotingTime() {
    return votingTime;
  }

  /**
   * Gets currentUserId.
   *
   * @return Value of currentUserId.
   */
  public UUID getCurrentUserId() {
    return currentUserId;
  }

  /**
   * Gets groupId.
   *
   * @return Value of groupId.
   */
  public UUID getGroupId() {
    return groupId;
  }

  /**
   * Gets votingDuration.
   *
   * @return Value of votingDuration.
   */
  public int getVotingDuration() {
    return votingDuration;
  }

  /**
   * Sets the groupId
   *
   * @param groupId the group's id
   * @since 0.1.0
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }

  /**
   * Sets the name
   *
   * @param name the group's name
   * @since 0.1.0
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Sets the anonymousVote
   *
   * @param anonymousVote the group's anonymousVote
   * @since 0.1.0
   */
  public void setAnonymousVote(boolean anonymousVote) {
    this.anonymousVote = anonymousVote;
  }

  /**
   * Sets the votingDays
   *
   * @param votingDays the group's votingDays
   * @since 0.1.0
   */
  public void setVotingDays(List<DayOfWeek> votingDays) {
    this.votingDays = votingDays;
  }

  /**
   * Sets the votingTime
   *
   * @param votingTime the group's votingTime
   * @since 0.1.0
   */
  public void setVotingTime(OffsetTime votingTime) {
    this.votingTime = votingTime;
  }

  /**
   * Sets the votingTime
   *
   * @param votingDuration the group's votingDuration (in hours)
   * @since 0.1.0
   */
  public void setVotingDuration(int votingDuration) {
    this.votingDuration = votingDuration;
  }

  /**
   * Sets the currentUserId
   *
   * @param currentUserId the currentUserId
   * @since 0.1.0
   */
  public void setCurrentUserId(UUID currentUserId) {
    this.currentUserId = currentUserId;
  }
}
