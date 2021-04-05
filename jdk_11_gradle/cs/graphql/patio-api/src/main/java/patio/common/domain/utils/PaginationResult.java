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

import io.micronaut.data.model.Page;
import java.util.List;

/**
 * Represents a paginated result. It tries to be as minimalistic as possible.
 *
 * @param <T> the wrapped type to be paginated over
 */
public final class PaginationResult<T> {

  private List<T> data;
  private long totalCount;
  private long page;
  private long lastPage;

  /**
   * Creates a new instance of {@link PaginationResult} from a Page
   *
   * @param page the {@link Page} with the results
   * @param <A> the type of the pagination result
   * @return an instance of type {@link PaginationResult}
   */
  public static <A> PaginationResult<A> from(Page page) {
    return PaginationResult.newBuilder()
        .with(pr -> pr.setData(page.getContent()))
        .with(pr -> pr.setTotalCount(page.getTotalSize()))
        .with(pr -> pr.setPage(page.getPageNumber()))
        .with(pr -> pr.setLastPage(page.getTotalPages() - 1))
        .build();
  }

  /**
   * Creates a new builder to create a new instance of type {@link PaginationResult}
   *
   * @return an instance of PaginationResult builder
   * @since 0.1.0
   */
  public static Builder<PaginationResult> newBuilder() {
    return Builder.build(PaginationResult::new);
  }

  /**
   * Returns a partial list of the whole result set
   *
   * @return a partial list of the whole result set
   */
  public List<T> getData() {
    return data;
  }

  /**
   * Defines a partial list of the whole result set
   *
   * @param data a list with the page content
   */
  public void setData(List<T> data) {
    this.data = data;
  }

  /**
   * Returns the current page number
   *
   * @return the total number in the result set
   */
  public long getPage() {
    return page;
  }

  /**
   * Defines the current page number
   *
   * @param page the number of the current page
   */
  public void setPage(long page) {
    this.page = page;
  }

  /**
   * Returns the last page number which has elements
   *
   * @return the total number in the result set
   */
  public long getLastPage() {
    return lastPage;
  }

  /**
   * Defines the last page number which has elements
   *
   * @param lastPage the last page number with content
   */
  public void setLastPage(long lastPage) {
    this.lastPage = lastPage;
  }

  /**
   * Returns the total number of elements that are being paginated
   *
   * @return the total number of elements in the result set
   */
  public long getTotalCount() {
    return totalCount;
  }

  /**
   * Defines the total number of elements that are being paginated
   *
   * @param totalCount te total size of the elements that are paginated
   */
  public void setTotalCount(long totalCount) {
    this.totalCount = totalCount;
  }
}
