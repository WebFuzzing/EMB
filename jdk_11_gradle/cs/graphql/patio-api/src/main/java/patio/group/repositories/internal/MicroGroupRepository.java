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
package patio.group.repositories.internal;

import io.micronaut.data.annotation.Repository;
import javax.persistence.EntityManager;
import patio.group.domain.Group;
import patio.group.repositories.GroupRepository;
import patio.infrastructure.persistence.MicroBaseRepository;

/** Persistence implementation access for {@link Group} */
@Repository
public abstract class MicroGroupRepository extends MicroBaseRepository implements GroupRepository {

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroGroupRepository(EntityManager entityManager) {
    super(entityManager);
  }
}
