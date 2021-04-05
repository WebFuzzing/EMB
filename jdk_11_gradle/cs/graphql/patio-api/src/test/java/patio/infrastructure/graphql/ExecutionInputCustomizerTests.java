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

import graphql.ExecutionInput;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import java.util.Optional;
import org.dataloader.DataLoaderRegistry;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import patio.security.services.SecurityService;
import patio.user.domain.User;
import reactor.test.StepVerifier;

/**
 * Tests the creation of the GraphQL {@link Context}
 *
 * @since 0.1.0
 */
class ExecutionInputCustomizerTests {

  @Test
  void testCustomizeExecutionInputWithUser() {
    // given: a request authorization header
    var httpRequest = Mockito.mock(HttpRequest.class);
    var httpHeaders = Mockito.mock(HttpHeaders.class);

    Mockito.when(httpRequest.getHeaders()).thenReturn(httpHeaders);
    Mockito.when(httpHeaders.getAuthorization()).thenReturn(Optional.of("Bearer token"));

    // and: a security service checking and getting a user
    var mockedService = Mockito.mock(SecurityService.class);
    Mockito.when(mockedService.resolveUser(Mockito.anyString()))
        .thenReturn(Optional.of(User.builder().build()));

    // when: customizing a given ExecutionInput
    var builder = new ExecutionInputCustomizer(mockedService, new DataLoaderRegistry());
    var sourceExecutionInput = ExecutionInput.newExecutionInput().build();
    var customizedExecutionInput = builder.customize(sourceExecutionInput, httpRequest);

    // then: it should be able to build a context with a user
    StepVerifier.create(customizedExecutionInput)
        .expectNextMatches(
            executionInput -> {
              Context context = (Context) executionInput.getContext();

              return context.getAuthenticatedUser() != null;
            })
        .expectComplete()
        .verify();
  }

  @Test
  void testCustomizeExecutionInputWithNoToken() {
    // given: a request authorization header with NO TOKEN
    var httpRequest = Mockito.mock(HttpRequest.class);
    var httpHeaders = Mockito.mock(HttpHeaders.class);

    Mockito.when(httpRequest.getHeaders()).thenReturn(httpHeaders);
    Mockito.when(httpHeaders.getAuthorization()).thenReturn(Optional.empty());

    // and: a builder with no service (no necessary)
    var builder = new ExecutionInputCustomizer(null, new DataLoaderRegistry());
    var sourceExecutionInput = ExecutionInput.newExecutionInput().build();
    var customizedExecutionInput = builder.customize(sourceExecutionInput, httpRequest);

    // then: it should be able to build a context with no user
    StepVerifier.create(customizedExecutionInput)
        .expectNextMatches(
            executionInput -> {
              Context context = (Context) executionInput.getContext();

              return context.getAuthenticatedUser() == null;
            })
        .expectComplete()
        .verify();
  }
}
