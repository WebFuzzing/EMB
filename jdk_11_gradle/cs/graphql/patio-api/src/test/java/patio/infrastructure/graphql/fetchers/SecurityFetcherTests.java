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
package patio.infrastructure.graphql.fetchers;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;

import graphql.schema.DataFetchingEnvironment;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.common.domain.utils.Result;
import patio.infrastructure.graphql.I18nGraphQLError;
import patio.infrastructure.graphql.fetchers.utils.FetcherTestUtils;
import patio.security.domain.Login;
import patio.security.domain.Tokens;
import patio.security.graphql.LoginInput;
import patio.security.graphql.SecurityFetcher;
import patio.security.services.SecurityService;
import patio.user.domain.User;

/**
 * Tests {@link SecurityFetcher}
 *
 * @since 0.1.0
 */
public class SecurityFetcherTests {

  @Test
  void testLoginByCredentialsSuccess() {
    // given: mocking service SUCCESSFUL call
    var securityService = Mockito.mock(SecurityService.class);
    Mockito.when(securityService.loginByCredentials(any(LoginInput.class)))
        .thenReturn(Result.result(random(Login.class)));

    // when: the fetcher is invoked for login query
    var securityFetcher = new SecurityFetcher(securityService);
    var fetchingEnvironment = Mockito.mock(DataFetchingEnvironment.class);
    var result = securityFetcher.loginByCredentials(fetchingEnvironment);

    // then: there should be no errors
    assertTrue(result.getErrors().isEmpty());

    // and: a login payload should be returned
    assertNotNull(result.getData());
  }

  @Test
  void testLoginByCredentialsFailure() {
    // given: failure code and message
    var code = "error.code";
    var message = "friendly message";

    // and: mocking service FAILING call
    var securityService = Mockito.mock(SecurityService.class);
    Mockito.when(securityService.loginByCredentials(any(LoginInput.class)))
        .thenReturn(Result.error(code, message));

    // when: the fetcher is invoked for login query
    var securityFetcher = new SecurityFetcher(securityService);
    var fetchingEnvironment = Mockito.mock(DataFetchingEnvironment.class);
    var result = securityFetcher.loginByCredentials(fetchingEnvironment);

    // then: there should be errors
    assertFalse(result.getErrors().isEmpty());

    I18nGraphQLError error = (I18nGraphQLError) result.getErrors().get(0);
    assertEquals(code, error.getCode());
    assertEquals(message, error.getMessage());

    // and: a login payload should be missing
    assertNull(result.getData());
  }

  @Test
  void testLoginByOtpSuccess() {
    // given: a user with an otp
    var otp = random(String.class);
    var user = User.builder().with(u -> u.setOtp(otp)).build();

    // and: mocking the service with a SUCCESSFUL Login response
    var securityService = Mockito.mock(SecurityService.class);
    var login = new Login(random(Tokens.class), user);
    Mockito.when(securityService.loginByOtp(otp)).thenReturn(Result.result(login));

    // and: a correct parameter from the environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(null, Map.of("otpCode", otp));

    // when: the fetcher is invoked for login by otp query
    var securityFetcher = new SecurityFetcher(securityService);
    var result = securityFetcher.loginByOtp(mockedEnvironment);

    // then: there should be no errors
    assertTrue(result.getErrors().isEmpty());

    // and: a login payload should be returned
    assertNotNull(result.getData());
  }

  @Test
  void testLoginByOtpFailure() {
    // given: any otp code
    var otpCode = random(String.class);

    // and: a failure code and message
    var code = "error.code";
    var message = "friendly message";

    // and: mocking the service to FAILURE with an error
    var securityService = Mockito.mock(SecurityService.class);
    Mockito.when(securityService.loginByOtp(otpCode)).thenReturn(Result.error(code, message));

    // and: a correct parameter received from the environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(null, Map.of("otpCode", otpCode));

    // when: the fetcher is invoked for login query
    var securityFetcher = new SecurityFetcher(securityService);
    var result = securityFetcher.loginByOtp(mockedEnvironment);

    // then: there should be errors
    assertFalse(result.getErrors().isEmpty());

    I18nGraphQLError error = (I18nGraphQLError) result.getErrors().get(0);
    assertEquals(code, error.getCode());
    assertEquals(message, error.getMessage());

    // and: a login payload should be missing
    assertNull(result.getData());
  }

  @Test
  void testChangingPasswordSuccess() {
    // given: a user
    var user = random(User.class);

    // and: mocking the service with a SUCCESSFUL response
    var securityService = Mockito.mock(SecurityService.class);
    Mockito.when(securityService.changePassword(any())).thenReturn(Result.result(true));

    // and: a correct parameter from the environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(user, Map.of("password", random(String.class)));

    // when: the fetcher is invoked for changing the password by otp mutation
    var securityFetcher = new SecurityFetcher(securityService);
    var result = securityFetcher.changePassword(mockedEnvironment);

    // then: there should be no errors
    assertTrue(result.getErrors().isEmpty());

    // and: a login payload should be returned
    assertNotNull(result.getData());
  }

  @Test
  void testChangingPasswordFailure() {
    // given: a user
    var user = random(User.class);

    // and: a failure code and message
    var code = "error.code";
    var message = "friendly message";

    // and: mocking the service with a FAILURE response
    var securityService = Mockito.mock(SecurityService.class);
    Mockito.when(securityService.changePassword(any())).thenReturn(Result.error(code, message));

    // and: a correct parameter from the environment
    var mockedEnvironment =
        FetcherTestUtils.generateMockedEnvironment(user, Map.of("password", random(String.class)));

    // when: the fetcher is invoked for changing the password by otp mutation
    var securityFetcher = new SecurityFetcher(securityService);
    var result = securityFetcher.changePassword(mockedEnvironment);

    // then: there should be errors
    assertFalse(result.getErrors().isEmpty());

    I18nGraphQLError error = (I18nGraphQLError) result.getErrors().get(0);
    assertEquals(code, error.getCode());
    assertEquals(message, error.getMessage());

    // and: a login payload should be missing
    assertNull(result.getData());
  }
}
