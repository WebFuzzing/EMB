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
package patio.infrastructure.graphql;

import graphql.GraphQLError;
import graphql.execution.DataFetcherResult;
import java.util.List;
import java.util.stream.Collectors;
import patio.common.domain.utils.Error;
import patio.common.domain.utils.Result;

/**
 * Responsible for converting domain classes {@link Result} and {@link Error} to instances of type
 * {@link DataFetcherResult}
 *
 * @since 0.1.0
 * @see Result
 * @see Error
 * @see DataFetcherResult
 */
public final class ResultUtils {

  private ResultUtils() {
    /* empty */
  }

  /**
   * Converts an instance of type {@link Result} to an instance of type {@link DataFetcherResult}.
   * It keeps classes from GraphQL framework from domain classes.
   *
   * @param result instance of {@link Result} to be rendered
   * @param <T> the success type
   * @return an instance of {@link DataFetcherResult}
   * @since 0.1.0
   * @see DataFetcherResult
   * @see Result
   */
  public static <T> DataFetcherResult<T> render(Result<T> result) {
    T success = result.getSuccess();
    List<GraphQLError> errors =
        result.getErrorList().stream()
            .map(error -> new I18nGraphQLError(error.getCode(), error.getMessage()))
            .collect(Collectors.toList());

    return new DataFetcherResult<>(success, errors);
  }

  /**
   * Converts an instance of domain class {@link Error} to an instance of GraphQL {@link
   * DataFetcherResult}
   *
   * @param error the source error
   * @return an instance of {@link DataFetcherResult} to be used by GraphQL
   * @since 0.1.0
   * @see DataFetcherResult
   * @see Error
   */
  public static DataFetcherResult render(Error error) {
    return render(Result.error(error));
  }
}
