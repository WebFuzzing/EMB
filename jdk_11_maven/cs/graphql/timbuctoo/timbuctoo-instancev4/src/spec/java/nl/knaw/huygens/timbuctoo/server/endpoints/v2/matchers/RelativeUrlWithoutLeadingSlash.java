package nl.knaw.huygens.timbuctoo.server.endpoints.v2.matchers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MatchingDiffResult;
import nl.knaw.huygens.contractdiff.diffresults.MisMatchDiffResult;
import nl.knaw.huygens.contractdiff.jsondiff.Matcher;
import nl.knaw.huygens.contractdiff.jsondiff.Recurser;

import java.net.URI;
import java.net.URISyntaxException;

public class RelativeUrlWithoutLeadingSlash implements Matcher {
  @Override
  public DiffResult match(JsonNode actual, ObjectNode config, Recurser recurser) {
    if (actual.isTextual()) {
      String path = actual.asText();
      if (path.startsWith("/")) {
        return new MisMatchDiffResult("a relative url _without_ a leading /", actual.toString());
      } else {
        try {
          new URI("http", "example.org", path);
          return new MatchingDiffResult("a string containing a relative url without a leading /", actual.toString());
        } catch (URISyntaxException e) {
          return new MisMatchDiffResult("a valid relative url", actual.toString());
        }
      }
    } else {
      return new MisMatchDiffResult("a string containing a relative url without a leading /", actual.toString());
    }

  }
}
