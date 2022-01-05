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

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

public abstract class PropertyMatcher<T, V> extends TypeSafeMatcher<T> {

  protected String propertyName;
  protected Matcher<V> matcher;

  public PropertyMatcher(String propertyName, Matcher<V> matcher) {
    this.propertyName = propertyName;
    this.matcher = matcher;
  }

  @Override
  public void describeTo(Description description) {
    description.appendText(propertyName) //
        .appendText(" matches ");
    matcher.describeTo(description);
  }

  @Override
  protected void describeMismatchSafely(T item, Description mismatchDescription) {
    mismatchDescription.appendText(propertyName).appendText(" ");
    matcher.describeMismatch(getItemValue(item), mismatchDescription);
  }

  @Override
  protected boolean matchesSafely(T item) {
    boolean matches = matcher.matches(getItemValue(item));
    return matches;
  }

  protected abstract V getItemValue(T item);

}
