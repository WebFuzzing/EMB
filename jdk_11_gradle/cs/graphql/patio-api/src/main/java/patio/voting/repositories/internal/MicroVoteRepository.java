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
package patio.voting.repositories.internal;

import io.micronaut.data.annotation.Repository;
import javax.persistence.EntityManager;
import patio.infrastructure.persistence.MicroBaseRepository;
import patio.voting.domain.Vote;
import patio.voting.repositories.VoteRepository;

/** Persistence implementation access for {@link Vote} */
@Repository
public abstract class MicroVoteRepository extends MicroBaseRepository implements VoteRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroVoteRepository(EntityManager entityManager) {
    super(entityManager);
  }
}
