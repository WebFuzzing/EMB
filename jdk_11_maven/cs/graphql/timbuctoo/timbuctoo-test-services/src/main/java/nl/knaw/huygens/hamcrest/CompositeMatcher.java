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

import com.google.common.collect.Lists;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.internal.ReflectiveTypeFinder;

import java.util.List;

public class CompositeMatcher<T> extends TypeSafeDiagnosingMatcher<T> {

  private List<TypeSafeMatcher<T>> matchers;

  public CompositeMatcher() {
    matchers = Lists.newArrayList();
  }

  public CompositeMatcher(ReflectiveTypeFinder typeFinder) {
    super(typeFinder);
  }

  @Override
  public void describeTo(Description description) {
    description.appendList("(", " " + "and" + " ", ")", getMatchers());
  }

  protected List<TypeSafeMatcher<T>> getMatchers() {
    return matchers;
  }

  protected void addMatcher(TypeSafeMatcher<T> propertyMatcher) {
    getMatchers().add(propertyMatcher);
  }

  @Override
  protected boolean matchesSafely(T item, Description mismatchDescription) {
    boolean matched = true;
    boolean firstMismatch = true;
    for (TypeSafeMatcher<T> matcher : getMatchers()) {
      if (!matcher.matches(item)) {
        if (!firstMismatch) {
          mismatchDescription.appendText(", ");
        }
        firstMismatch = false;
        matcher.describeMismatch(item, mismatchDescription);
        matched = false;
      }
    }
    return matched;
  }
}
