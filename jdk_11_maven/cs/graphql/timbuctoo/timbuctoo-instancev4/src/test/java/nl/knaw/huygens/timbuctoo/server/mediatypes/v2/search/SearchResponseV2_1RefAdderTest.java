package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.search.EntityRef;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class SearchResponseV2_1RefAdderTest {

  @Test
  public void addsASearchResponseRefToTheSearchResult() {
    SearchResponseV2_1RefAdder instance = new SearchResponseV2_1RefAdder();

    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();

    EntityRef entityRef = new EntityRef("type", "id");
    entityRef.setDisplayName("displayName");

    instance.addRef(searchResponse, entityRef);

    assertThat(searchResponse.getRefs(), Matchers.contains(
      SearchResponseV2_1RefMatcher.likeSearchResponseRef()
        .withId("id")
        .withType("type")
        .withDisplayName("displayName")
        .withPath("domain/types/id")));
  }

  @Test
  public void addsTheDataOfTheEntityRefToTheSearchResponseV2_1Ref() {
    SearchResponseV2_1RefAdder instance = new SearchResponseV2_1RefAdder();

    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();

    EntityRef entityRef = new EntityRef("type", "id");
    HashMap<String, Object> data = Maps.newHashMap();
    entityRef.setData(data);

    instance.addRef(searchResponse, entityRef);

    assertThat(searchResponse.getRefs(), Matchers.contains(SearchResponseV2_1RefMatcher.likeSearchResponseRef()
      .withData(data)));
  }
}
