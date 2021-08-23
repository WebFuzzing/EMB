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

import static io.github.benas.randombeans.api.EnhancedRandom.random;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

import io.micronaut.context.MessageSource;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.infrastructure.email.services.internal.EmailComposerService;
import patio.infrastructure.email.services.internal.templates.JadeTemplateService;

/**
 * Tests {@link EmailComposerService}
 *
 * @since 0.1.0
 */
public class EmailComposerServiceTests {

  @Test
  void testComposeEmail() {
    // given: the email parts to be composed together
    var recipient = "jSmith@example.com";
    var subject = random(String.class);
    var bodyTemplate = random(String.class);
    var bodyVariables = new HashMap<String, Object>();

    // and: a mocked jadeTemplateService service
    var jadeTemplateService = Mockito.mock(JadeTemplateService.class);
    Mockito.when(jadeTemplateService.render(any(), any())).thenReturn(random(String.class));

    // and: a mocked messageSource service
    var messageSource = Mockito.mock(MessageSource.class);

    // and: an english locale
    var locale = Optional.of("eng");

    // when: invoking composeEmail()
    var emailComposerService = new EmailComposerService(jadeTemplateService, messageSource, locale);

    var emailResult =
        emailComposerService.composeEmail(recipient, subject, bodyTemplate, bodyVariables);

    // then: we should get an email with all of its parts
    assertThat("The recipient is the same", emailResult.getRecipient(), is(recipient));
    assertThat("The subject is the same", emailResult.getSubject(), is(subject));
    assertThat("The text body exists", emailResult.getTextBody(), is(not(isEmptyString())));

    // and: jadeTemplateService is called to render the text body
    verify(jadeTemplateService, atLeast(1)).render(bodyTemplate, bodyVariables);
  }

  @Test
  void testGetMessageNoInterpolation() {
    // given: a key and its text message
    var key = "sample.key";
    var retMessage = "sample text message";

    // mocked jadeTemplateService service
    var jadeTemplateService = Mockito.mock(JadeTemplateService.class);

    // and: a locale with its mocked context
    var locale = Optional.of("");
    var messageSource = Mockito.mock(MessageSource.class);
    Mockito.when(messageSource.interpolate(any(), any())).thenReturn(retMessage);
    Mockito.when(messageSource.getMessage(any(), any())).thenReturn(Optional.of(retMessage));

    // when: invoking getMessage() with no variables to interpolate
    var emailComposerService = new EmailComposerService(jadeTemplateService, messageSource, locale);

    // then: it should return the message
    assertThat("The retMessage is returned", emailComposerService.getMessage(key), is(retMessage));
  }

  @Test
  void testGetMessageInterpolation() {
    // given: a key and its text message
    var key = "sample.key";
    var retMessage = "Hello {name}";

    // and: some variables to interpolate
    var vars = new HashMap<String, Object>();
    vars.put("name", "Charles Xavier");

    // mocked jadeTemplateService service
    var jadeTemplateService = Mockito.mock(JadeTemplateService.class);

    // and: a locale with its mocked context
    var locale = Optional.of("");
    var messageSource = Mockito.mock(MessageSource.class);
    Mockito.when(messageSource.interpolate(any(), any())).thenReturn(retMessage);
    Mockito.when(messageSource.getMessage(any(), any())).thenReturn(Optional.of(retMessage));

    // when: invoking getMessage() with no variables to interpolate
    var emailComposerService = new EmailComposerService(jadeTemplateService, messageSource, locale);

    // then: it should return the message
    assertThat(
        "The retMessage is returned", emailComposerService.getMessage(key, vars), is(retMessage));
  }
}
