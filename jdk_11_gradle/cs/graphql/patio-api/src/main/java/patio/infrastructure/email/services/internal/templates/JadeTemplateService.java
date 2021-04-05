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

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.JadeTemplate;
import java.util.Map;
import java.util.Optional;
import javax.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service resolves and renders a given Jade template
 *
 * @since 0.1.0
 */
@Singleton
public class JadeTemplateService {

  private static final Logger LOG = LoggerFactory.getLogger(JadeTemplateService.class);
  private final transient JadeConfiguration configuration;

  /**
   * Default constructor receiving the {@link JadeConfiguration} containing information about
   * templates location, templates cache.
   *
   * @param configuration instance of {@link JadeConfiguration}
   * @since 0.1.0
   */
  public JadeTemplateService(JadeConfiguration configuration) {
    this.configuration = configuration;
  }

  /**
   * Renders a given template by its name and passes the required template data
   *
   * @param templateName name of the template we'd like to use
   * @param data model data passed to the template
   * @return the result of the template processing
   * @since 0.1.0
   */
  public String render(String templateName, Map<String, Object> data) {
    return Optional.ofNullable(templateName)
        .flatMap(this::getTemplateSafely)
        .map(template -> configuration.renderTemplate(template, data))
        .orElse("");
  }

  @SuppressWarnings({"PMD.OnlyOneReturn", "PMD.AvoidCatchingThrowable"})
  private Optional<JadeTemplate> getTemplateSafely(String name) {
    try {
      return Optional.ofNullable(configuration.getTemplate(name));
    } catch (Throwable throwable) {
      LOG.error(String.format("Problem loading template: %s", throwable.getMessage()));
      return Optional.empty();
    }
  }
}
