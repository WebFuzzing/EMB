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
package patio.infrastructure.email.services;

import java.util.Map;
import patio.infrastructure.email.domain.Email;

/**
 * Different methods and utilities regarding the composition of emails
 *
 * @since 0.1.0
 */
public interface EmailComposer {

  /**
   * Compose an {@link Email} from its different parts
   *
   * @param recipient the email recipient (jsmith@example.com)
   * @param subject the email subject
   * @param bodyTemplate the path where the html body template is located (with the html email body)
   * @param bodyVariables the Map of variables the bodyTemplate requires to be interpolated with
   * @return the composed {@link Email}
   */
  Email composeEmail(
      String recipient, String subject, String bodyTemplate, Map<String, Object> bodyVariables);

  /**
   * Get a text message from a messages.properties file
   *
   * @param key the key to which recover the text from
   * @param variables the variables in the text that should be interpolated
   * @return the text message
   */
  String getMessage(String key, Map<String, Object> variables);

  /**
   * Get a plain text message, no variables to interpolate, from a messages.properties file
   *
   * @param key the key to which recover the text from
   * @return the text message
   */
  String getMessage(String key);

  /**
   * Get a composed message representing the current date
   *
   * @return the textual date
   */
  String getTodayMessage();
}
