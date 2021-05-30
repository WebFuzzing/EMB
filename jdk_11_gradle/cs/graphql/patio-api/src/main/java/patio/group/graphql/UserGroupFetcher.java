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
import javax.inject.Singleton;
import patio.common.domain.utils.Result;
import patio.group.domain.Group;
import patio.group.domain.UserGroup;
import patio.group.services.UserGroupService;
import patio.infrastructure.graphql.Context;
import patio.infrastructure.graphql.ResultUtils;
import patio.user.domain.User;

/**
 * All related GraphQL operations over the {@link UserGroup} domain
 *
 * @since 0.1.0
 */
@Singleton
public class UserGroupFetcher {

  /**
   * Instance handling the business logic
   *
   * @since 0.1.0
   */
  private final transient UserGroupService service;

  /**
   * Constructor initializing the access to the business logic
   *
   * @param service class handling the logic over groups
   * @since 0.1.0
   */
  public UserGroupFetcher(UserGroupService service) {
    this.service = service;
  }

  /**
   * Adds an user to a group
   *
   * @param env GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Boolean> addUserToGroup(DataFetchingEnvironment env) {
    AddUserToGroupInput input = UserGroupFetcherUtils.addUserToGroupInput(env);

    Result<Boolean> result = service.addUserToGroup(input);
    return ResultUtils.render(result);
  }

  /**
   * Get if the current user an admin of the group
   *
   * @param env GraphQL execution environment
   * @return a boolean
   * @since 0.1.0
   */
  public boolean isCurrentUserAdmin(DataFetchingEnvironment env) {
    Context ctx = env.getContext();
    User user = ctx.getAuthenticatedUser();
    Group group = env.getSource();

    return service.isAdmin(user.getId(), group.getId());
  }

  /**
   * Fetches the users that belongs to a group
   *
   * @param env GraphQL execution environment
   * @return a list of available {@link User}
   * @since 0.1.0
   */
  public Iterable<User> listUsersGroup(DataFetchingEnvironment env) {
    ListUsersGroupInput input = UserGroupFetcherUtils.listUsersGroupInput(env);
    return service.listUsersGroup(input);
  }

  /**
   * Leave the specified group
   *
   * @param env GraphQL execution environment
   * @return an instance of {@link DataFetcherResult} because it could return errors
   * @since 0.1.0
   */
  public DataFetcherResult<Boolean> leaveGroup(DataFetchingEnvironment env) {
    LeaveGroupInput input = UserGroupFetcherUtils.leaveGroupInput(env);
    return ResultUtils.render(service.leaveGroup(input));
  }
}
