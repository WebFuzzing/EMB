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

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.TypeDefinitionRegistry;
import io.micronaut.context.annotation.Bean;
import io.micronaut.context.annotation.Factory;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import javax.inject.Singleton;

/**
 * This factory creates the {@link GraphQLSchema} instance. In order to create the {@link
 * GraphQLSchema} it needs to load
 *
 * <ul>
 *   <li>The {@link TypeDefinitionRegistry} which is basically the AST of the GraphQL schema file
 *   <li>{@link QueryProvider} list: {@link DataFetcher} mappings for queries
 *   <li>{@link MutationProvider} list: {@link DataFetcher} mappings for mutations
 *   <li>{@link TypeProvider} list: instances providing field resolution for GraphQL types
 *   <li>{@link ScalarProvider} list: instances providing scalar implementations
 * </ul>
 */
@Factory
public class GraphQLSchemaFactory {

  private static final String SCHEMA_TYPE_QUERY = "Query";
  private static final String SCHEMA_TYPE_MUTATION = "Mutation";

  /**
   * Creates an instance of type {@link GraphQLSchema}
   *
   * @param registry an instance of type {@link TypeDefinitionRegistry}
   * @param queryProviders a list of {@link QueryProvider}
   * @param mutationProviders a list of {@link MutationProvider}
   * @param typeProviders a list of {@link TypeProvider}
   * @param scalarProviders a list of {@link ScalarProvider}
   * @return an instance of type {@link GraphQLSchema}
   */
  @Bean
  @Singleton
  public GraphQLSchema getSchema(
      TypeDefinitionRegistry registry,
      List<QueryProvider> queryProviders,
      List<MutationProvider> mutationProviders,
      List<TypeProvider> typeProviders,
      List<ScalarProvider> scalarProviders) {

    // Loading QUERIES and MUTATIONS
    var wiringBuilder =
        RuntimeWiring.newRuntimeWiring()
            .type(SCHEMA_TYPE_QUERY, process(queryProviders, QueryProvider::getQueries))
            .type(SCHEMA_TYPE_MUTATION, process(mutationProviders, MutationProvider::getMutations));

    // Loading custom TYPES and SCALARS
    var processTypes = process(typeProviders, TypeProvider::getTypes);
    var processScalars = process(scalarProviders, ScalarProvider::getScalars);
    var wiring = processTypes.andThen(processScalars).apply(wiringBuilder).build();

    // Building the Schema
    return new SchemaGenerator().makeExecutableSchema(registry, wiring);
  }

  private static <A, B> UnaryOperator<B> process(
      List<A> providers, Function<A, UnaryOperator<B>> mapper) {
    return providers.stream()
        .map(mapper)
        .filter(Objects::nonNull)
        .reduce((left, right) -> (b) -> left.apply(right.apply(b)))
        .orElse((b) -> b);
  }
}
