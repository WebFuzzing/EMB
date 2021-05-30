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
package patio.group.domain;

import com.vladmihalcea.hibernate.type.array.ListArrayType;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import patio.common.domain.utils.Builder;

/**
 * Represents the different groups a user could belong to
 *
 * @since 0.1.0
 */
@Entity
@Table(name = "groups")
@TypeDef(name = "list-array", typeClass = ListArrayType.class)
public final class Group {

  @Column(name = "name")
  private String name;

  @Id @GeneratedValue private UUID id;

  @Column(name = "anonymous_vote")
  private boolean anonymousVote;

  @Type(
      type = "com.vladmihalcea.hibernate.type.array.ListArrayType",
      parameters = {@Parameter(name = ListArrayType.SQL_ARRAY_TYPE, value = "text")})
  @Column(name = "voting_days", columnDefinition = "text[]")
  private List<DayOfWeek> votingDays;

  @Column(name = "voting_time")
  private OffsetTime votingTime;

  @OneToMany(mappedBy = "group")
  private Set<UserGroup> users;

  @Column(name = "voting_duration")
  private Integer votingDuration; /* in hours */

  /**
   * Creates a new {@link Group} builder
   *
   * @return a builder to create {@link Group} instances
   * @since 0.1.0
   */
  public static Builder<Group> builder() {
    return Builder.build(Group::new);
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
   * Sets the group's name
   *
   * @param name the group's name
   * @since 0.1.0
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Returns the unique identifier of the group
   *
   * @return an instance of {@link UUID}
   * @since 0.1.0
   */
  public UUID getId() {
    return id;
  }

  /**
   * Sets the unique identifier of a group
   *
   * @param id an instance of {@link UUID}
   * @since 0.1.0
   */
  public void setId(UUID id) {
    this.id = id;
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
   * Sets whether the vote is allowed to be anonymous in this group or not
   *
   * @param anonymousVote true if it's allowed, false otherwise
   * @since 0.1.0
   */
  public void setAnonymousVote(boolean anonymousVote) {
    this.anonymousVote = anonymousVote;
  }

  /**
   * Returns the days of the week when reminders are sent
   *
   * @return an array of {@link DayOfWeek}
   * @since 0.1.0
   */
  public List<DayOfWeek> getVotingDays() {
    return this.votingDays;
  }

  /**
   * Sets the days of the week when reminders are sent
   *
   * @param votingDays days of the week when reminders are sent
   * @since 0.1.0
   */
  public void setVotingDays(List<DayOfWeek> votingDays) {
    this.votingDays = votingDays;
  }

  /**
   * Returns when the reminders are sent during the day
   *
   * @return the moment when reminders are sent
   * @since 0.1.0
   */
  public OffsetTime getVotingTime() {
    return votingTime;
  }

  /**
   * Sets the moment when reminders are sent
   *
   * @param votingTime an instance of type {@link OffsetTime}
   * @since 0.1.0
   */
  public void setVotingTime(OffsetTime votingTime) {
    this.votingTime = votingTime;
  }

  /**
   * Get the duration (in hours) during which the voting will be open
   *
   * @return the number of hours
   */
  public Integer getVotingDuration() {
    return votingDuration;
  }

  /**
   * Sets the number of hours during which the duration will be open
   *
   * @param votingDuration number of hours
   */
  public void setVotingDuration(Integer votingDuration) {
    this.votingDuration = votingDuration;
  }

  public Set<UserGroup> getUsers() {
    return users;
  }

  public void setUsers(Set<UserGroup> users) {
    this.users = users;
  }
}
