package nl.knaw.huygens.timbuctoo.search;

import nl.knaw.huygens.timbuctoo.util.Timeout;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static nl.knaw.huygens.hamcrest.OptionalPresentMatcher.present;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;

public class SearchStoreTest {

  public static final SearchResult OTHER_SEARCH_RESULT1 = new SearchResult(null, null, null);
  private static final Timeout ONE_SECOND_TIMEOUT = new Timeout(1, TimeUnit.SECONDS);
  public SearchResult searchResult;
  private SearchStore instance;

  @Before
  public void setUp() throws Exception {
    instance = new SearchStore(ONE_SECOND_TIMEOUT);
    searchResult = new SearchResult(null, null, null);
  }

  @Test
  public void addReturnsAnId() {
    UUID id = instance.add(searchResult);

    assertThat(id, is(notNullValue()));
  }

  @Test
  public void addCreatesADifferentIdEachCall() {
    UUID id1 = instance.add(searchResult);
    UUID id2 = instance.add(searchResult);

    assertThat(id1, is(not(id2)));
  }

  @Test
  public void addSetsTheIdToTheSearchResult() {
    UUID id = instance.add(searchResult);

    assertThat(searchResult, hasProperty("id", equalTo(id)));
  }

  @Test
  public void getSearchResultReturnsASearchResult() {
    UUID id = instance.add(searchResult);

    Optional<SearchResult> searchResult = instance.getSearchResult(id);

    assertThat(searchResult, is(present()));
  }

  @Test
  public void getSearchResultReturnsTheSameResultForTheSameId() {
    UUID id = instance.add(searchResult);

    SearchResult retrieval1 = instance.getSearchResult(id).get();
    SearchResult retrieval2 = instance.getSearchResult(id).get();

    assertThat(retrieval1, is(retrieval2));
  }

  @Test
  public void getSearchResultReturnsADifferentResultForDifferentIds() {
    UUID id1 = instance.add(searchResult);
    UUID id2 = instance.add(OTHER_SEARCH_RESULT1);

    SearchResult searchResult1 = instance.getSearchResult(id1).get();
    SearchResult searchResult2 = instance.getSearchResult(id2).get();

    assertThat(searchResult1, is(not(searchResult2)));
  }

  @Test
  public void getSearchResultReturnsNullIfTheSearchResultIsNotFound() {
    Optional<SearchResult> searchResult = instance.getSearchResult(UUID.randomUUID());

    assertThat(searchResult, is(not(present())));
  }

  @Test
  public void getSearchResultReturnsNullIfTheSearchAfterTheTimeOut() throws InterruptedException {
    UUID id = instance.add(searchResult);

    Optional<SearchResult> searchResult = instance.getSearchResult(id);
    assertThat(searchResult, is(present()));

    Thread.sleep(ONE_SECOND_TIMEOUT.toMilliseconds() + 101);

    Optional<SearchResult> searchResultAfterTimeout = instance.getSearchResult(id);
    assertThat(searchResultAfterTimeout, is(not(present())));
  }

}
