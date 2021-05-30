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
package patio.security.services.internal;

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.infrastructure.utils.ErrorConstants;
import patio.security.graphql.ChangePasswordInput;
import patio.security.graphql.LoginInput;
import patio.security.services.CryptoService;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/**
 * Tests {@link patio.security.services.internal.DefaultSecurityService}
 *
 * @since 0.1.0
 */
public class DefaultSecurityServiceTests {

  @Test
  void findUserByTokenWithGoodToken() {
    // given: mocked calls
    var userEmail = "user@email.com";
    var cryptoService = Mockito.mock(CryptoService.class);
    var decodedJWT = Mockito.mock(DecodedJWT.class);

    var claim = Mockito.mock(Claim.class);
    Mockito.when(claim.asString()).thenReturn("");
    Mockito.when(decodedJWT.getClaim("name")).thenReturn(claim);
    Mockito.when(decodedJWT.getClaim("email")).thenReturn(claim);

    Mockito.when(decodedJWT.getSubject()).thenReturn(null);
    Mockito.when(cryptoService.verifyToken(any())).thenReturn(Optional.of(decodedJWT));

    var userRepository = Mockito.mock(UserRepository.class);
    var providedUser = Optional.of(random(User.class));
    Mockito.when(userRepository.findByEmailOrCreate(any())).thenReturn(providedUser);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing security service with a good token
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var user = securityService.resolveUser("good_token");

    // then: we should build the information of the matching user
    assertEquals(providedUser.get().getName(), user.get().getName());
  }

  @Test
  void findUserByTokenWithWrongToken() {
    // given: mocked calls
    var userEmail = "user@email.com";
    var cryptoService = Mockito.mock(CryptoService.class);
    var decodedJWT = Mockito.mock(DecodedJWT.class);
    var claim = Mockito.mock(Claim.class);
    Mockito.when(claim.asString()).thenReturn("");
    Mockito.when(decodedJWT.getClaim("name")).thenReturn(claim);
    Mockito.when(decodedJWT.getClaim("email")).thenReturn(claim);
    Mockito.when(cryptoService.verifyToken(any())).thenReturn(Optional.of(decodedJWT));

    var userRepository = Mockito.mock(UserRepository.class);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing security service with a wrong token
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var user = securityService.resolveUser("good_token");

    // then: we should build NO user
    assertFalse(user.isPresent());
  }

