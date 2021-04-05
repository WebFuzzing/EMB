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
package patio.voting.graphql;

import graphql.schema.DataFetchingEnvironment;
import java.util.UUID;

/**
 * Contains functions to build domain inputs from the underlying {@link DataFetchingEnvironment}
 * coming from the GraphQL engine execution. This class is meant to be used only for the {@link
 * VotingFetcher} instance and related tests.
 *
 * @since 0.1.0
 */
final class VotingStatsFetcherUtils {

  private VotingStatsFetcherUtils() {
    /* empty */
  }

  /**
   * Creates a {@link GetStatsByGroupInput}
   *
   * @param environment the GraphQL {@link DataFetchingEnvironment}
   * @return an instance of type {@link ListVotingsGroupInput}
   * @since 0.1.0
   */
  /* default */ static GetStatsByGroupInput createGetStatsByGroupInput(
      DataFetchingEnvironment environment) {
    UUID groupId = environment.getArgument("groupId");

    return GetStatsByGroupInput.newBuilder().with(i -> i.setGroupId(groupId)).build();
  }
}
