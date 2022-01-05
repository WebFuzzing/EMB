package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import com.codahale.metrics.annotation.Timed;
import com.google.common.base.Stopwatch;
import io.dropwizard.jersey.params.UUIDParam;
import nl.knaw.huygens.timbuctoo.search.SearchDescription;
import nl.knaw.huygens.timbuctoo.search.SearchResult;
import nl.knaw.huygens.timbuctoo.search.SearchStore;
import nl.knaw.huygens.timbuctoo.search.description.SearchDescriptionFactory;
import nl.knaw.huygens.timbuctoo.server.GraphWrapper;
import nl.knaw.huygens.timbuctoo.server.SearchConfig;
import nl.knaw.huygens.timbuctoo.util.UriHelper;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.SearchRequestV2_1;
import nl.knaw.huygens.timbuctoo.server.mediatypes.v2.search.SearchResponseV2_1Factory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.Optional;
import java.util.UUID;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/v2.1/search")
@Produces(APPLICATION_JSON)
public class Search {

  public static final Logger LOG = LoggerFactory.getLogger(Search.class);
  private final SearchResponseV2_1Factory searchResponseFactory;
  private final SearchDescriptionFactory searchDescriptionFactory;
  private final GraphWrapper graphWrapper;
  private SearchStore searchStore;

  public Search(SearchConfig searchConfig, UriHelper uriHelper, GraphWrapper graphWrapper) {
    this.searchStore = new SearchStore(searchConfig.getSearchResultAvailabilityTimeout());
    this.graphWrapper = graphWrapper;
    this.searchResponseFactory = new SearchResponseV2_1Factory(uriHelper);
    searchDescriptionFactory = new SearchDescriptionFactory();

  }

  @POST
  @Path("wwrelations/wwdocuments")
  public Response receptionSearch(SearchRequestV2_1 searchRequest) {
    Optional<SearchResult> otherSearch = searchStore.getSearchResult(
            UUID.fromString(searchRequest.getOtherSearchId()));

    LOG.info("Using other search ID: {}", searchRequest.getOtherSearchId());

    if (otherSearch.isPresent()) {
      LOG.info("other search is present");
      UUID uuid = searchStore.add(getDescription("wwrelations", otherSearch.get())
              .get().execute(graphWrapper, searchRequest));
      URI uri = createUri(uuid);

      return Response.created(uri).build();
    }

    return Response.status(Response.Status.BAD_REQUEST)
            .entity(new NotFoundMessage(new UUIDParam(searchRequest.getOtherSearchId()))).build();
  }

  @Timed
  @POST
  @Path("{entityName: [a-z]+}s")
  public Response post(@PathParam("entityName") String entityName, SearchRequestV2_1 searchRequest) {
    Stopwatch stopwatch = Stopwatch.createStarted();
    Optional<SearchDescription> description = getDescription(entityName);

    if (description.isPresent()) {
      /*
       * The previous implementation of the faceted search v2.1 did validate the search request. For reasons of time
       * this feature is omitted, until proven necessary. Execute will just ignore unknown facets, full text search
       * fields and sort fields.
       */
      UUID uuid = searchStore.add(description.get().execute(graphWrapper, searchRequest));

      URI uri = createUri(uuid);
      LOG.info("Duration of search request: {}", stopwatch);
      return Response.created(uri).build();
    }

    return Response.status(Response.Status.BAD_REQUEST).entity(new InvalidCollectionMessage(entityName)).build();
  }

  private URI createUri(UUID uuid) {
    return UriBuilder.fromResource(Search.class).path("{id}").build(uuid);
  }

  @GET
  @Path("{id}")
  /*
   * Use UUIDParam instead of UUID, because we want to be explicit to the user of this API the request is not
   * supported. When UUID is used Jersery returns a '404 Not Found' if the request contains a malformed one. The
   * UUIDParam will return a '400 Bad Request' for malformed UUID's.
   * See: http://www.dropwizard.io/0.9.1/dropwizard-jersey/apidocs/io/dropwizard/jersey/params/AbstractParam.html
   * Or: http://codahale.com/what-makes-jersey-interesting-parameter-classes/
   */
  public Response get(@PathParam("id") UUIDParam id,
                      @QueryParam("rows") @DefaultValue("10") int rows,
                      @QueryParam("start") @DefaultValue("0") int start) {
    Optional<SearchResult> searchResult = searchStore.getSearchResult(id.get());
    if (searchResult.isPresent()) {
      return Response.ok(searchResponseFactory.createResponse(searchResult.get(), rows, start)).build();
    }

    return Response
      .status(Response.Status.NOT_FOUND)
      .entity(new NotFoundMessage(id))
      .build();
  }

  private Optional<SearchDescription> getDescription(String entityName) {
    return getDescription(entityName, null);
  }

  private Optional<SearchDescription> getDescription(String entityName, SearchResult otherSearch) {
    return searchDescriptionFactory.create(entityName, otherSearch);
  }

  private class NotFoundMessage {
    public final String message;

    public NotFoundMessage(UUIDParam id) {
      message = String.format("No SearchResult with id '%s'", id.get());
    }
  }

  private class InvalidCollectionMessage {
    public final String message;

    public InvalidCollectionMessage(String entityName) {
      message = String.format("'%s' is not a valid collection", entityName);
    }
  }
}
