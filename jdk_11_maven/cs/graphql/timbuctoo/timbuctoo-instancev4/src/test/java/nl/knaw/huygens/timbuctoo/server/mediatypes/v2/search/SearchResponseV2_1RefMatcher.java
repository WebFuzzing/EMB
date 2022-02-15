package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;

public class SearchResponseV2_1RefMatcher extends CompositeMatcher<SearchResponseV2_1Ref> {
  private SearchResponseV2_1RefMatcher() {

  }

  public static SearchResponseV2_1RefMatcher likeSearchResponseRef() {
    return new SearchResponseV2_1RefMatcher();
  }

  public SearchResponseV2_1RefMatcher withType(String type) {
    this.addMatcher(new PropertyEqualityMatcher<SearchResponseV2_1Ref, String>("type", type) {

      @Override
      protected String getItemValue(SearchResponseV2_1Ref item) {
        return item.getType();
      }
    });
    return this;
  }

  public SearchResponseV2_1RefMatcher withId(String id) {
    this.addMatcher(new PropertyEqualityMatcher<SearchResponseV2_1Ref, String>("id", id) {
      @Override
      protected String getItemValue(SearchResponseV2_1Ref item) {
        return item.getId();
      }
    });

    return this;
  }

  public SearchResponseV2_1RefMatcher withPath(String path) {
    this.addMatcher(new PropertyEqualityMatcher<SearchResponseV2_1Ref, String>("path", path) {
      @Override
      protected String getItemValue(SearchResponseV2_1Ref item) {
        return item.getPath();
      }
    });
    return this;
  }

  public SearchResponseV2_1RefMatcher withDisplayName(String displayName) {
    this.addMatcher(new PropertyEqualityMatcher<SearchResponseV2_1Ref, String>("displayName", displayName) {
      @Override
      protected String getItemValue(SearchResponseV2_1Ref item) {
        return item.getDisplayName();
      }
    });
    return this;
  }

  public SearchResponseV2_1RefMatcher withData(Object data) {
    this.addMatcher(new PropertyEqualityMatcher<SearchResponseV2_1Ref, Object>("data", data) {
      @Override
      protected Object getItemValue(SearchResponseV2_1Ref item) {
        return item.getData();
      }
    });
    return this;
  }
}
