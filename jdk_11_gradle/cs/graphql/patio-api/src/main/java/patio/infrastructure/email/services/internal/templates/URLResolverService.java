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
package patio.infrastructure.email.services.internal.templates;

import io.micronaut.context.annotation.Value;
import java.net.URI;
import java.text.MessageFormat;
import javax.inject.Singleton;

/**
 * Resolves all links relative to the host declared in applcation configuration
 *
 * @since 0.1.0
 */
@Singleton
public class URLResolverService {

  private final transient String host;

  /**
   * Initializes service with the declared application host
   *
   * @param host host all links are going to be resolved against
   * @since 0.1.0
   */
  public URLResolverService(@Value("${urlresolver.host}") String host) {
    this.host = host;
  }

  /**
   * Resolves link relative to declared host domain
   *
   * @param pattern url path pattern
   * @param values values to substitue in the path part of the url
   * @return complete url
   * @since 0.1.0
   */
  public String resolve(String pattern, Object... values) {
    String path = MessageFormat.format(pattern, values);

    return URI.create(host).resolve(path).toString();
  }
}
