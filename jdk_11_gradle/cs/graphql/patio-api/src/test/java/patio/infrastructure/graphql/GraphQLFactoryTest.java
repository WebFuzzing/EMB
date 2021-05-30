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

import static io.github.benas.randombeans.api.EnhancedRandom.randomListOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import graphql.ExecutionInput;
import graphql.schema.idl.TypeRuntimeWiring;
import io.micronaut.core.io.ResourceResolver;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;
import patio.common.graphql.CommonScalarProvider;
import patio.group.domain.Group;
import patio.user.domain.User;

class GraphQLFactoryTest {

  @Test
  void testCreateSchema() {
    // and: mocking group fetcher behavior
    List<QueryProvider> queryProviders = List.of(this::mockQueryFetcherProvider);

    // when: creating a valid GraphQL engine
    var typeRegistry =
        new TypeDefinitionRegistryFactory()
            .load("classpath:graphql/schema.graphqls", new ResourceResolver());
    var schema =
        new GraphQLSchemaFactory()
            .getSchema(
                typeRegistry,
                queryProviders,
                List.of(),
                List.of(),
                List.of(new CommonScalarProvider()));
    var graphQLEngine = new GraphQLFactory().graphQL(schema);

    // and: querying the schema with an authenticated user
    var context = new Context();
    context.setAuthenticatedUser(User.builder().build());
    var executionInput =
        ExecutionInput.newExecutionInput()
            .query("{ listMyGroups { name } }")
            .context(context)
            .build();
    var result = graphQLEngine.execute(executionInput);
    Map<String, List<Map<String, ?>>> data = result.getData();

    var groupList = data.get("listMyGroups");

    // then: we should build the expected result
    assertEquals(groupList.size(), 2);
  }

  private UnaryOperator<TypeRuntimeWiring.Builder> mockQueryFetcherProvider() {
    return (TypeRuntimeWiring.Builder builder) ->
        builder.dataFetcher("listMyGroups", (env) -> randomListOf(2, Group.class));
  }
}
