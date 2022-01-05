package nl.knaw.huygens.hamcrest;

/*
 * #%L
 * Test services
 * =======
 * Copyright (C) 2012 - 2015 Huygens ING
 * =======
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.base.Joiner;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class ListContainsItemsInAnyOrderMatcher<T> extends TypeSafeMatcher<List<T>> {
  private final List<T> variations;
  private List<T> nonMatchingItems;
  private List<T> extraFound;

  private ListContainsItemsInAnyOrderMatcher(List<T> variations) {
    this.variations = variations;
  }

  public static <E> ListContainsItemsInAnyOrderMatcher<E> containsInAnyOrder(List<E> variations) {
    return new ListContainsItemsInAnyOrderMatcher<E>(variations);
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("Contains in any order ").appendValue(variations);
  }

  @Override
  protected void describeMismatchSafely(List<T> item, Description mismatchDescription) {
    if (nonMatchingItems != null) {
      mismatchDescription.appendText("No matchers found for ");
      mismatchDescription.appendText(Joiner.on(", ").join(nonMatchingItems));
    } else {
      mismatchDescription.appendText("Extra items found ");
      mismatchDescription.appendText(Joiner.on(", ").join(extraFound));
    }
  }

  @Override
  protected boolean matchesSafely(List<T> item) {
    if (item.containsAll(variations)) {
      if (variations.containsAll(item)) {
        return true;
      }

      extraFound = item.stream().filter(variation -> !variations.contains(variation)).collect(toList());

      return false;
    }

    nonMatchingItems = variations.stream().filter(variation -> !item.contains(variation)).collect(toList());

    return false;
  }


}
