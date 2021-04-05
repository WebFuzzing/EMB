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
package patio.security.graphql;

import graphql.schema.idl.TypeRuntimeWiring;
import java.util.function.UnaryOperator;
import javax.inject.Singleton;
import patio.infrastructure.graphql.MutationProvider;
import patio.infrastructure.graphql.QueryProvider;

/**
 * Contains all mapped fetchers for queries, and mutations for security related operations
 *
 * @see QueryProvider
 * @see MutationProvider
 */
@Singleton
public class SecurityProvider implements QueryProvider, MutationProvider {

  private final transient SecurityFetcher securityFetcher;
  private final transient ResetPasswordFetcher resetPasswordFetcher;

  /**
   * Initializes providers with its dependencies
   *
   * @param securityFetcher security related data fetchers
   * @param resetPasswordFetcher reset password fetcher
   */
  public SecurityProvider(
      SecurityFetcher securityFetcher, ResetPasswordFetcher resetPasswordFetcher) {
    this.securityFetcher = securityFetcher;
    this.resetPasswordFetcher = resetPasswordFetcher;
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getMutations() {
    return (builder) ->
        builder
            .dataFetcher("resetPassword", resetPasswordFetcher::resetPassword)
            .dataFetcher("changePassword", securityFetcher::changePassword);
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getQueries() {
    return (builder) ->
        builder
            .dataFetcher("login", securityFetcher::loginByCredentials)
            .dataFetcher("loginOauth2", securityFetcher::loginByOauth2)
            .dataFetcher("loginOtp", securityFetcher::loginByOtp);
  }
}
