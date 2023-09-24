/*
 * This file is part of bibliothek, licensed under the MIT License.
 *
 * Copyright (c) 2019-2023 PaperMC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.papermc.bibliothek.util;

import java.time.Instant;
import java.util.Comparator;
import java.util.Objects;

public final class BringOrderToChaos {
  private BringOrderToChaos() {
  }

  public static <T extends NameSource & TimeSource> Comparator<T> timeOrNameComparator() {
    return (o1, o2) -> {
      final Instant t1 = o1.time();
      final Instant t2 = o2.time();
      // Both objects are not guaranteed to have a time present, but are guaranteed
      // to have a name present - we prefer to compare them by time, but in cases where
      // the time is not available on both objects we will compare them using their name
      if (t1 != null && t2 != null) {
        return t1.compareTo(t2);
      }
      final String n1 = Objects.requireNonNull(o1.name(), () -> "name of " + o1);
      final String n2 = Objects.requireNonNull(o2.name(), () -> "name of " + o2);
      return n1.compareTo(n2);
    };
  }
}
