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

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.junit.jupiter.api.Test;

public class JadeTemplateServiceTests {

  @Test
  void testRenderingTemplate() {
    // given: a configuration data
    var configuration = new JadeConfigurationFactory("UTF-8", false).get();

    // and: a template service
    var templateService = new JadeTemplateService(configuration);

    // and: provided data
    var data = Map.<String, Object>of("name", "john", "age", 22);

    // when: rendering the template with the provided data
    String result = templateService.render("templates/example.pug", data);

    // then: the result should contain provided data
    assertTrue(result.contains("john"), "rendered template should contain provided data");

    // and:
    assertTrue(result.contains("22"), "rendered template should contain provided data");
  }

  @Test
  void testFailingWhenTemplateNotFound() {
    // given: a configuration data
    var configuration = new JadeConfigurationFactory("UTF-8", false).get();

    // and: a template service
    var templateService = new JadeTemplateService(configuration);

    // when: rendering and unknown template
    var emptyResult = templateService.render("templates/unknown.pig", Map.of());

    // then: the result will be empty
    assertEquals("", emptyResult);
  }
}
