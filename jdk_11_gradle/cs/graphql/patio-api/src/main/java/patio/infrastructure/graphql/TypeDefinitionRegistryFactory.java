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
package patio.infrastructure.graphql;

import static java.nio.charset.StandardCharsets.UTF_8;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import io.micronaut.core.io.ResourceResolver;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.inject.Singleton;

/**
 * Factory to creates an instance of type {@link TypeDefinitionRegistry}
 *
 * @see TypeDefinitionRegistry
 */
@Factory
public class TypeDefinitionRegistryFactory {

  /**
   * Creates an instance of type {@link TypeDefinitionRegistry}
   *
   * @param path where is located the schema
   * @param resourceResolver required to know how to resolve the path
   * @return an instance of type {@link TypeDefinitionRegistry}
   */
  @Bean
  @Singleton
  public TypeDefinitionRegistry load(
      @Value("${graphql.schema}") String path, ResourceResolver resourceResolver) {
    var typeRegistry = new TypeDefinitionRegistry();
    var schemaParser = new SchemaParser();
    var schemaReader =
        resourceResolver
            .getResourceAsStream(path)
            .map(inputStream -> new InputStreamReader(inputStream, UTF_8))
            .map(BufferedReader::new);

    return schemaReader.map(schemaParser::parse).map(typeRegistry::merge).orElse(null);
  }
}
