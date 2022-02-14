package nl.knaw.huygens.timbuctoo.server.endpoints.v2;


import com.google.common.collect.Lists;
import nl.knaw.huygens.concordion.extensions.ActualResult;
import nl.knaw.huygens.concordion.extensions.HttpRequest;
import org.concordion.integration.junit4.ConcordionRunner;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.runner.RunWith;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(ConcordionRunner.class)
public class WwRelationV2_1EndpointFixture extends BaseDomainV2_1EndpointFixture {

  private String personPath;
  private String personId;

  public String getPersonPath() {
    return personPath;
  }

  public String getDocumentPath() {
    return documentPath;
  }

  private String documentId;
  private String documentPath;

  public String makeDocumentRecord() throws JSONException {
    HttpRequest postRequest = new HttpRequest("POST", "/v2.1/domain/wwdocuments", makeDocumentJson())
      .withHeaders(makeAuthHeaders());

    ActualResult response = executeRequestUsingJaxRs(postRequest);
    documentPath = response.getFirstHeader("Location")
      .orElseThrow(() -> new RuntimeException("Location header not present"))
      .replaceAll("http://[^/]+/", "");
    documentId = documentPath.replaceAll(".*\\/", "");
    retrievePid(documentPath);
    return documentId;
  }

  public String makePersonRecord() throws JSONException {
    HttpRequest postRequest = new HttpRequest("POST", "/v2.1/domain/wwpersons", makePersonJson())
          .withHeaders(makeAuthHeaders());

    ActualResult response = executeRequestUsingJaxRs(postRequest);
    personPath = response.getFirstHeader("Location")
      .orElseThrow(() -> new RuntimeException("Location header not present"))
      .replaceAll("http://[^/]+/", "");
    personId = personPath.replaceAll(".*\\/", "");
    retrievePid(personPath);
    return personId;
  }

  public String getRelationId() throws JSONException {
    HttpRequest request = new HttpRequest("POST", "/v2.1/gremlin", "g.V().has(\"relationtype_regularName\", \"isCreatedBy\")")
      .withHeader("accept", "text/plain")
      .withHeader("content-type", "text/plain");


    ActualResult response = executeRequestUsingJaxRs(request);
    Pattern pattern = Pattern.compile(".*tim_id: \"([^\n]*)\".*", Pattern.DOTALL);

    Matcher matcher = pattern.matcher(response.getBody());
    matcher.matches();
    return matcher.group(1);
  }

  public void deleteEntities() {
    executeRequestUsingJaxRs(new HttpRequest("DELETE", personPath).withHeaders(makeAuthHeaders()));
    executeRequestUsingJaxRs(new HttpRequest("DELETE", documentPath).withHeaders(makeAuthHeaders()));
  }

  private String makeDocumentJson() throws JSONException {
    JSONObject documentObject = new JSONObject();
    documentObject.put("@type", "wwdocument");
    documentObject.put("title", "A title");
    return documentObject.toString();
  }

  private String makePersonJson() throws JSONException {
    JSONObject personObject = new JSONObject();
    JSONArray types = new JSONArray("[\"AUTHOR\"]");
    JSONArray names = new JSONArray("[{\"components\": [{\"type\": \"FORENAME\", \"value\": \"name\"}]}]");
    personObject.put("@type", "wwperson");
    personObject.put("gender", "MALE");
    personObject.put("birthDate", "1589");
    personObject.put("deathDate", "1653");
    personObject.put("types", types);
    personObject.put("names", names);
    return personObject.toString();
  }


  private List<Map.Entry<String, String>> makeAuthHeaders() {
    List<Map.Entry<String, String>> headers = Lists.newArrayList();
    headers.add(new AbstractMap.SimpleEntry<>("Authorization",  getAuthenticationToken()));
    headers.add(new AbstractMap.SimpleEntry<>("Content-type",  "application/json"));
    headers.add(new AbstractMap.SimpleEntry<>("VRE_ID",  "WomenWriters"));
    return headers;
  }


}
