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

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;
import java.util.Optional;
import org.junit.jupiter.api.Test;

public class MailResourceBundleFactoryTests {

  @Test
  void resolveMessageSuccessfully() {
    // given: an instance of a resource bundle
    var factory = new MailResourceBundleFactory(Optional.of(Locale.ENGLISH));
    var bundle = factory.get();

    // when: getting an i18n string by its key
    var message = bundle.getString("voting.thanks");

    // then: we should get the expected i18n message
    assertEquals("Thanks!", message);
  }
}
