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

import java.util.List;

/**
 * Represents the result of an offset pagination
 *
 * @param <T> the wrapped type to be paginated over
 * @see OffsetPaginationRequest
 */
public class OffsetPaginationResult<T> {

  private final int totalCount;
  private final int offset;
  private final List<T> data;

  /**
   * Inits an {@link OffsetPaginationResult} with the potential total number of results from
   * database, the current offset, and the partial results
   *
   * @param totalCount total number of records in database
   * @param offset the current offset
   * @param data partial results
   */
  public OffsetPaginationResult(int totalCount, int offset, List<T> data) {
    this.totalCount = totalCount;
    this.offset = offset;
    this.data = data;
  }

  /**
   * Creates an empty result
   *
   * @param <T> the wrapped type to be paginated over
   * @return an empty result
   */
  public static <T> OffsetPaginationResult<T> empty() {
    return new OffsetPaginationResult<T>(0, 0, List.of());
  }

  /**
   * Returns total number of records in database
   *
   * @return total number of records in database
   */
  public int getTotalCount() {
    return totalCount;
  }

  /**
   * Returns current offset
   *
   * @return current offset
   */
  public int getOffset() {
    return offset;
  }

  /**
   * Returns partial results
   *
   * @return partial results
   */
  public List<T> getData() {
    return data;
  }
}
