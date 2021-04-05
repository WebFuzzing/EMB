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
import de.neuland.jade4j.template.ClasspathTemplateLoader;
import de.neuland.jade4j.template.TemplateLoader;
import io.micronaut.context.annotation.Value;
import javax.inject.Provider;
import javax.inject.Singleton;

/**
 * Configuration related to templating behavior
 *
 * @since 0.1.0
 */
@Singleton
public class JadeConfigurationFactory implements Provider<JadeConfiguration> {

  private final transient String encoding;
  private final transient boolean cached;

  /**
   * Default constructor
   *
   * @param encoding encoding type (UTF-8, ISO-...)
   * @param cached whether the templates are cached or not
   * @since 0.1.0
   */
  public JadeConfigurationFactory(
      @Value("${templates.encoding}") String encoding,
      @Value("${templates.cached}") boolean cached) {
    this.cached = cached;
    this.encoding = encoding;
  }

  @Override
  public JadeConfiguration get() {
    JadeConfiguration configuration = new JadeConfiguration();
    TemplateLoader templateLoader = new ClasspathTemplateLoader(this.encoding);

    configuration.setCaching(this.cached);
    configuration.setTemplateLoader(templateLoader);

    return configuration;
  }
}
