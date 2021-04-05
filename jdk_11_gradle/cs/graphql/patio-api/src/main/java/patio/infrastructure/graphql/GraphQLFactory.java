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

import graphql.GraphQL;
import graphql.execution.instrumentation.dataloader.DataLoaderDispatcherInstrumentation;
import graphql.schema.GraphQLSchema;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import javax.inject.Singleton;
import patio.infrastructure.graphql.instrumentation.AuthenticationCheck;

/**
 * Maps the schema with the functions that are actually operating over the real data
 *
 * @since 0.1.0
 */
@Factory
public class GraphQLFactory {

  /**
   * Configures the GraphQL environment mapping fetchers with fields in the schema.
   *
   * @param schema the {@link GraphQLSchema}
   * @return an instance of {@link GraphQL}
   * @since 0.1.0
   */
  @Bean
  @Singleton
  public GraphQL graphQL(GraphQLSchema schema) {
    return GraphQL.newGraphQL(schema)
        .instrumentation(new AuthenticationCheck())
        .instrumentation(new DataLoaderDispatcherInstrumentation())
        .build();
  }
}
