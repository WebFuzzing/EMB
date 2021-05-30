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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static patio.infrastructure.utils.ErrorConstants.BAD_CREDENTIALS;

import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import io.micronaut.core.io.ResourceResolver;
import java.util.Map;
import org.junit.jupiter.api.Test;
import patio.infrastructure.graphql.instrumentation.AuthenticationCheck;
import patio.user.domain.User;

/**
 * Tests {@link AuthenticationCheck}
 *
 * @since 0.1.0
 */
public class AuthenticationCheckTests {

  @Test
  void testQueryAllowedBecauseDirective() {
    // when: executing a query with no context (therefore with no user)
    var input =
        ExecutionInput.newExecutionInput().query("{ sayHi }").context((Object) null).build();
    var result = createGraphQL().execute(input);
    Map<String, ?> payload = result.getData();

    // then: we should build result because it has the directive
    assertEquals("Hi", payload.get("sayHi"));
  }

  @Test
  void testQueryAllowedBecauseUser() {
    // when: executing a query with and authenticated user
    var context = new Context();
    context.setAuthenticatedUser(User.builder().build());

    var input =
        ExecutionInput.newExecutionInput().query("{ sayHiAuthenticated }").context(context).build();
    var result = createGraphQL().execute(input);
    Map<String, ?> payload = result.getData();

    // then: we should build result because there was an authenticated user
    assertEquals("Hi authenticated", payload.get("sayHiAuthenticated"));
  }

  @Test
  void testQueryForbiddenBecauseMissingUser() {
    // when: executing a query with no context user
    var input =
        ExecutionInput.newExecutionInput()
            .context((Object) null)
            .query("{ sayHiAuthenticated }")
            .build();
    var result = createGraphQL().execute(input);
    var errors = result.getErrors();
    var badCredentials = errors.get(0);

    // then:
    assertEquals(1, errors.size());

    // and:
    assertEquals(BAD_CREDENTIALS.getMessage(), badCredentials.getMessage());
    assertEquals(BAD_CREDENTIALS.getCode(), badCredentials.getExtensions().get("code"));
  }

  private GraphQL createGraphQL() {
    var wiring =
        RuntimeWiring.newRuntimeWiring()
            .type(
                "Query",
                builder ->
                    builder
                        .dataFetcher("sayHi", (env) -> "Hi")
                        .dataFetcher("sayHiAuthenticated", (env) -> "Hi authenticated"))
            .build();
    var registry =
        new TypeDefinitionRegistryFactory()
            .load(
                "classpath:patio/infrastructure/graphql/anonymousallowed_schema.graphql",
                new ResourceResolver());

    var schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);

    // when: executing the query against the GraphQL engine
    return GraphQL.newGraphQL(schema).instrumentation(new AuthenticationCheck()).build();
  }
}
