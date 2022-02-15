package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.concordion.extensions.ActualResult;
import nl.knaw.huygens.concordion.extensions.HttpRequest;
import nl.knaw.huygens.contractdiff.diffresults.MissingPropertyDiffResult;
import nl.knaw.huygens.contractdiff.jsondiff.ArrayDiffResult;
import nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import nl.knaw.huygens.timbuctoo.server.endpoints.v2.matchers.NumericDateWithoutDashes;
import nl.knaw.huygens.timbuctoo.server.endpoints.v2.matchers.RelativeUrlWithoutLeadingSlash;
import org.apache.commons.lang3.StringUtils;
import org.concordion.api.FullOGNL;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.jsonDiffer;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static nl.knaw.huygens.util.DropwizardMaker.makeTimbuctoo;

@FullOGNL
@RunWith(ConcordionRunner.class)
public class RelationSearchV2_1EndpointFixture extends AbstractV2_1EndpointFixture {

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APPLICATION = makeTimbuctoo();

  public String getPersonSearchId() {
    HttpRequest postRequest = new HttpRequest("POST", "/v2.1/search/wwpersons", "{}")
      .withHeader("Content-type",  "application/json")
      .withHeader("VRE_ID",  "WomenWriters");

    ActualResult response = executeRequestUsingJaxRs(postRequest);
    return response.getFirstHeader("Location")
      .map(l -> l.replaceAll("http://[^/]+/", ""))
      .map(l -> l.replaceAll(".*\\/", ""))
      .orElse("");
  }

  public String isFullyQualified(String url) {

    if (StringUtils.isBlank(url) || (!url.startsWith("http://") && !url.startsWith("https://"))) {
      return "not a fully qualified HTTP url";
    } else {
      return "a fully qualified HTTP url";
    }
  }

  @Override
  protected JsonDiffer makeJsonDiffer() {
    return jsonDiffer()
      .handleArraysWith(
        "ALL_MATCH_ONE_OF",
        expectationVal -> {
          if (expectationVal.size() > 1) {
            ObjectNode expectation = jsnO();
            for (int i = 0; i < expectationVal.size(); i++) {
              if (expectationVal.get(i).has("type")) {
                expectation.set(expectationVal.get(i).get("type").asText(), expectationVal.get(i));
              } else {
                throw new RuntimeException("Expectation value has no property 'type': " + expectationVal);
              }
            }
            return jsnO(
              "possibilities", expectation,
              "keyProp", jsn("type")
            );
          } else {
            return jsnO(
              "invariant", expectationVal.get(0)
            );
          }
        })
      .withCustomHandler("RELATIVE_URL_WITHOUT_LEADING_SLASH", new RelativeUrlWithoutLeadingSlash())
      .withCustomHandler("NUMERIC_DATE_WITHOUT_DASHES", new NumericDateWithoutDashes())
      .withCustomHandler("IS_SAME_ARRAY", (actual, config, recurser) -> {
        ArrayDiffResult result = new ArrayDiffResult();
        JsonNode expectation = config.get("array");
        for (int i = 0; i < actual.size(); i++) {
          result.add(i, recurser.recurser(actual.get(i), expectation.get(i)));
        }
        if (expectation.size() > actual.size()) {
          for (int i = actual.size() - 1; i < expectation.size(); i++) {
            result.add(i, new MissingPropertyDiffResult(expectation.get(i).toString()));
          }
        }
        return result;
      })
      .build();

  }

  @Override
  protected WebTarget returnUrlToMockedOrRealServer(String serverAddress) {
    String defaultAddress = String.format("http://localhost:%d", APPLICATION.getLocalPort());
    String address = serverAddress != null ? serverAddress : defaultAddress;

    return ClientBuilder.newClient().target(address);
  }

}
