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
package patio;

import io.micronaut.runtime.Micronaut;

/**
 * Application's entry point
 *
 * @since 0.1.0
 */
@SuppressWarnings("PMD.UseUtilityClass")
public class Application {

  /**
   * Executes the application
   *
   * @param args possible command line arguments
   * @since 0.1.0
   */
  public static void main(String[] args) {
    Micronaut.run(Application.class, args);
  }
}
