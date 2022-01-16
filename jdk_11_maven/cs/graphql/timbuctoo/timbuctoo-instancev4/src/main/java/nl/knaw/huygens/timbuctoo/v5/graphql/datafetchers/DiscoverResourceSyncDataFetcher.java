package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.remote.rs.ResourceSyncService;
import nl.knaw.huygens.timbuctoo.remote.rs.view.Interpreter;
import nl.knaw.huygens.timbuctoo.remote.rs.view.SetItemView;
import nl.knaw.huygens.timbuctoo.remote.rs.view.SetListBase;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class DiscoverResourceSyncDataFetcher implements DataFetcher {
  private ResourceSyncService resourceSyncService;

  public DiscoverResourceSyncDataFetcher(ResourceSyncService resourceSyncService) {
    this.resourceSyncService = resourceSyncService;
  }

  @Override
  public Object get(DataFetchingEnvironment env) {
    String url = env.getArgument("url");
    Boolean debug = env.getArgument("debug");
    String authString = env.getArgument("authorization");

    if (debug == null) {
      debug = false;
    }

    try {
      SetListBase setListBase = resourceSyncService.listSets(url, new Interpreter()
        .withStackTrace(debug), authString);

      List<DiscoverRsResult> discoveryResults = new ArrayList<>();

      List<SetItemView> setDetails = setListBase.getSetDetails();

      for (SetItemView setDetail : setDetails) {
        if (setDetail.getCapability().equals("capabilitylist") && setDetail.getDescribedBy() != null) {
          JsonNode content = setDetail.getDescribedBy().getDescription().getContent();
          for (JsonNode node : content) {
            discoveryResults.add(new DiscoverRsResult(
                setDetail.getLocation(),
                node.get("http://purl.org/dc/terms/description").get(0).get("@value").asText(),
                node.get("http://purl.org/dc/terms/license").get(0).get("@id").asText(),
                node.get("http://purl.org/dc/terms/title").get(0).get("@value").asText()
              )
            );
          }
        } else { //in case description is not available (Eg. for certain external resource sync)
          discoveryResults.add(new DiscoverRsResult(setDetail.getLocation(), "N/A", "N/A", "N/A"));
        }
      }

      return discoveryResults;

    } catch (URISyntaxException e) {
      String errorMessage = String.format("Url '%s' is not valid.", url);
      throw new RuntimeException(errorMessage, e);
    } catch (InterruptedException e) {
      throw new RuntimeException("Cannot list sets of url", e);
    }
  }

  public class DiscoverRsResult {
    private String location;
    private String description;
    private String license;
    private String title;

    public DiscoverRsResult(String location, String description, String license, String title) {
      this.location = location;
      this.description = description;
      this.license = license;
      this.title = title;
    }


    public String getLocation() {
      return location;
    }

    public String getDescription() {
      return description;
    }

    public String getLicense() {
      return license;
    }

    public String getTitle() {
      return title;
    }
  }
}
