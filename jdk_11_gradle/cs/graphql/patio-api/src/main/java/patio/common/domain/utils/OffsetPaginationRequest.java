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
package patio.common.domain.utils;

/**
 * Provides information on how to paginate using offset pagination
 *
 * @see OffsetPaginationResult
 */
public class OffsetPaginationRequest {

  private final int offset;
  private final int max;

  /**
   * Inits a new {@link OffsetPaginationRequest} with offset and max
   *
   * @param offset the offset number
   * @param max the maximum number of results
   */
  public OffsetPaginationRequest(int offset, int max) {
    this.offset = offset;
    this.max = max;
  }

  /**
   * Returns the pagination offset
   *
   * @return the pagination offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns the maximum number of results
   *
   * @return the maximum number of results
   */
  public int getMax() {
    return max;
  }
}
