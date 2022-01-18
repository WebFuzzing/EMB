package nl.knaw.huygens.timbuctoo.server.endpoints.v2;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import org.concordion.integration.junit4.ConcordionRunner;
import org.junit.ClassRule;
import org.junit.runner.RunWith;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.UUID;

import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.assertThat;
import static nl.knaw.huygens.contractdiff.jsondiff.JsonDiffer.jsonDiffer;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static nl.knaw.huygens.util.DropwizardMaker.makeTimbuctoo;

@RunWith(ConcordionRunner.class)
public class MetadataFixture extends AbstractV2_1EndpointFixture {

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APPLICATION = makeTimbuctoo();

  private static ArrayList<String> validNameComponents;

  public MetadataFixture() {
    validNameComponents = Lists.newArrayList("FORENAME", "SURNAME", "NAME_LINK", "ROLE_NAME", "GEN_NAME");
  }

  @Override
  protected WebTarget returnUrlToMockedOrRealServer(String serverAddress) {
    String defaultAddress = String.format("http://localhost:%d", APPLICATION.getLocalPort());
    String address = serverAddress != null ? serverAddress : defaultAddress;

    return ClientBuilder.newClient().target(address);
  }

  @Override
  protected JsonDiffer makeJsonDiffer() {
    return jsonDiffer()
      .handleArraysWith(
        "ALL_MATCH_ONE_OF",
          expectationVal -> {
            if (expectationVal.get(0).isObject()) {
              ObjectNode expectation = jsnO();
              for (int i = 0; i < expectationVal.size(); i++) {
                expectation.set(expectationVal.get(i).get("type").asText(), expectationVal.get(i));
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
      .withCustomHandler("RELATIVE_URL", n -> assertThat(n.asText().startsWith("/"), "a url without a hostname", n))
      .withCustomHandler("IN_OR_OUT_OR_BOTH", n -> assertThat(n.asText().equals("IN") || n.asText().equals("OUT") || n.asText().equals("BOTH"), "\"IN\" or \"OUT\" or \"BOTH\"", n))
      .withCustomHandler("NAME_COMPONENT", n -> assertThat(isNameComponent(n), "One of " + String.join(",", validNameComponents), n))
      .withCustomHandler(
        "STRING_STARTING_WITH_WW_ENDING_WITH_S",
        n -> assertThat(
          n.asText().startsWith("ww") && n.asText().endsWith("s"),
          "a collection (starting with ww, ending with an s)",
          n
        )
      )
      .withCustomHandler("UUID", n -> assertThat(isUuid(n), "a valid UUID", n))
      .build();
  }

  private static boolean isUuid(JsonNode value) {
    try {
      UUID.fromString(value.asText());
      return true;
    } catch (Exception e) {
      return false;
    }
  }


  private static boolean isNameComponent(JsonNode value) {
    return validNameComponents.contains(value.asText());
  }

  //
  //@Override
  //public ValidationResult validateBody(ExpectedResult expectation, ActualResult reality) {
  //  if (expectation.getBody() == null) {
  //    return result(true, reality.getBody());
  //  }
  //
  //  return validate(expectation.getBody(), reality.getBody());
  //}
  //
  //private ValidationResult validate(String expectationBody, String realityBody) {
  //  try {
  //    JSONComparator comparator = new DefaultComparator(JSONCompareMode.LENIENT);
  //    Customization customization = new Customization("wwpersons", new OmissionsAllowedArrayMatcher<>(comparator));
  //    CustomComparator customComparator = new CustomComparator(JSONCompareMode.LENIENT, customization);
  //
  //    JSONCompareResult jsonCompareResult = JSONCompare.compareJSON(expectationBody, realityBody, customComparator);
  //
  //    return result(Strings.isBlank(jsonCompareResult.getMessage()), jsonCompareResult.getMessage());
  //  } catch (AssertionError e) {
  //    return result(false, e.getMessage());
  //  } catch (JSONException e) {
  //    throw new RuntimeException(e);
  //  }
  //}

}
