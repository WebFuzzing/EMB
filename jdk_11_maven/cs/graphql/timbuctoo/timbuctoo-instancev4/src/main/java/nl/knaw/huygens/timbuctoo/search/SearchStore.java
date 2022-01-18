package nl.knaw.huygens.timbuctoo.search;


import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import nl.knaw.huygens.timbuctoo.util.Timeout;

import java.util.Optional;
import java.util.UUID;

public class SearchStore {

  private final Cache<UUID, SearchResult> cache;

  public SearchStore(Timeout searchResultAvailabilityTime) {
    cache = createCache(searchResultAvailabilityTime);
  }

  private static Cache<UUID, SearchResult> createCache(Timeout timeout) {
    return CacheBuilder.newBuilder().expireAfterAccess(timeout.duration, timeout.timeUnit).build();
  }

  public Optional<SearchResult> getSearchResult(UUID id) {
    return Optional.ofNullable(cache.getIfPresent(id));
  }

  public UUID add(SearchResult searchResult) {
    UUID id = UUID.randomUUID();
    cache.put(id, searchResult);
    searchResult.setId(id);
    return id;
  }
}
