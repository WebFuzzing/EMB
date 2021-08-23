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
import io.micronaut.context.annotation.Value;
import java.text.DateFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.inject.Singleton;
import javax.transaction.Transactional;
import patio.infrastructure.email.domain.Email;
import patio.infrastructure.email.services.EmailComposer;
import patio.infrastructure.email.services.internal.templates.JadeTemplateService;

/**
 * Business logic regarding the composition of an {@link Email}
 *
 * @since 0.1.0
 */
@Singleton
@Transactional
public class EmailComposerService implements EmailComposer {

  private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

  private final transient JadeTemplateService jadeTemplateService;
  private final transient MessageSource messageSource;
  private final transient Locale locale;

  /**
   * Different methods and utilities regarding the composition of an email
   *
   * @param jadeTemplateService to allow templates in the email composition
   * @param messageSource bundle to get i18n messages from
   * @param locale from configuration to internationalize dates and texts
   * @since 0.1.0
   */
  public EmailComposerService(
      JadeTemplateService jadeTemplateService,
      MessageSource messageSource,
      @Value("${locale}") Optional<String> locale) {
    this.jadeTemplateService = jadeTemplateService;
    this.messageSource = messageSource;
    this.locale = locale.map(Locale::new).orElse(DEFAULT_LOCALE);
  }

  /**
   * @param recipient the email recipient (jsmith@example.com)
   * @param subject the email subject
   * @param bodyTemplate the path where the .pug template is, to generate the html of the email body
   * @param bodyVariables a Map with the required variables to compose the bodyTemplate
   * @return the composed {@link Email}
   */
  @Override
  public Email composeEmail(
      String recipient, String subject, String bodyTemplate, Map<String, Object> bodyVariables) {
    String body = jadeTemplateService.render(bodyTemplate, bodyVariables);

    return Email.builder()
        .with(email -> email.setRecipient(recipient))
        .with(email -> email.setSubject(subject))
        .with(email -> email.setTextBody(body))
        .build();
  }

  /**
   * Recovers a message from 'messages.properties' files (for the configuration locale) with
   * variables to interpolate
   *
   * @param key the key in a 'messages.properties' file
   * @param variables the variables to interpolate for that entry key
   * @return the text message
   */
  @Override
  public String getMessage(String key, Map<String, Object> variables) {
    MessageSource.MessageContext context = MessageSource.MessageContext.of(this.locale, variables);

    return this.getInterpolatedMessage(key, context);
  }

  /**
   * Recovers a message from 'messages.properties' files (for the configuration locale) with NO
   * variables to interpolate
   *
   * @param key the key in a 'messages.properties' file
   * @return the text message
   */
  @Override
  public String getMessage(String key) {
    MessageSource.MessageContext context = MessageSource.MessageContext.of(this.locale);

    return this.getInterpolatedMessage(key, context);
  }

  @Override
  /**
   * Get a composed message representing the current date
   *
   * @return the textual date
   */
  public String getTodayMessage() {
    String dayOfTheWeek = this.getDayOfTheWeek();
    DateFormat formatter = DateFormat.getDateInstance(DateFormat.MEDIUM, this.locale);
    String today = formatter.format(new Date());

    return String.format("%s, %s", dayOfTheWeek, today);
  }

  private String getInterpolatedMessage(String key, MessageSource.MessageContext context) {
    String template = this.messageSource.getMessage(key, context).orElse("");

    return messageSource.interpolate(template, context);
  }

  private String getDayOfTheWeek() {
    LocalDate today = LocalDate.now();
    DayOfWeek dayOfWeek = today.getDayOfWeek();

    return dayOfWeek.getDisplayName(TextStyle.FULL, this.locale);
  }
}
