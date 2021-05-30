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
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.inject.Singleton;

/**
 * Factory responsible for creating instances of {@link Algorithm}
 *
 * @since 0.1.0
 */
@Factory
public class AlgorithmFactory {

  /**
   * Provides a singleton instance of {@link Algorithm}
   *
   * @param secret the secret used to initiate the algorithm
   * @param algorithmType the type of algorithm (HS256 or RS256)
   * @return an instance of {@link Algorithm}
   * @throws NoSuchAlgorithmException if algorithm is different than HS256 or RS256
   * @throws InvalidKeySpecException if the key provided (when using RS256) is invalid
   * @since 0.1.0
   */
  @Singleton
  @SuppressWarnings("PMD.OnlyOneReturn")
  public Algorithm create(
      @Value("${crypto.jwt.secret:none}") String secret,
      @Value("${crypto.jwt.algorithm:none}") String algorithmType)
      throws NoSuchAlgorithmException, InvalidKeySpecException {

    switch (algorithmType) {
      case "RS256":
        return Algorithm.RSA256(getPublicKeyFromString(secret));
      case "HS256":
        return Algorithm.HMAC256(secret);
      default:
        throw new NoSuchAlgorithmException("Algorithm not supported");
    }
  }

  private static RSAPublicKey getPublicKeyFromString(String publicKeyContent)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    String cleanedKey =
        publicKeyContent
            .replaceAll("\\n", "")
            .replace("-----BEGIN PUBLIC KEY-----", "")
            .replace("-----END PUBLIC KEY-----", "");

    KeyFactory keyFactory = KeyFactory.getInstance("RSA");
    X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.getDecoder().decode(cleanedKey));

    return (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
  }
}
