package nl.knaw.huygens.timbuctoo.util;

import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import org.apache.tinkerpop.gremlin.structure.Property;

public class PropertyMatcher extends CompositeMatcher<Property> {

  private PropertyMatcher() {

  }

  public static PropertyMatcher likeProperty() {
    return new PropertyMatcher();
  }

  public PropertyMatcher withKey(String key) {
    this.addMatcher(new PropertyEqualityMatcher<Property, String>("key", key) {
      @Override
      protected String getItemValue(Property item) {
        return item.key();
      }
    });
    return this;
  }
}
