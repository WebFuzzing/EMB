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

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/** Utility functions to deal with {@link Optional} instances */
public final class OptionalUtils {

  private OptionalUtils() {
    /* empty */
  }

  /**
   * @param <A> left object type
   * @param <B> right object type
   *     <p>Holds a pair of {@link Optional} instances to be combined with each other
   */
  public static class Holder<A, B> {
    private final transient Optional<A> left;
    private final transient Optional<B> right;

    /**
     * Initializes a {@link Holder}
     *
     * @param left left part of the {@link Holder}
     * @param right right part of the {@link Holder}
     */
    /* default */ Holder(Optional<A> left, Optional<B> right) {
      this.left = left;
      this.right = right;
    }

    /**
     * Combines both parts of the holder to be combined
     *
     * @param <C> result type
     * @param func function to combine both parts of the holder. The result of the function will be
     *     wrapped in an {@link Optional}
     * @return the combination result
     */
    public <C> Optional<C> into(BiFunction<A, B, C> func) {
      return this.left.flatMap((A a) -> this.right.flatMap((B b) -> Optional.of(func.apply(a, b))));
    }

    /**
     * When the result could be an {@link Optional} of an {@link Optional} this function could
     * unwrap the inner {@link Optional}.
     *
     * @param <C> result type
     * @param func function to combine both parts of the holder. If the function returns and
     *     optional the result won't be wrapped in another optional.
     * @return an {@link Optional} with the result of executing the function passed as parameter
     */
    public <C> Optional<C> flatmapInto(BiFunction<A, B, Optional<C>> func) {
      return this.left.flatMap((A a) -> this.right.flatMap((B b) -> func.apply(a, b)));
    }
  }

  /**
   * Combine two {@link Optional} instances
   *
   * @param <A> left object type
   * @param <B> right object type
   * @param left left part of the {@link Holder}
   * @param right right part of the {@link Holder}
   * @return an instance of type {@link Holder}
   */
  public static <A, B> Holder<A, B> combine(Optional<A> left, Optional<B> right) {
    return new Holder<>(left, right);
  }

  /**
   * * This function can be used to create a side effect
   *
   * <p><code>Optional.of(value).map(sideEffect(System.out::println))</code>
   *
   * @param <A> element to use in the side effect
   * @param func function to create a side effect
   * @return an identity function
   */
  public static <A> Function<A, A> sideEffect(Consumer<A> func) {
    return (A value) -> {
      Optional.ofNullable(func).ifPresent(fn -> Optional.ofNullable(value).ifPresent(fn));
      return value;
    };
  }
}
