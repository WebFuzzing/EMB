package nl.knaw.huygens.timbuctoo.core.dto;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class QuickSearchTest {
  @Test
  public void fromQueryStringRemovesTheAsteriskAtTheBeginning() {
    QuickSearch quickSearch = QuickSearch.fromQueryString("*test");

    assertThat(quickSearch.partialMatches(), contains("test"));
  }

  @Test
  public void fromQueryStringSplitsTheStringOnWhiteSpace() {
    QuickSearch quickSearch = QuickSearch.fromQueryString("test test2 test3");

    assertThat(quickSearch.fullMatches(), contains("test", "test2"));
    assertThat(quickSearch.partialMatches(), contains("test3"));
  }

  @Test
  public void fromQueryStringRemovesTheAsteriskPostfixOfTheLastWord() {
    QuickSearch quickSearch = QuickSearch.fromQueryString("test test2 test3*");

    assertThat(quickSearch.fullMatches(), contains("test", "test2"));
    assertThat(quickSearch.partialMatches(), contains("test3"));
  }

  @Test
  public void fromQueryStringCreatesAnEmptyQuickSearchWhenTheQueryStringIsNull() {
    QuickSearch quickSearch = QuickSearch.fromQueryString(null);

    assertThat(quickSearch.fullMatches(), is(empty()));
    assertThat(quickSearch.partialMatches(), is(empty()));
  }

}
