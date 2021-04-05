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

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import java.util.function.UnaryOperator;
import javax.inject.Singleton;
import patio.group.graphql.GroupFetcher;
import patio.infrastructure.graphql.MutationProvider;
import patio.infrastructure.graphql.QueryProvider;
import patio.infrastructure.graphql.TypeProvider;

/**
 * Contains all mapped fetchers for queries, mutations, and types for user related operations
 *
 * @see QueryProvider
 * @see MutationProvider
 */
@Singleton
public class UserProvider implements QueryProvider, MutationProvider, TypeProvider {

  private final transient UserFetcher userFetcher;
  private final transient GroupFetcher groupFetcher;

  /**
   * Initializes providers with its required dependencies
   *
   * @param userFetcher data fetchers for user operations
   * @param groupFetcher data fetchers for group operations
   */
  public UserProvider(UserFetcher userFetcher, GroupFetcher groupFetcher) {
    this.userFetcher = userFetcher;
    this.groupFetcher = groupFetcher;
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getMutations() {
    return null;
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getQueries() {
    return (builder) ->
        builder
            .dataFetcher("listUsers", userFetcher::listUsers)
            .dataFetcher("getUser", userFetcher::getUser)
            .dataFetcher("myProfile", userFetcher::getCurrentUser);
  }

  @Override
  public UnaryOperator<RuntimeWiring.Builder> getTypes() {
    return (runtime) ->
        runtime
            .type("Login", builder -> builder.dataFetcher("profile", userFetcher::getCurrentUser))
            .type(
                "UserProfile",
                builder ->
                    builder
                        .dataFetcher("groups", groupFetcher::listGroupsUser)
                        .dataFetcher("favouriteGroup", groupFetcher::getMyFavouriteGroup));
  }
}
