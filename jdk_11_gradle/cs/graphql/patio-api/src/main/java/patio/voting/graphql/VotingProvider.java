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

import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.TypeRuntimeWiring;
import java.util.function.UnaryOperator;
import javax.inject.Singleton;
import patio.infrastructure.graphql.MutationProvider;
import patio.infrastructure.graphql.QueryProvider;
import patio.infrastructure.graphql.TypeProvider;

/**
 * Contains all mapped fetchers for queries, mutations and types for voting related operations
 *
 * @see QueryProvider
 * @see MutationProvider
 */
@Singleton
public class VotingProvider implements QueryProvider, MutationProvider, TypeProvider {

  private final transient VotingFetcher votingFetcher;
  private final transient VotingStatsFetcher votingStatsFetcher;

  /**
   * Initializes provider with its dependencies @@param votingFetcher all voting related data
   * fetchers
   *
   * @param votingFetcher {@link VotingFetcher} related voting data fetchers
   * @param votingStatsFetcher {@link VotingStatsFetcher} related voting statistics fetchers
   */
  public VotingProvider(VotingFetcher votingFetcher, VotingStatsFetcher votingStatsFetcher) {
    this.votingFetcher = votingFetcher;
    this.votingStatsFetcher = votingStatsFetcher;
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getQueries() {
    return (builder) ->
        builder
            .dataFetcher("listUserVotesInGroup", votingFetcher::listUserVotesInGroup)
            .dataFetcher("getVoting", votingFetcher::getVoting)
            .dataFetcher("getLastVotingByGroup", votingFetcher::getLastVotingByGroup)
            .dataFetcher("getStatsByGroup", votingStatsFetcher::getVotingStatsByGroup);
  }

  @Override
  public UnaryOperator<TypeRuntimeWiring.Builder> getMutations() {
    return (builder) ->
        builder
            .dataFetcher("createVoting", votingFetcher::createVoting)
            .dataFetcher("createVote", votingFetcher::createVote);
  }

  @Override
  public UnaryOperator<RuntimeWiring.Builder> getTypes() {
    return (runtime) ->
        runtime
            .type(
                "Voting",
                builder ->
                    builder
                        .dataFetcher("votes", votingFetcher::listVotesVoting)
                        .dataFetcher("didIVote", votingFetcher::didIVote)
                        .dataFetcher("stats", votingFetcher::getVotingStats)
                        .dataFetcher("nextVoting", votingFetcher::getNextVoting)
                        .dataFetcher("previousVoting", votingFetcher::getPreviousVoting))
            .type(
                "Vote",
                builder -> builder.dataFetcher("createdBy", votingFetcher::getVoteCreatedBy));
  }
}
