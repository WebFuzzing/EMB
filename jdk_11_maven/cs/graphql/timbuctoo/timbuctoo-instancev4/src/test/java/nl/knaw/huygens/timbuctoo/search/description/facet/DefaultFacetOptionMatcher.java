package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;

// needed to use Facet.Option to be able to match with the Facet.getOptions
public class DefaultFacetOptionMatcher extends CompositeMatcher<Facet.Option> {
  private DefaultFacetOptionMatcher() {

  }

  public static DefaultFacetOptionMatcher likeDefaultFacetOption() {
    return new DefaultFacetOptionMatcher();
  }

  public DefaultFacetOptionMatcher withName(String name) {
    this.addMatcher(new PropertyEqualityMatcher<Facet.Option, String>("name", name) {
      @Override
      protected String getItemValue(Facet.Option item) {
        if (item instanceof Facet.DefaultOption) {
          return ((Facet.DefaultOption) item).getName();
        }
        return null;
      }
    });

    return this;
  }
}
