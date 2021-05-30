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
 * Holds the information on how to paginate a result
 *
 * @see PaginationResult
 */
public final class PaginationRequest {

  private final int max;
  private final int page;

  private PaginationRequest(int max, int page) {
    this.max = max;
    this.page = page;
  }

  /**
   * Creates a new instance of {@link PaginationRequest} from an max limit and the page where the
   * pagination begins
   *
   * @param max the maximum number of elements to show
   * @param page where to begin to count the elements to show
   * @return an instance of {@link PaginationRequest}
   */
  public static PaginationRequest from(int max, int page) {
    return new PaginationRequest(max, page);
  }

  /**
   * Returns the maximum number of elements to show
   *
   * @return the maximum number of elements to show
   */
  public int getMax() {
    return max;
  }

  /**
   * Returns where to start counting the elements to show
   *
   * @return where to start counting the elements to show
   */
  public int getPage() {
    return page;
  }
}
