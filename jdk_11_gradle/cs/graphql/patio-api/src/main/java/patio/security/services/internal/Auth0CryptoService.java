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

import static patio.infrastructure.utils.FunctionsUtils.safely;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.time.OffsetDateTime;
import java.util.Date;
import java.util.Optional;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCrypt;
import patio.security.domain.Tokens;
import patio.security.services.CryptoService;
import patio.user.domain.User;

/**
 * Default implementation of the {@link CryptoService}
 *
 * @since 0.1.0
 */
@Singleton
public class Auth0CryptoService implements CryptoService {

  private final transient SecurityConfiguration configuration;
  private static final Logger LOGGER = LoggerFactory.getLogger(Auth0CryptoService.class);

  /**
   * Initializes the service with a specific {@link Algorithm} instance
   *
   * @param configuration configuration values to init security service
   * @since 0.1.0
   */
  public Auth0CryptoService(SecurityConfiguration configuration) {
    this.configuration = configuration;
  }

  @Override
  public Tokens createTokens(User user) {
    var issuer = configuration.getIssuer();
    var algorithm = configuration.getAlgorithm();
    var daysToExpire = configuration.getDaysToExpire();

    var now = OffsetDateTime.now();
    var currentDate = Date.from(now.toInstant());
    var expirationDate = Date.from(now.plusDays(daysToExpire).toInstant());

    String authenticationToken =
        JWT.create()
            .withIssuer(issuer)
            .withSubject(user.getEmail())
            .withClaim("name", user.getName())
            .withNotBefore(currentDate)
            .withIssuedAt(currentDate)
            .withExpiresAt(expirationDate)
            .sign(algorithm);

    return Tokens.builder().with(t -> t.setAuthenticationToken(authenticationToken)).build();
  }

  @Override
  public Optional<DecodedJWT> verifyToken(String token) {
    var algorithm = configuration.getAlgorithm();
    var issuer = configuration.getIssuer();
    var verifier = JWT.require(algorithm).withIssuer(issuer).build();

    return Optional.ofNullable(token)
        .flatMap(safely(verifier::verify, (th) -> LOGGER.error(th.getMessage())));
  }

  @Override
  public Optional<DecodedJWT> decode(String token) {
    return Optional.ofNullable(token).map(JWT::decode);
  }

  @Override
  public String hash(String text) {
    return BCrypt.hashpw(text, BCrypt.gensalt());
  }

  @Override
  public boolean verifyWithHash(String plain, String hashed) {
    return BCrypt.checkpw(plain, hashed);
  }
}
