package nl.knaw.huygens.hamcrest;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

import java.util.Optional;

public class OptionalPresentMatcher extends TypeSafeMatcher<Optional<?>> {
  private OptionalPresentMatcher() {

  }

  @Override
  protected boolean matchesSafely(Optional<?> item) {
    return item.isPresent();
  }

  @Override
  public void describeTo(Description description) {
    description.appendText("to have a value");
  }

  public static OptionalPresentMatcher present() {
    return new OptionalPresentMatcher();
  }
}
