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

import graphql.ErrorType;
import graphql.GraphQLError;
import graphql.language.SourceLocation;
import java.util.List;
import java.util.Map;

/**
 * Custom {@link GraphQLError} that only shows two fields code and message
 *
 * @since 0.1.0
 * @see GraphQLError
 */
public class I18nGraphQLError implements GraphQLError {

  private static final long serialVersionUID = 1L;
  private final String code;
  private final String message;
  /**
   * Initializes the error with its code and message
   *
   * @param code i18n code
   * @param message developer friendly message
   * @since 0.1.0
   */
  public I18nGraphQLError(String code, String message) {
    this.code = code;
    this.message = message;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return Map.of("code", this.getCode());
  }

  /**
   * Returns the error code
   *
   * @return the error code
   * @since 0.1.0
   */
  public String getCode() {
    return this.code;
  }

  @Override
  public String getMessage() {
    return this.message;
  }

  @Override
  public List<SourceLocation> getLocations() {
    return null;
  }

  @Override
  public ErrorType getErrorType() {
    return ErrorType.OperationNotSupported;
  }
}
