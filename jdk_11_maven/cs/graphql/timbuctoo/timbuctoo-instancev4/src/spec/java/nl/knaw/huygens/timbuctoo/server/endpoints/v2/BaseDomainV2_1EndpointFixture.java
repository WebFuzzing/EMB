package nl.knaw.huygens.timbuctoo.server.endpoints.v2;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.concordion.extensions.ActualResult;
import nl.knaw.huygens.concordion.extensions.ExpectedResult;
import nl.knaw.huygens.concordion.extensions.HttpRequest;
import nl.knaw.huygens.concordion.extensions.ValidationResult;
import nl.knaw.huygens.contractdiff.diffresults.DiffResult;
import nl.knaw.huygens.contractdiff.httpdiff.ExpectedHeadersAreEqualValidator;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.ClassRule;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static nl.knaw.huygens.concordion.extensions.ValidationResult.result;
import static nl.knaw.huygens.util.DropwizardMaker.makeTimbuctoo;

public abstract class BaseDomainV2_1EndpointFixture extends AbstractV2_1EndpointFixture {

  private String recordId;
  private String recordLocation;
  private String pid;
  private String authenticationToken;

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APPLICATION = makeTimbuctoo();

  @Override
  protected WebTarget returnUrlToMockedOrRealServer(String serverAddress) {
    String defaultAddress = String.format("http://localhost:%d", APPLICATION.getLocalPort());
    String address = serverAddress != null ? serverAddress : defaultAddress;

    return ClientBuilder.newClient().target(address);
  }

  private final ObjectMapper objectMapper;

  public BaseDomainV2_1EndpointFixture() {
    this.objectMapper = new ObjectMapper();
  }

  public int getNumberOfItems(ActualResult result) {
    JsonNode jsonNode = getBody(result);
    return Lists.newArrayList(jsonNode.elements()).size();
  }

  private JsonNode getBody(ActualResult result) {
    try {
      return objectMapper.readTree(result.getBody().getBytes());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean doesNotContainResult(ActualResult resultToTest, ActualResult resultToBeContained) {
    List<String> idsToTest = getIds(resultToTest);
    List<String> idsToBeContained = getIds(resultToBeContained);

    return !idsToTest.containsAll(idsToBeContained);
  }

  private List<String> getIds(ActualResult result) {
    JsonNode body = getBody(result);
    ArrayList<String> ids = Lists.newArrayList();
    for (Iterator<JsonNode> elements = body.elements(); elements.hasNext(); ) {
      ids.add(elements.next().get("_id").textValue());
    }

    return ids;
  }

  public boolean recordHasPid() {
    return pid != null && !pid.equalsIgnoreCase("null");
  }

  public String retrievePid(String path)  {
    if (path != null) {
      int attempts = 0;
      HttpRequest getRequest = new HttpRequest("GET", path)
        .withHeader("Accept", "application/json");

      while ((pid == null || pid.equalsIgnoreCase("null")) && attempts < 24) {
        ActualResult response = executeRequestUsingJaxRs(getRequest);
        try {
          JSONObject data = new JSONObject(response.getBody());
          pid = data.getString("^pid");
        } catch (JSONException e) {
          // Expected exception when record is still updating
        }
        attempts++;
        try {
          Thread.sleep(2500);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
      return pid;
    } else {
      return null;
    }
  }

  public String retrievePid()  {
    return retrievePid(recordLocation);
  }


  public ValidationResult validateIdFromLocationHeader(ExpectedResult expectation, ActualResult reality) {
    Tuple<Boolean, String> statusResult = testStatus(expectation, reality);
    String result = statusResult.getRight();

    DiffResult headersResult = ExpectedHeadersAreEqualValidator.validate(expectation.getHeaders(), reality.getMultiHeaders());
    result += headersResult.asHtml();
    result += "\n" + reality.getBody();

    if (statusResult.getLeft() && headersResult.wasSuccess()) {
      String location = reality.getFirstHeader("location").get();
      recordLocation = location;
      recordId = location.replaceAll(".*\\/", "");

      if (!recordId.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$")) {
        return result(false, "<pre>" + result + "\nnot a valid UUID: " + recordId + "</pre>");
      }
      return result(true, "<pre>" + result + "</pre>");
    } else {
      return result(false, "<pre>" + result + "</pre>");
    }
  }

  public ValidationResult validatePostWithEmptyBodyResponse(ExpectedResult expectation, ActualResult reality) {
    Tuple<Boolean, String> statusResult = testStatus(expectation, reality);
    String result = statusResult.getRight();

    DiffResult headersResult = ExpectedHeadersAreEqualValidator.validate(expectation.getHeaders(), reality.getMultiHeaders());
    result += headersResult.asHtml();

    if (!statusResult.getLeft() || !headersResult.wasSuccess()) {
      return result(false, "<pre>" + result + "</pre>");
    } else {
      final String expected = "missing property '@type'";
      return reality.getBody().contains(expected) ?
        result(true, "<pre>" + result + "\n" + reality.getBody() + "</pre>") :
        result(false, "<pre>" + result + "\n" + reality.getBody() + "\nExpected response to contain: '" + expected + "', but got: '" + reality.getBody() + "'" + "</pre>");
    }
  }

  public String getRecordId() {
    return recordId;
  }

  public String getAuthenticationToken() {
    if (authenticationToken != null) {
      return authenticationToken;
    }
    HttpRequest loginRequest = new HttpRequest("POST", "/v2.1/authenticate")
      .withHeader("Authorization", "Basic dXNlcjpwYXNzd29yZA==");

    ActualResult response = executeRequestUsingJaxRs(loginRequest);
    authenticationToken = response.getFirstHeader("x_auth_token").orElse("X_AUTH_TOKEN_NOT_RETURNED");
    return authenticationToken;
  }

  public boolean isValidPid(String result) {
    return !StringUtils.isBlank(result) && !result.equals("null");
  }
}