  @Test
  void testLoginWithGoodCredentials() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);
    var plainPassword = "password";

    // and: a repository returning a specific user
    var userRepository = Mockito.mock(UserRepository.class);
    var storedUser = Optional.of(random(User.class));
    storedUser.get().setPassword(cryptoService.hash(plainPassword));
    Mockito.when(userRepository.findByEmail(any())).thenReturn(storedUser);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing the security service with good credentials
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var result =
        securityService.loginByCredentials(
            new LoginInput(storedUser.get().getEmail(), plainPassword));

    // then: we should build a token that matches the user stored in database
    var resultUser = result.getSuccess().getUser();
    var resultToken = result.getSuccess().getTokens().getAuthenticationToken();
    var resultEmail = cryptoService.verifyToken(resultToken).get();

    assertNotNull(resultUser);
    assertNotNull(result);
    assertEquals(resultEmail.getSubject(), storedUser.get().getEmail());
  }

  @Test
  void testLoginWithBadCredentials() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);

    // and: a repository returning a specific user
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing the security service with good credentials
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);

    var loginInput = random(LoginInput.class);
    var result = securityService.loginByCredentials(loginInput);

    // then: we should build an error because of bad credentials
    var errors = result.getErrorList();
    var badCredentialsError = errors.get(0);

    assertNotNull(errors);
    assertEquals(errors.size(), 1);
    assertEquals(badCredentialsError.getCode(), ErrorConstants.BAD_CREDENTIALS.getCode());
    assertEquals(badCredentialsError.getMessage(), ErrorConstants.BAD_CREDENTIALS.getMessage());
  }

  @Test
  void testLoginWithValidOtp() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);
    var plainPassword = "password";
    var otp = "$2a$10$L0gD4";

    // and: a user with an OTP code and password
    var user =
        User.builder()
            .with(user1 -> user1.setOtp(otp))
            .with(user1 -> user1.setPassword(cryptoService.hash(plainPassword)))
            .build();
    var storedUser = Optional.of(user);

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByOtp(otp)).thenReturn(storedUser);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing the security service with the user's otp
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var result = securityService.loginByOtp(storedUser.get().getOtp());

    // then: we should build a token that matches the user stored in database
    var resultUser = result.getSuccess().getUser();
    var resultToken = result.getSuccess().getTokens().getAuthenticationToken();
    var resultEmail = cryptoService.verifyToken(resultToken).get();

    assertNotNull(resultUser);
    assertNotNull(result);
    assertEquals(resultEmail.getSubject(), storedUser.get().getEmail());
  }

  @Test
  void testLoginWithInvalidOtp() {
    // given: a security configuration
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);
    var plainPassword = "password";
    var otp = "$2a$10$L0gD4";

    // and: another otp
    var anotherOtp = "not the same otp";

    // and: a user with an OTP code and password
    var user =
        User.builder()
            .with(user1 -> user1.setOtp(otp))
            .with(user1 -> user1.setPassword(cryptoService.hash(plainPassword)))
            .build();
    var storedUser = Optional.of(user);

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByOtp(otp)).thenReturn(storedUser);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing the security service with the user's otp
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var result = securityService.loginByOtp(anotherOtp);

    // then: we should build an error because of bad credentials
    var errors = result.getErrorList();
    var badCredentialsError = errors.get(0);

    assertNotNull(errors);
    assertEquals(errors.size(), 1);
    assertEquals(badCredentialsError.getCode(), ErrorConstants.BAD_CREDENTIALS.getCode());
    assertEquals(badCredentialsError.getMessage(), ErrorConstants.BAD_CREDENTIALS.getMessage());
  }

  @Test
  void testValidPasswordChange() {
    // given: a mocked crypto security service
    var cryptoService = Mockito.mock(Auth0CryptoService.class);

    // and: a user who wants to change its previous password
    var oldPassword = "old password";
    var user =
        User.builder().with(user1 -> user1.setPassword(cryptoService.hash(oldPassword))).build();
    var storedUser = Optional.of(user);

    // and: the new intended password
    var newPassword = "new password";

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByOtp("otpCode")).thenReturn(storedUser);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing the security service to change her password
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var result = securityService.changePassword(new ChangePasswordInput("otpCode", newPassword));

    // then: the result is correct
    assertEquals(result.isSuccess(), true);

    // and: the new encrypted password is stored in database
    verify(cryptoService, times(1)).hash(newPassword);

    // and: otp users fields are cleared from database and persisted
    assertEquals(user.getOtp(), "");
    assertEquals(user.getOtpCreationDateTime(), null);
    verify(userRepository, times(2)).save(user);
  }

  @Test
  void testInvalidPasswordChangeIsTheSame() {
    // given: a crypto security service
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);

    // and: a user who wants to change its previous password
    var oldPassword = "old password";
    var user =
        User.builder().with(user1 -> user1.setPassword(cryptoService.hash(oldPassword))).build();
    var storedUser = Optional.of(user);

    // and: the same intended password
    var newPassword = oldPassword;

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByOtp("otpCode")).thenReturn(storedUser);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing the security service to change her password
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var result = securityService.changePassword(new ChangePasswordInput("otpCode", newPassword));

    // then: an error is returned because of the same password
    assertEquals(result.hasErrors(), true);

    var errors = result.getErrorList();
    var samePasswordError = errors.get(0);

    assertNotNull(errors);
    assertEquals(errors.size(), 1);
    assertEquals(samePasswordError.getCode(), ErrorConstants.SAME_PASSWORD.getCode());
  }

  @Test
  void testInvalidPasswordChangeIsBlank() {
    // given: a crypto security service
    var configuration = new SecurityConfiguration("issuer", 1, Algorithm.HMAC256("secret"));
    var cryptoService = new Auth0CryptoService(configuration);

    // and: a user who wants to change its previous password
    var oldPassword = "old password";
    var user =
        User.builder()
            .with(u -> u.setPassword(cryptoService.hash(oldPassword)))
            .with(u -> u.setOtp(random(String.class)))
            .with(u -> u.setOtpCreationDateTime(OffsetDateTime.now()))
            .build();
    var storedUser = Optional.of(user);

    // and: the new intended password left blank
    var newPassword = "";

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByOtp("otpCode")).thenReturn(storedUser);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    // and: an otp checker
    var otpExpiredForUser = Mockito.mock(OtpExpiredForUser.class);

    // when: executing the security service to change her password
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var result = securityService.changePassword(new ChangePasswordInput("otpCode", newPassword));

    // then: an error is returned because of the same password
    assertEquals(result.hasErrors(), true);

    var errors = result.getErrorList();
    var samePasswordError = errors.get(0);

    assertNotNull(errors);
    assertEquals(errors.size(), 1);
    assertEquals(samePasswordError.getCode(), ErrorConstants.BLANK_PASSWORD.getCode());
  }

  @Test
  void testInvalidPasswordChangeOtpExpired() {
    // given: a mocked crypto security service
    var cryptoService = Mockito.mock(Auth0CryptoService.class);

    // and: a user who wants to change its previous password
    var oldPassword = "old password";
    var user =
        User.builder().with(user1 -> user1.setPassword(cryptoService.hash(oldPassword))).build();
    var storedUser = Optional.of(user);

    // and: the new intended password
    var newPassword = "new password";

    // and: a mocked user repository
    var userRepository = Mockito.mock(UserRepository.class);
    Mockito.when(userRepository.findByOtp("otpCode")).thenReturn(storedUser);
    Mockito.when(userRepository.save(user)).thenReturn(user);

    // and: an otp checker that will always fails (expiration time of 0 minutes)
    var otpExpiredForUser = new OtpExpiredForUser(0);

    // when: executing the security service to change her password
    var securityService =
        new DefaultSecurityService(cryptoService, null, null, userRepository, otpExpiredForUser);
    var result = securityService.changePassword(new ChangePasswordInput("otpCode", newPassword));

    // then: an error is returned because of the same password
    assertEquals(result.hasErrors(), true);

    var errors = result.getErrorList();
    var samePasswordError = errors.get(0);

    assertNotNull(errors);
    assertEquals(errors.size(), 1);
    assertEquals(samePasswordError.getCode(), ErrorConstants.OTP_EXPIRED_FOR_USER.getCode());
  }
}
