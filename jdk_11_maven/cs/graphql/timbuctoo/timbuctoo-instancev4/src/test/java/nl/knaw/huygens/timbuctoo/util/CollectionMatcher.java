package nl.knaw.huygens.timbuctoo.util;

import nl.knaw.huygens.hamcrest.CompositeMatcher;
import nl.knaw.huygens.hamcrest.PropertyEqualityMatcher;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;

public class CollectionMatcher extends CompositeMatcher<Collection> {

  private CollectionMatcher() {

  }

  public static CollectionMatcher likeCollection() {
    return new CollectionMatcher();
  }

  public CollectionMatcher withCollectionName(String collectionName) {
    this.addMatcher(new PropertyEqualityMatcher<Collection, String>("collectionName", collectionName) {
      @Override
      protected String getItemValue(Collection item) {
        return item.getCollectionName();
      }
    });
    return this;
  }
}
