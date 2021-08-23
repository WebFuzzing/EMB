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
package patio.infrastructure.persistence;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/** Base repository to access {@link EntityManager} */
public class MicroBaseRepository {
  @PersistenceContext private final transient EntityManager entityManager;

  /**
   * Initializes repository with {@link EntityManager}
   *
   * @param entityManager persistence {@link EntityManager} instance
   */
  public MicroBaseRepository(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Returns the current {@link EntityManager}
   *
   * @return an {@link EntityManager}
   */
  public EntityManager getEntityManager() {
    return entityManager;
  }
}
