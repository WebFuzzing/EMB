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
package patio.security.services;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Optional;
import patio.security.domain.Tokens;
import patio.user.domain.User;

/**
 * Service responsible for creating and validating JWT tokens
 *
 * @since 0.1.0
 */
public interface CryptoService {

  /**
   * Creates a new token from the {@link User} information
   *
   * @param user user to create the token from
   * @return a valid pair of tokens
   * @since 0.1.0
   */
  Tokens createTokens(User user);

  /**
   * Verifies that the provided token is valid
   *
   * @param token the token to validate
   * @return the token subject if the token is valid, is empty otherwise
   * @since 0.1.0
   */
  Optional<DecodedJWT> verifyToken(String token);

  /**
   * Decodes without verifying anything the token passed as argument
   *
   * @param token token to decode
   * @return an instance of {@link DecodedJWT}
   * @since 0.1.0
   */
  Optional<DecodedJWT> decode(String token);

  /**
   * Hashes a given text
   *
   * @param text the text to hash
   * @return the hashed text
   * @since 0.1.0
   */
  String hash(String text);

  /**
   * Verifies that the plain text provided matches the hashed version
   *
   * @param plain plain text
   * @param hashed hashed version of the plain text
   * @return true if plain text and hashed password are considered equal
   * @since 0.1.0
   */
  boolean verifyWithHash(String plain, String hashed);
}
