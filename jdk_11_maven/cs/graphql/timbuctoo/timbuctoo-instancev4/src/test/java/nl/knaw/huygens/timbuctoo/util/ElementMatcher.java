package nl.knaw.huygens.timbuctoo.util;

import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

public class ElementMatcher extends CompositeMatcher<Element> {

  private ElementMatcher() {

  }

  public static ElementMatcher likeElement() {
    return new ElementMatcher();
  }

  public <T extends Element> ElementMatcher ofType(final Class<T> type) {
    this.addMatcher(new TypeSafeMatcher<Element>() {
      @Override
      protected boolean matchesSafely(Element element) {
        return type.isAssignableFrom(element.getClass());
      }

      @Override
      public void describeTo(Description description) {
        description.appendText("Type is ").appendValue(type);
      }
    });
    return this;
  }

  public ElementMatcher withTimId(String id) {
    this.addMatcher(new PropertyEqualityMatcher<Element, String>("id", id) {
      @Override
      protected String getItemValue(Element item) {
        return item.value("tim_id");
      }
    });
    return this;
  }

  public ElementMatcher withProperty(final String name, final Object value) {
    this.addMatcher(new PropertyEqualityMatcher<Element, Object>(name, value) {
      @Override
      protected Object getItemValue(Element item) {
        return item.value(name);
      }
    });
    return this;
  }
}
