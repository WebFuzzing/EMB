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

import static java.util.ResourceBundle.getBundle;

import io.micronaut.context.annotation.Value;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Loads all mail related messages in the locale configured in application configuration file
 *
 * @since 0.1.0
 */
@Singleton
public class MailResourceBundleFactory implements Provider<ResourceBundle> {

  private final transient Optional<Locale> locale;

  /**
   * Loads all messages of the chosen locale
   *
   * @param locale optional locale required to load the messages
   * @since 0.1.0
   */
  public MailResourceBundleFactory(@Value("${locale}") Optional<Locale> locale) {
    this.locale = locale;
  }

  @Override
  public ResourceBundle get() {
    return this.locale.map(locale -> getBundle("messages", locale)).orElse(getBundle("messages"));
  }
}
