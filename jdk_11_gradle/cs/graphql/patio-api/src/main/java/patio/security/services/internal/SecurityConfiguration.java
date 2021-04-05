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

import com.auth0.jwt.algorithms.Algorithm;
import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;
import patio.security.services.SecurityService;

/**
 * Gathers all information required to initialize a {@link SecurityService} instance
 *
 * @since 0.1.0
 */
@Singleton
public class SecurityConfiguration {

  private final String issuer;
  private final Algorithm algorithm;
  private final int daysToExpire;

  /**
   * Initializes security information
   *
   * @param issuer the tokens issuer
   * @param daysToExpire days before the token is out of date
   * @param algorithm the type of algorithm used to sign jwt
   * @since 0.1.0
   */
  public SecurityConfiguration(
      @Value("${crypto.jwt.issuer:none}") String issuer,
      @Value("${crypto.jwt.days:none}") int daysToExpire,
      Algorithm algorithm) {
    this.issuer = issuer;
    this.algorithm = algorithm;
    this.daysToExpire = daysToExpire;
  }

  /**
   * The issuer used for signing the tokens. It's the application's name
   *
   * @return the tokens issuer
   * @since 0.1.0
   */
  /* default */ String getIssuer() {
    return this.issuer;
  }

  /**
   * The {@link Algorithm} used to sign the token
   *
   * @return an instance of the algorithm used to sign the token
   * @since 0.1.0
   */
  /* default */ Algorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * Days added to the token generation date before the token becomes invalid
   *
   * @return days the token will be valid
   * @since 0.1.0
   */
  /* default */ int getDaysToExpire() {
    return daysToExpire;
  }
}
