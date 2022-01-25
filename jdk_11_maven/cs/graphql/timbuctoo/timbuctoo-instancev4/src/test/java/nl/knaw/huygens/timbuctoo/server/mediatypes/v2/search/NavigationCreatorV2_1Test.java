package nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search;

import nl.knaw.huygens.timbuctoo.util.UriHelper;
import org.junit.Before;
import org.junit.Test;

import java.net.URI;
import java.util.UUID;

import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;

public class NavigationCreatorV2_1Test {

  public static final String BASE_URI = "http://example.com";
  private NavigationCreator instance;

  @Before
  public void setUp() throws Exception {
    UriHelper uriHelper = new UriHelper(URI.create(BASE_URI));
    instance = new NavigationCreator(uriHelper);
  }

  @Test
  public void nextAddsANextLinkToTheSearchResult() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.next(searchResponse, 10, 0, 20, id);

    assertThat(searchResponse,
      hasProperty("next", endsWith("/v2.1/search/17baf00d-0089-4446-a9bd-2a4add3e55ea?start=10&rows=10")));
  }

  @Test
  public void nextAddsNoNextLinkWhenTheCurrentStartAndRowsIsLargerThanNumFound() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.next(searchResponse, 10, 0, 8, id);

    assertThat(searchResponse, hasProperty("next", is(nullValue())));
  }

  @Test
  public void nextAddsNoNextLinkWhenTheCurrentStartAndRowsIsEqualToNumFound() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.next(searchResponse, 10, 0, 10, id);

    assertThat(searchResponse, hasProperty("next", is(nullValue())));
  }

  @Test
  public void nextAddsANextLinkThatStartsWithTheBaseUriFromTheSearchConfig() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.next(searchResponse, 10, 0, 20, id);

    assertThat(searchResponse, hasProperty("next", startsWith(BASE_URI)));
  }

  @Test
  public void prevAddsAPrevLinkToTheSearchResult() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.prev(searchResponse, 10, 10, 20, id);

    assertThat(searchResponse,
      hasProperty("prev", endsWith("/v2.1/search/17baf00d-0089-4446-a9bd-2a4add3e55ea?start=0&rows=10")));
  }

  @Test
  public void prevAddsAPrevLinkThatStartsWithTheBaseUriFromTheSearchConfig() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.prev(searchResponse, 10, 10, 20, id);

    assertThat(searchResponse, hasProperty("prev", startsWith(BASE_URI)));
  }

  @Test
  public void prevDoesNotAddALinkWhenTheCurrentStartIsZero() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.prev(searchResponse, 10, 0, 20, id);

    assertThat(searchResponse, hasProperty("prev", is(nullValue())));
  }

  @Test
  public void prevSetTheStartToZeroWhenThePrevStartWillBeLessThanZero() {
    SearchResponseV2_1 searchResponse = new SearchResponseV2_1();
    UUID id = UUID.fromString("17baf00d-0089-4446-a9bd-2a4add3e55ea");

    instance.prev(searchResponse, 10, 7, 20, id);

    assertThat(searchResponse,
      hasProperty("prev", endsWith("/v2.1/search/17baf00d-0089-4446-a9bd-2a4add3e55ea?start=0&rows=10")));
  }
}
