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

import io.micronaut.data.annotation.DateCreated;
import java.time.OffsetDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import patio.common.domain.utils.Builder;

/**
 * Represents all the statistics which belong to a {@link Voting}
 *
 * @since 0.1.0
 */
@Entity
@Table(name = "voting_stats")
public final class VotingStats {

  @Id @GeneratedValue private UUID id;

  @OneToOne
  @JoinColumn(name = "voting_id", referencedColumnName = "id")
  private Voting voting;

  @DateCreated
  @Column(name = "created_at")
  private OffsetDateTime createdAtDateTime;

  private Double average;

  @Column(name = "moving_average")
  private Double movingAverage;

  /**
   * Creates a new fluent builder to build instances of type {@link VotingStats}
   *
   * @return an instance of the voting builder
   * @since 0.1.0
   */
  public static Builder<VotingStats> newBuilder() {
    return Builder.build(VotingStats::new);
  }

  /**
   * Returns the voting's id
   *
   * @return the voting's id
   * @since 0.1.0
   */
  public UUID getId() {
    return id;
  }

  /**
   * Sets the voting id
   *
   * @param id the voting id
   * @since 0.1.0
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * Return vote's voting record
   *
   * @return an instance of type {@link Voting}
   * @since 0.1.0
   */
  public Voting getVoting() {
    return voting;
  }

  /**
   * Sets the voting this vote belongs to
   *
   * @param voting the {@link Voting} this vote belongs to
   * @since 0.1.0
   */
  public void setVoting(Voting voting) {
    this.voting = voting;
  }

  /**
   * Returns the moment the voting statistics was created
   *
   * @return the moment the voting was created
   * @since 0.1.0
   */
  public OffsetDateTime getCreatedAtDateTime() {
    return createdAtDateTime;
  }

  /**
   * Sets the moment the voting statistics was created
   *
   * @param createdAtDateTime when the voting was created
   * @since 0.1.0
   */
  public void setCreatedAtDateTime(OffsetDateTime createdAtDateTime) {
    this.createdAtDateTime = createdAtDateTime;
  }

  /**
   * Returns the voting's average
   *
   * @return the voting's average
   * @since 0.1.0
   */
  public Double getAverage() {
    return average;
  }

  /**
   * Sets the voting's average
   *
   * @param average the voting average
   * @since 0.1.0
   */
  public void setAverage(Double average) {
    this.average = average;
  }

  /**
   * Returns the voting's average
   *
   * @return the voting's average
   * @since 0.1.0
   */
  public Double getMovingAverage() {
    return movingAverage;
  }

  /**
   * Sets the voting's average
   *
   * @param movingAverage the voting average
   * @since 0.1.0
   */
  public void setMovingAverage(Double movingAverage) {
    this.movingAverage = movingAverage;
  }
}
