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

import graphql.schema.DataFetchingEnvironment;
import java.time.DayOfWeek;
import java.time.OffsetTime;
import java.util.List;
import java.util.UUID;
import patio.infrastructure.graphql.Context;
import patio.user.domain.User;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * GroupFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class GroupFetcherUtils {

  private GroupFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link GetGroupInput} from the data coming from the {@link DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link GetGroupInput}
   * @since 0.1.0
   */
  /* default */ static GetGroupInput getGroupInput(DataFetchingEnvironment environment) {
    UUID groupId = environment.getArgument("id");
    Context ctx = environment.getContext();
    User currentUser = ctx.getAuthenticatedUser();
    return GetGroupInput.newBuilder()
        .withCurrentUserId(currentUser.getId())
        .withGroupId(groupId)
        .build();
  }

  /**
   * Creates a {@link UpsertGroupInput} from the data coming from the {@link
   * DataFetchingEnvironment}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of {@link UpsertGroupInput}
   * @since 0.1.0
   */
  /* default */ static UpsertGroupInput upsertGroupInput(DataFetchingEnvironment environment) {
    UUID groupId = environment.getArgument("groupId");
    String name = environment.getArgument("name");
    boolean anonymousVote = environment.getArgument("anonymousVote");
    List<DayOfWeek> votingDays = environment.getArgument("votingDays");
    int votingDuration = environment.getArgument("votingDuration");
    OffsetTime votingTime = environment.getArgument("votingTime");
    Context ctx = environment.getContext();
    User currentUser = ctx.getAuthenticatedUser();

    return UpsertGroupInput.newBuilder()
        .with(i -> i.setCurrentUserId(currentUser.getId()))
        .with(i -> i.setGroupId(groupId))
        .with(i -> i.setName(name))
        .with(i -> i.setAnonymousVote(anonymousVote))
        .with(i -> i.setVotingDays(votingDays))
        .with(i -> i.setVotingTime(votingTime))
        .with(i -> i.setVotingDuration(votingDuration))
        .build();
  }
}
