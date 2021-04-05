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

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Functions used to help handling lambda expressions (filters/transformations/...)
 *
 * @since 0.1.0
 */
public final class FunctionsUtils {

  private FunctionsUtils() {
    /* empty */
  }

  /**
   * Safely wraps a possible failing function. The possible {@link Throwable} can be handled by the
   * consumer passed as argument.
   *
   * @param <A> type of the failing function input
   * @param <B> type of the failing function output
   * @param function possible failing execution
   * @param consumer to handle the possible exception
   * @return an {@link Optional} object of the failing function type
   * @since 0.1.0
   */
  @SuppressWarnings("PMD.AvoidCatchingThrowable")
  public static <A, B> Function<A, Optional<B>> safely(
      Function<A, B> function, Consumer<Throwable> consumer) {
    return (A input) -> {
      try {
        return Optional.of(function.apply(input));
      } catch (Throwable throwable) {
        consumer.accept(throwable);
      }
      return Optional.empty();
    };
  }

  /**
   * Composes all predicates and produces a new one that will be true if any of the contained
   * predicates evaluates true for the same input
   *
   * @param <A> type of the input
   * @param predicates variable list of {@link Predicate} types
   * @return an instance of {@link Predicate} as the result of composing all the predicates passed
   *     as parameter
   * @since 0.1.0
   */
  @SafeVarargs
  public static <A> Predicate<A> any(Predicate<A>... predicates) {
    return (A a) -> Arrays.stream(predicates).anyMatch(p -> p.test(a));
  }
}
