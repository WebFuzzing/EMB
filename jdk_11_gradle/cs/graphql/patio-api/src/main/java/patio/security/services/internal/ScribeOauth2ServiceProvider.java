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

import com.github.scribejava.apis.GoogleApi20;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.oauth.OAuth20Service;
import io.micronaut.context.annotation.Value;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Creates an instance of {@link OAuth20Service} with its properties configured from the
 * application.yml values
 *
 * @since 0.1.0
 */
@Singleton
public class ScribeOauth2ServiceProvider implements Provider<OAuth20Service> {

  private final transient String apiKey;
  private final transient String apiSecret;
  private final transient String callback;

  /**
   * Creates a new provider setting the Oauth2 apiKey and apiSecret properties
   *
   * @param apiKey the Oauth2 api key
   * @param apiSecret the Oauth2 api secret
   * @param callback a valid url callback declared in oauth2 service provider
   * @since 0.1.0
   */
  public ScribeOauth2ServiceProvider(
      @Value("${oauth2.apikey:none}") String apiKey,
      @Value("${oauth2.apisecret:none}") String apiSecret,
      @Value("${oauth2.callback:none}") String callback) {
    this.apiKey = apiKey;
    this.apiSecret = apiSecret;
    this.callback = callback;
  }

  @Override
  public OAuth20Service get() {
    return new ServiceBuilder(this.apiKey)
        .apiSecret(this.apiSecret)
        .callback(this.callback)
        .build(GoogleApi20.instance());
  }
}
