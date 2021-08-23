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
package patio.infrastructure.utils;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/** Functions to handle {@link Iterable} instances */
public final class IterableUtils {

  private IterableUtils() {
    /* empty */
  }

  /**
   * Transforms an {@link Iterable} to {@link Stream}
   *
   * @param <T> type of the elements of the iterable
   * @param iterable instance of type {@link Iterable} to transform
   * @return an instance of type {@link Stream}
   */
  public static <T> Stream<T> iterableToStream(Iterable<T> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
  }
}
