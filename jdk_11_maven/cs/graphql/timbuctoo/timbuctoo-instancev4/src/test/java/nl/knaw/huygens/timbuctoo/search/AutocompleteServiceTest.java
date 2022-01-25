package nl.knaw.huygens.timbuctoo.search;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.TimbuctooActions;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearch;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearchResult;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.crud.UrlGenerator;
import org.junit.Test;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionStubs.collWithCollectionName;
import static nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionStubs.keywordCollWithCollectionName;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;
import static org.mockito.hamcrest.MockitoHamcrest.intThat;
import static uk.co.datumedge.hamcrest.json.SameJSONAs.sameJSONAs;

public class AutocompleteServiceTest {

  @Test(expected = InvalidCollectionException.class)
  public void searchThrowsWhenTheCollectionNameDoesNotExist() throws InvalidCollectionException {
    TimbuctooActions timbuctooActions = mock(TimbuctooActions.class);
    given(timbuctooActions.getCollectionMetadata(anyString())).willThrow(new InvalidCollectionException(""));
    AutocompleteService underTest = new AutocompleteService(null, timbuctooActions);
    underTest.search("nonexistent", Optional.empty(), Optional.empty());
  }

  @Test
  public void searchConvertsTheReadEntityToJson() throws Exception {
    UUID id = UUID.randomUUID();
    String collectionName = "wwpersons";
    QuickSearchResult entity = QuickSearchResult.create("[TEMP] An author", id, 2);
    TimbuctooActions timbuctooActions = mock(TimbuctooActions.class);
    given(timbuctooActions.getCollectionMetadata(anyString())).willReturn(collWithCollectionName(collectionName));
    given(timbuctooActions.doQuickSearch(any(), any(), isNull(), anyInt())).willReturn(Lists.newArrayList(entity));
    AutocompleteService instance = new AutocompleteService(
      (collection, id1, rev) -> URI.create("http://example.com/" + collection + "/" + id1 + "?rev=" + rev),
      timbuctooActions
    );

    String query = "*author*";
    JsonNode result = instance.search(collectionName, Optional.of(query), Optional.empty());

    assertThat(result.toString(), sameJSONAs(jsnA(
      jsnO("value", jsn("[TEMP] An author"), "key", jsn("http://example.com/wwpersons/" + id.toString() + "?rev=2"))
    ).toString()));
    verify(timbuctooActions).doQuickSearch(
      argThat(hasProperty("collectionName", equalTo(collectionName))),
      any(QuickSearch.class),
      isNull(),
      intThat(is(50))
    );
  }

  @Test
  public void searchFiltersKeywordsByType() throws InvalidCollectionException {
    String query = "*foo bar*";
    String keywordType = "maritalStatus";
    String collectionName = "wwkeywords";
    UUID id = UUID.randomUUID();
    QuickSearchResult readEntity = QuickSearchResult.create("a keyword", id, 2);
    TimbuctooActions timbuctooActions = mock(TimbuctooActions.class);
    given(timbuctooActions.getCollectionMetadata(anyString()))
      .willReturn(keywordCollWithCollectionName(collectionName));
    given(timbuctooActions.doQuickSearch(any(), any(), anyString(), anyInt()))
      .willReturn(Lists.newArrayList(readEntity));
    UrlGenerator urlGenerator =
      (coll, id1, rev) -> URI.create("http://example.com/" + coll + "/" + id1 + "?rev=" + rev);
    AutocompleteService instance = new AutocompleteService(
      urlGenerator,
      timbuctooActions);

    JsonNode result = instance.search(collectionName, Optional.of(query), Optional.of(keywordType));

    assertThat(result.toString(), sameJSONAs(jsnA(
      jsnO("value", jsn("a keyword"), "key", jsn("http://example.com/wwkeywords/" + id.toString() + "?rev=2"))
    ).toString()));
    verify(timbuctooActions).doQuickSearch(
      argThat(hasProperty("collectionName", equalTo(collectionName))),
      any(QuickSearch.class),
      argThat(is(keywordType)),
      intThat(is(50))
    );
  }

  @Test
  public void searchRequests1000ResultsWhenTheQueryIsEmpty() throws Exception {
    UUID id = UUID.randomUUID();
    String collectionName = "wwpersons";
    QuickSearchResult entity = QuickSearchResult.create("[TEMP] An author", id, 2);
    TimbuctooActions timbuctooActions = mock(TimbuctooActions.class);
    given(timbuctooActions.getCollectionMetadata(anyString())).willReturn(collWithCollectionName(collectionName));
    given(timbuctooActions.doQuickSearch(any(), any(), anyString(), anyInt())).willReturn(Lists.newArrayList(entity));
    AutocompleteService instance = new AutocompleteService(
      (collection, id1, rev) -> URI.create("http://example.com/" + collection + "/" + id1 + "?rev=" + rev),
      timbuctooActions
    );

    instance.search(collectionName, Optional.empty(), Optional.empty());

    verify(timbuctooActions).doQuickSearch(
      any(Collection.class),
      any(QuickSearch.class),
      any(),
      intThat(is(1000))
    );
  }

}
