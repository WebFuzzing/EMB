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
package patio.infrastructure.email.services.internal;

import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.context.i18n.ResourceBundleMessageSource;
import java.util.Locale;
import java.util.Optional;
import javax.inject.Singleton;

/** Handle the message template resolution and variables interpolation */
@Factory
public class MessagesFactory {

  /**
   * Initializes MessageSource with the locale taken from configuration
   *
   * @param locale language for {@link Locale}
   * @return The message source backed by the locale resource bundle {@link
   *     ResourceBundleMessageSource}
   */
  @Singleton
  @Bean
  /* default */ MessageSource messageSource(@Value("${locale}") Optional<String> locale) {
    Locale configLocale = locale.map(Locale::new).orElse(Locale.ENGLISH);
    return new ResourceBundleMessageSource("messages", configLocale);
  }
}
