package nl.knaw.huygens.timbuctoo.search;

import com.fasterxml.jackson.databind.JsonNode;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearchResult;
import nl.knaw.huygens.timbuctoo.crud.InvalidCollectionException;
import nl.knaw.huygens.timbuctoo.crud.UrlGenerator;
import nl.knaw.huygens.timbuctoo.core.TimbuctooActions;
import nl.knaw.huygens.timbuctoo.core.dto.QuickSearch;
import nl.knaw.huygens.timbuctoo.core.dto.ReadEntity;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.Collection;

import java.util.List;
import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;

public class AutocompleteService {
  private final UrlGenerator autoCompleteUrlFor;
  private final TimbuctooActions timbuctooActions;

  public AutocompleteService(UrlGenerator autoCompleteUrlFor, TimbuctooActions timbuctooActions) {
    this.timbuctooActions = timbuctooActions;
    this.autoCompleteUrlFor = autoCompleteUrlFor;
  }

  public JsonNode search(String collectionName, Optional<String> query, Optional<String> keywordType)
    throws InvalidCollectionException {

    final Collection collection = timbuctooActions.getCollectionMetadata(collectionName);

    int limit = query.isPresent() ? 50 : 1000;
    String queryString = query.orElse(null);
    QuickSearch quickSearch = QuickSearch.fromQueryString(queryString);
    List<QuickSearchResult> results = timbuctooActions.doQuickSearch(
      collection,
      quickSearch,
      keywordType.orElse(null),
      limit
    );

    return jsnA(results.stream().map(entity -> jsnO(
      "value", jsn(entity.getIndexedValue()),
      "key", jsn(autoCompleteUrlFor.apply(collectionName, entity.getId(), entity.getRev()).toString())
    )));
  }

  public static class AutocompleteServiceFactory {
    private final UrlGenerator autoCompleteUri;

    public AutocompleteServiceFactory(UrlGenerator autoCompleteUri) {
      this.autoCompleteUri = autoCompleteUri;
    }

    public AutocompleteService create(TimbuctooActions timbuctooActions) {
      return new AutocompleteService(autoCompleteUri, timbuctooActions);
    }
  }
}
