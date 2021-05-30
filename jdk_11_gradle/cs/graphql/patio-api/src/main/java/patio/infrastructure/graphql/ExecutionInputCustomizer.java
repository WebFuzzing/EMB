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
import io.micronaut.configuration.graphql.GraphQLExecutionInputCustomizer;
import io.micronaut.context.annotation.Primary;
import io.micronaut.core.async.publisher.Publishers;
import io.micronaut.http.HttpRequest;
import java.util.Optional;
import javax.inject.Singleton;
import org.dataloader.DataLoaderRegistry;
import org.reactivestreams.Publisher;
import patio.security.services.SecurityService;

/**
 * Customizes {@link ExecutionInput} by adding information about security (e.g. authenticated user)
 * and registered data loaders (e.g. required for proper batching)
 *
 * @since 0.1.0
 */
@Primary
@Singleton
public class ExecutionInputCustomizer implements GraphQLExecutionInputCustomizer {

  private static final String JWT_PREFIX = "JWT ";
  private static final String EMPTY = "";

  /**
   * Service responsible to resolve the user from the provided token
   *
   * @since 0.1.0
   */
  private final transient SecurityService securityService;

  private final transient DataLoaderRegistry dataLoaderRegistry;

  /**
   * Initializes the execution input customizer with security and data loading information
   *
   * @param securityService required to inject authenticated user information to context
   * @param dataLoaderRegistry required to access registered data loaders
   * @since 0.1.0
   */
  public ExecutionInputCustomizer(
      SecurityService securityService, DataLoaderRegistry dataLoaderRegistry) {
    this.securityService = securityService;
    this.dataLoaderRegistry = dataLoaderRegistry;
  }

  private Optional<String> extractToken(String authorization) {
    return Optional.ofNullable(authorization).map(auth -> auth.replace(JWT_PREFIX, EMPTY));
  }

  private Optional<Context> resolveUser(String token) {
    return Optional.of(token)
        .flatMap(securityService::resolveUser)
        .map(
            user -> {
              Context context = new Context();
              context.setAuthenticatedUser(user);
              return context;
            });
  }

  @Override
  public Publisher<ExecutionInput> customize(
      ExecutionInput executionInput, HttpRequest httpRequest) {
    Context context =
        httpRequest
            .getHeaders()
            .getAuthorization()
            .flatMap(this::extractToken)
            .flatMap(this::resolveUser)
            .orElseGet(Context::new);

    ExecutionInput input =
        ExecutionInput.newExecutionInput()
            .context(context)
            .query(executionInput.getQuery())
            .operationName(executionInput.getOperationName())
            .variables(executionInput.getVariables())
            .dataLoaderRegistry(this.dataLoaderRegistry)
            .build();

    return Publishers.just(input);
  }
}
