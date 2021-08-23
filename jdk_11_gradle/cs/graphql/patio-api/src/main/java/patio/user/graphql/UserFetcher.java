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
package patio.user.graphql;

import graphql.schema.DataFetchingEnvironment;
import java.util.Optional;
import java.util.UUID;
import javax.inject.Singleton;
import patio.infrastructure.graphql.Context;
import patio.user.domain.User;
import patio.user.services.UserService;
import patio.user.services.internal.DefaultUserService;

/**
 * All related GraphQL operations over the {@link User} domain
 *
 * @since 0.1.0
 */
@Singleton
public class UserFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient UserService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service instance of {@link DefaultUserService}
   * @since 0.1.0
   */
  public UserFetcher(UserService service) {
    this.service = service;
  }

  /**
   * Fetches all the available users in the system
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link User}
   * @since 0.1.0
   */
  public Iterable<User> listUsers(DataFetchingEnvironment env) {
    return service.listUsers();
  }

  /**
   * Get the specified user
   *
   * @param env GraphQL execution environment
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public Optional<User> getUser(DataFetchingEnvironment env) {
    UUID userId = env.getArgument("id");
    return service.getUser(userId);
  }

  /**
   * Get the current user
   *
   * @param env GraphQL execution environment
   * @return The requested {@link User}
   * @since 0.1.0
   */
  public User getCurrentUser(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    return ctx.getAuthenticatedUser();
  }
}
