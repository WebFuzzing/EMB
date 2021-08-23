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
package patio.common.domain.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Builds instances initializing their properties with a fluent API
 *
 * @param <T> the type of the instance built
 * @since 0.1.0
 */
public final class Builder<T> {
  private final transient List<Consumer<T>> statementList;
  private final transient Supplier<T> supplier;
  private transient boolean ifCond = true; // default

  private Builder(Supplier<T> supplier) {
    this.statementList = new ArrayList<>();
    this.supplier = supplier;
  }

  /**
   * Creates a new builder instance
   *
   * @param <T> the type of the instance built with this builder
   * @param supplier function creating a new instance of the type T
   * @return a new {@link Builder} instance capable of building instances of type T
   * @since 0.1.0
   */
  public static <T> Builder<T> build(Supplier<T> supplier) {
    return new Builder<>(supplier);
  }

  /**
   * Initializes a given property of the instance to be built
   *
   * @param setter consumer setting a property of the underlying instance to be built
   * @return the current builder instance
   * @since 0.1.0
   */
  public Builder<T> with(Consumer<T> setter) {
    if (ifCond) {
      statementList.add(setter);
    }
    return this;
  }

  /**
   * Returns the initialized instance to be built
   *
   * @return the instance built
   * @since 0.1.0
   */
  public T build() {
    T instance = this.supplier.get();

    statementList.stream().forEach(consumer -> consumer.accept(instance));

    return instance;
  }

  /**
   * Starts a block of properties modification that will be ultimately applied only if the provided
   * condition evaluates to true. The end of the condition block is determined by a {@link
   * Builder#endIfMatches()} call.
   *
   * @param condition condition to be met to apply the initialization changes
   * @return the current builder instance
   * @since 0.1.0
   */
  public Builder<T> ifMatches(BooleanSupplier condition) {
    this.ifCond = condition.getAsBoolean();
    return this;
  }

  /**
   * End of an {@link Builder#ifMatches(BooleanSupplier)} block
   *
   * @return the current builder instance
   * @since 0.1.0
   */
  public Builder<T> endIfMatches() {
    this.ifCond = true;
    return this;
  }
}
