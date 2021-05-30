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

import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Optional;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import patio.common.domain.utils.Result;
import patio.infrastructure.utils.ErrorConstants;
import patio.security.domain.Login;
import patio.security.domain.Tokens;
import patio.security.graphql.ChangePasswordInput;
import patio.security.graphql.LoginInput;
import patio.security.services.CryptoService;
import patio.security.services.GoogleUserService;
import patio.security.services.OauthService;
import patio.security.services.SecurityService;
import patio.user.domain.User;
import patio.user.repositories.UserRepository;

/**
 * Service responsible to check the security constraints
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class DefaultSecurityService implements SecurityService {

  private static final Logger LOG = LoggerFactory.getLogger(DefaultSecurityService.class);

  private final transient CryptoService cryptoService;
  private final transient OauthService oauthService;
  private final transient UserRepository userRepository;
  private final transient GoogleUserService googleUserService;
  private final transient OtpExpiredForUser otpExpiredForUser;

  /**
   * Initializes security service with cryptographic service and user database access
   *
   * @param cryptoService service used to handle JWT tokens
   * @param googleUserService service to get user information from Google
   * @param oauthService service to interact with an oauth2 provider
   * @param userRepository service used to check user data constraints
   * @param otpExpiredForUser to validate an otp has not expired
   * @since 0.1.0
   */
  public DefaultSecurityService(
      CryptoService cryptoService,
      GoogleUserService googleUserService,
      OauthService oauthService,
      UserRepository userRepository,
      OtpExpiredForUser otpExpiredForUser) {
    this.cryptoService = cryptoService;
    this.googleUserService = googleUserService;
    this.oauthService = oauthService;
    this.userRepository = userRepository;
    this.otpExpiredForUser = otpExpiredForUser;
  }

  @Override
  public Optional<User> resolveUser(String token) {
    return cryptoService
        .verifyToken(token)
        .map(this::extractUserFrom)
        .flatMap(userRepository::findByEmailOrCreate);
  }

  private User extractUserFrom(DecodedJWT decodedJWT) {
    String name = decodedJWT.getClaim("name").asString();
    String email = decodedJWT.getSubject();

    return User.builder()
        .with(user -> user.setName(name))
        .with(user -> user.setEmail(email))
        .build();
  }

  @Override
  public Result<Login> loginByCredentials(LoginInput input) {
    Optional<User> user = userRepository.findByEmail(input.getEmail());

    return user.filter(
            user1 -> cryptoService.verifyWithHash(input.getPassword(), user1.getPassword()))
        .flatMap(this::getLoginFromUser)
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }

  @Override
  public Result<Login> loginByOauth2(String code) {
    return Optional.ofNullable(code)
        .flatMap(oauthService::getAccessToken)
        .flatMap(googleUserService::loadFromAccessToken)
        .flatMap(userRepository::findByEmailOrCreate)
        .flatMap(this::getLoginFromUser)
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }

  @Override
  public Result<Login> loginByOtp(String otpCode) {
    return Optional.ofNullable(otpCode)
        .flatMap(userRepository::findByOtp)
        .flatMap(this::clearOtpForUser)
        .flatMap(this::getLoginFromUser)
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }

  @Override
  public Result<Login> refresh(String refreshToken) {
    return cryptoService
        .verifyToken(refreshToken)
        .map(this::extractUserFrom)
        .flatMap(this::getLoginFromUser)
        .map(Result::result)
        .orElse(Result.error(ErrorConstants.BAD_CREDENTIALS));
  }

  @Override
  public Result<Boolean> changePassword(ChangePasswordInput input) {
    Optional<User> user =
        Optional.ofNullable(input.getOtpCode()).flatMap(userRepository::findByOtp);
    String newPassword = Optional.ofNullable(input.getPassword()).orElse("");

    PasswordIsBlank passwordIsBlank = new PasswordIsBlank();
    SamePassword samePassword = new SamePassword(cryptoService);

    user.ifPresent(
        (u) -> LOG.info(String.format("User %s is attempting to set a new password", u.getId())));

    return user.map(
            u -> {
              return Result.<Boolean>create()
                  .thenCheck(() -> this.otpExpiredForUser.check(user))
                  .thenCheck(() -> passwordIsBlank.check(newPassword))
                  .thenCheck(() -> samePassword.check(u, newPassword))
                  .then(() -> updatePasswordIfSuccess(u, input.getPassword()))
                  .sideEffect((success) -> this.clearOtpForUser(u));
            })
        .orElse(Result.result(true));
  }

  private Optional<Login> getLoginFromUser(User user) {
    Tokens tokens = cryptoService.createTokens(user);

    return Optional.of(new Login(tokens, user));
  }

  private Optional<User> clearOtpForUser(User user) {
    user.setOtp("");
    user.setOtpCreationDateTime(null);

    return Optional.of(userRepository.save(user));
  }

  private Boolean updatePasswordIfSuccess(User user, String password) {
    user.setPassword(cryptoService.hash(password));
    return Optional.of(user).map(userRepository::save).isPresent();
  }
}
