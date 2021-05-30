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

import graphql.schema.idl.TypeRuntimeWiring;
import java.util.function.UnaryOperator;

/**
 * A class implementing this interface it's supposed to provide data fetchers to handle query calls
 */
@FunctionalInterface
public interface QueryProvider {

  /**
   * It should return a function that will receive a {@link TypeRuntimeWiring.Builder} and will
   * return that same {@link TypeRuntimeWiring.Builder}. The builder received by that function could
   * be use to chain several {@link TypeRuntimeWiring.Builder#dataFetcher(String,
   * graphql.schema.DataFetcher)} calls to register query fetchers
   *
   * @return a function that will receive a {@link TypeRuntimeWiring.Builder} and will return that
   *     same {@link TypeRuntimeWiring.Builder}
   */
  UnaryOperator<TypeRuntimeWiring.Builder> getQueries();
}
