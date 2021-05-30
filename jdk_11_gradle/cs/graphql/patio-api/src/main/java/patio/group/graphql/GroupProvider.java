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
package patio.group.graphql;

import graphql.schema.idl.TypeRuntimeWiring;
import java.util.function.UnaryOperator;
import javax.inject.Singleton;
import patio.infrastructure.graphql.MutationProvider;
import patio.infrastructure.graphql.QueryProvider;

/**
 * Contains all mapped fetchers for queries, and mutations for group related operations
 *
 * @see QueryProvider
 * @see MutationProvider
 */
@Singleton
public class GroupProvider implements QueryProvider, MutationProvider {

  private final transient GroupFetcher groupFetcher;
  private final transient UserGroupFetcher userGroupFetcher;

  /**
   * Data fetchers required some dependencies
   *
   * @param groupFetcher all group fetchers
   * @param userGroupFetcher all group/user fetchers
   */
  public GroupProvider(GroupFetcher groupFetcher, UserGroupFetcher userGroupFetcher) {
    this.groupFetcher = groupFetcher;
    this.userGroupFetcher = userGroupFetcher;
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getMutations() {
    return (builder) ->
        builder
            .dataFetcher("createGroup", groupFetcher::createGroup)
            .dataFetcher("updateGroup", groupFetcher::updateGroup)
            .dataFetcher("addUserToGroup", userGroupFetcher::addUserToGroup)
            .dataFetcher("leaveGroup", userGroupFetcher::leaveGroup);
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getQueries() {
    return (builder) ->
        builder
            .dataFetcher("listGroups", groupFetcher::listGroups)
            .dataFetcher("listMyGroups", groupFetcher::listMyGroups)
            .dataFetcher("getGroup", groupFetcher::getGroup)
            .dataFetcher("getMyFavouriteGroup", groupFetcher::getMyFavouriteGroup);
  }
}
