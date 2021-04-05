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
package patio.infrastructure.graphql.instrumentation;

import graphql.execution.instrumentation.InstrumentationState;

/**
 * Represents the current state of the hierarchy of a field that could be annotated by
 * the @anonymousAllowed directive
 *
 * @since 0.1.0
 */
class AuthenticationCheckState implements InstrumentationState {
  private boolean allowed;

  /**
   * Initial state
   *
   * @param allowed if the hierarchy parent has been already allowed
   * @since 0.1.0
   */
  /* default */ AuthenticationCheckState(boolean allowed) {
    this.allowed = allowed;
  }

  /**
   * Returns whether the field hierarchy has been already allowed or not
   *
   * @return true if the field hierarchy has been already allowed, false otherwise
   * @since 0.1.0
   */
  /* default */ boolean isAllowed() {
    return allowed;
  }

  /**
   * @param allowed true if the field hierarchy has been already allowed, false otherwise
   * @since 0.1.0
   */
  /* default */ void setAllowed(boolean allowed) {
    this.allowed = allowed;
  }
}
