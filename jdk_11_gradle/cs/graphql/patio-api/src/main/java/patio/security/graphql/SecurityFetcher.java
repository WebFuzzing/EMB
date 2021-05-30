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

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import javax.inject.Singleton;
import patio.common.domain.utils.Result;
import patio.infrastructure.graphql.ResultUtils;
import patio.security.domain.Login;
import patio.security.services.SecurityService;
import patio.user.domain.User;

/**
 * Responsible for handling all security related GraphQL queries
 *
 * @since 0.1.0
 */
@Singleton
public class SecurityFetcher {

  private final transient SecurityService securityService;

  /**
   * Initializes fetcher with the {@link SecurityService} instance
   *
   * @param securityService service responsible to check security constraints
   * @since 0.1.0
   */
  public SecurityFetcher(SecurityService securityService) {
    this.securityService = securityService;
  }

  /**
   * Fetcher responsible to handle user's login
   *
   * @param environment GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Login> loginByCredentials(DataFetchingEnvironment environment) {
    LoginInput input = SecurityFetcherUtils.login(environment);
    Result<Login> login = securityService.loginByCredentials(input);

    return ResultUtils.render(login);
  }

  /**
   * Fetcher responsible to handle user's login using an Oauth2 authorization code
   *
   * @param environment GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Login> loginByOauth2(DataFetchingEnvironment environment) {
    String authorizationCode = environment.getArgument("authorizationCode");

    Result<Login> login = securityService.loginByOauth2(authorizationCode);

    return ResultUtils.render(login);
  }

  /**
   * Fetcher responsible to handle the user's login using an OTP (one-time password) code
   *
   * @param environment GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Login> loginByOtp(DataFetchingEnvironment environment) {
    String otpCode = environment.getArgument("otpCode");

    Result<Login> login = securityService.loginByOtp(otpCode);

    return ResultUtils.render(login);
  }

  /**
   * Fetcher responsible of changing the password for a {@link User}.
   *
   * @param env GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Boolean> changePassword(DataFetchingEnvironment env) {
    ChangePasswordInput input = SecurityFetcherUtils.changePassword(env);

    return ResultUtils.render(securityService.changePassword(input));
  }
}
