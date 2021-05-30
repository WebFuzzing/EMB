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
package patio.infrastructure.graphql.scalars;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import graphql.ExecutionInput;
import graphql.GraphQL;
import graphql.GraphQLError;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import io.micronaut.core.io.ResourceResolver;
import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import patio.infrastructure.graphql.TypeDefinitionRegistryFactory;

@SuppressWarnings("PMD.AvoidDuplicateLiterals")
public class DayOfWeekTests {

  @Test
  @DisplayName("DayOfWeek: Tests conversion from GraphQL query variables to DayOfWeek")
  void testFromValue() {
    // given:
    var dayOfWeek = DayOfWeek.MONDAY;

    // and: a query with variable references
    var query = "query FindDay($day: DayOfWeek!) { findDayOfWeek(day: $day) }";

    // and: the execution input with query/variables
    var input =
        ExecutionInput.newExecutionInput()
            .query(query)
            .variables(Map.of("day", dayOfWeek.toString()))
            .build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    Map<String, ?> data = executionResult.getData();

    // we should build the same id because the conversion went ok
    assertEquals(data.get("findDayOfWeek"), dayOfWeek.toString());
  }

  @Test
  @DisplayName("DayOfWeek: Tests conversion from GraphQL query empty variables")
  void testFromValueEmpty() {
    // and: a query with variable references
    var query = "query FindDayOfWeek($day: DayOfWeek) { findDayOfWeek(day: $day) }";

    // and: the execution input with query/variables
    var input = ExecutionInput.newExecutionInput().query(query).build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    Map<String, ?> data = executionResult.getData();

    // we should build the same id because the conversion went ok
    assertNull(data.get("findDayOfWeek"));
  }

  @Test
  @DisplayName("DayOfWeek: Tests failure when a query variable is wrong")
  void testFromValueFailure() {
    // given: a wrong day name we would like to use
    var dayOfWeek = "MANDY";

    // and: a query with variable references
    var query = "query FindDayOfWeek($day: DayOfWeek!) { findDayOfWeek(day: $day) }";

    // and: the execution input with query/variables
    var input =
        ExecutionInput.newExecutionInput().query(query).variables(Map.of("day", dayOfWeek)).build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    List<GraphQLError> errors = executionResult.getErrors();

    // we should build a coercing value exception
    assertTrue(errors.size() == 1);
    assertTrue(errors.get(0) instanceof GraphQLError);
  }

  @Test
  @DisplayName("DayOfWeek: Tests conversion from GraphQL query embedded value to DayOfWeek")
  void testFromLiteral() {
    // given: a day of week we would like to use
    var dayOfWeek = DayOfWeek.MONDAY;

    // and: a query with harcoded literal
    var query = "query { findDayOfWeek(day: \"" + dayOfWeek + "\") }";

    // and: the execution input with query/variables
    var input = ExecutionInput.newExecutionInput().query(query).build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    Map<String, ?> data = executionResult.getData();

    // we should build the same id because the conversion went ok
    assertEquals(data.get("findDayOfWeek"), dayOfWeek.toString());
  }

  @Test
  @DisplayName("DayOfWeek: Tests conversion from GraphQL empty embedded value")
  void testFromLiteralEmpty() {
    // and: a query with harcoded literal
    var query = "query { findDayOfWeek }";

    // and: the execution input with query/variables
    var input = ExecutionInput.newExecutionInput().query(query).build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    Map<String, ?> data = executionResult.getData();

    // we should not build anything
    assertNull(data.get("findDayOfWeek"));
  }

  @Test
  @DisplayName("DayOfWeek: Tests failure from GraphQL query with wrong embedded id")
  void testFailureFromLiteral() {
    // given: a wrong id
    var dayOfWeek = "FROGDAY";

    // and: a query with harcoded literal
    var query = "query { findDayOfWeek(day: \"" + dayOfWeek + "\") }";

    // and: the execution input with query/variables
    var input = ExecutionInput.newExecutionInput().query(query).build();

    // when: executing the query passing the related variables
    var executionResult = createGraphQL().execute(input);
    List<GraphQLError> errors = executionResult.getErrors();

    // we should build a coercing literal exception
    assertTrue(errors.size() == 1);
    assertTrue(errors.get(0) instanceof GraphQLError);
  }

  private GraphQL createGraphQL() {
    var wiring =
        RuntimeWiring.newRuntimeWiring()
            .scalar(ScalarsConstants.DAY_OF_WEEK)
            .type(
                "Query",
                builder -> builder.dataFetcher("findDayOfWeek", (env) -> env.getArgument("day")))
            .build();
    var registry =
        new TypeDefinitionRegistryFactory()
            .load(
                "classpath:patio/infrastructure/graphql/scalars/dayofweek_schema.graphql",
                new ResourceResolver());
    var schema = new SchemaGenerator().makeExecutableSchema(registry, wiring);

    // when: executing the query against the GraphQL engine
    return GraphQL.newGraphQL(schema).build();
  }
}
