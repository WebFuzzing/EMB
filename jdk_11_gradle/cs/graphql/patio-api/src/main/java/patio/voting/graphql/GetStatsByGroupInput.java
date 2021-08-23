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
 * Class containing the input to recover the statistics given a group
 *
 * @since 0.1.0
 */
public class GetStatsByGroupInput {

  private UUID groupId;

  /**
   * Creates a new builder to create a new instance of type {@link GetStatsByGroupInput}
   *
   * @return an instance of the builder to build instances of type GetStatsByGroupInput
   * @since 0.1.0
   */
  public static Builder<GetStatsByGroupInput> newBuilder() {
    return Builder.build(GetStatsByGroupInput::new);
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
   * Sets the group id
   *
   * @param groupId of type {@link UUID} to get its stats
   */
  public void setGroupId(UUID groupId) {
    this.groupId = groupId;
  }
}
