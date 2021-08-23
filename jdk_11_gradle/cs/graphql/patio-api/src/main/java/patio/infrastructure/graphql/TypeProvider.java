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
package patio.infrastructure.graphql;

import graphql.schema.idl.RuntimeWiring;
import java.util.function.UnaryOperator;

/**
 * A class implementing this interface it's supposed to provide implementation for type's fields
 * fetchers defined in the schema
 */
@FunctionalInterface
public interface TypeProvider {

  /**
   * Returns a function
   *
   * @return an {@link UnaryOperator} of type {@link RuntimeWiring.Builder}
   */
  UnaryOperator<RuntimeWiring.Builder> getTypes();
}
