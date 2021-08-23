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

import graphql.execution.DataFetcherResult;
import graphql.schema.DataFetchingEnvironment;
import java.util.List;
import javax.inject.Singleton;
import patio.group.domain.Group;
import patio.group.services.GroupService;
import patio.infrastructure.graphql.Context;
import patio.infrastructure.graphql.ResultUtils;
import patio.user.domain.User;

/**
 * All related GraphQL operations over the {@link Group} domain
 *
 * @since 0.1.0
 */
@Singleton
public class GroupFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient GroupService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public GroupFetcher(GroupService service) {
    this.service = service;
  }

  /**
   * Fetches all the available groups in the system
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Group}
   * @since 0.1.0
   */
  public Iterable<Group> listGroups(DataFetchingEnvironment env) {
    return service.listGroups();
  }

  /**
   * Fetches the groups of the current user
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link Group}
   * @since 0.1.0
   */
  public List<Group> listMyGroups(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    User currentUser = ctx.getAuthenticatedUser();
    return service.listGroupsUser(currentUser.getId());
  }

  /**
   * Fetches the groups that belongs to an user
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link User}
   * @since 0.1.0
   */
  public List<Group> listGroupsUser(DataFetchingEnvironment env) {
    User user = env.getSource();
    return service.listGroupsUser(user.getId());
  }

  /**
   * Creates a new group
   *
   * @param env GraphQL execution environment
   * @return the new {@link Group}
   * @since 0.1.0
   */
  public Group createGroup(DataFetchingEnvironment env) {
    UpsertGroupInput input = GroupFetcherUtils.upsertGroupInput(env);

    return service.createGroup(input);
  }

  /**
   * Updates a group
   *
   * @param env GraphQL execution environment
   * @return the updated {@link Group}
   * @since 0.1.0
   */
  public DataFetcherResult<Group> updateGroup(DataFetchingEnvironment env) {
    UpsertGroupInput input = GroupFetcherUtils.upsertGroupInput(env);
    return ResultUtils.render(service.updateGroup(input));
  }

  /**
   * Get the specified group
   *
   * @param env GraphQL execution environment
   * @return The requested {@link Group}
   * @since 0.1.0
   */
  public DataFetcherResult<Group> getGroup(DataFetchingEnvironment env) {
    GetGroupInput input = GroupFetcherUtils.getGroupInput(env);
    return ResultUtils.render(service.getGroup(input));
  }

  /**
   * Returns the user's favourite group
   *
   * @param env GraphQL execution environment
   * @return the user's favourite {@link Group}
   */
  public DataFetcherResult<Group> getMyFavouriteGroup(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    User currentUser = ctx.getAuthenticatedUser();
    return ResultUtils.render(service.getMyFavouriteGroup(currentUser.getId()));
  }
}
