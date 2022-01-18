package nl.knaw.huygens.timbuctoo.v5;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import io.dropwizard.testing.junit.DropwizardAppRule;
import nl.knaw.huygens.timbuctoo.CleaningDropwizard;
import nl.knaw.huygens.timbuctoo.server.TimbuctooConfiguration;
import nl.knaw.huygens.timbuctoo.util.EvilEnvironmentVariableHacker;
import org.apache.activemq.util.ByteArrayInputStream;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.io.Resources.getResource;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsn;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnA;
import static nl.knaw.huygens.timbuctoo.util.JsonBuilder.jsnO;
import static nl.knaw.huygens.timbuctoo.util.StreamIterator.stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.hasXPath;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.Is.is;

public class IntegrationTest {

  @ClassRule
  public static final DropwizardAppRule<TimbuctooConfiguration> APP = new CleaningDropwizard(
    "example_config.yaml",
    Paths.get(resourceFilePath("integrationtest"), "datasets")
  );
  private static final String AUTH = "FAKE_AUTH_TOKEN";
  private static final String USER_ID = "33707283d426f900d4d33707283d426f900d4d0d";
  private static Client client;
  private static String PREFIX = "u" + USER_ID;

  static {
    EvilEnvironmentVariableHacker.setEnv(
      "http://localhost",
      "9200",
      "elastic",
      "changeme",
      "http://127.0.0.1:0",
      resourceFilePath("integrationtest"),
      resourceFilePath("integrationtest"),
      "0",
      "0"
    );
  }

  @BeforeClass
  public static void beforeClass() throws IOException {
    ClientConfig configuration = new ClientConfig();
    client = ClientBuilder.newClient(configuration);
  }

  @Test
  public void succeedingRdfUploadWithGraphql() throws Exception {
    String vreName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + vreName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));
    uploadResponse.readEntity(String.class);

    Response graphqlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(String.format("{\n" +
        "  dataSets {\n" +
        "    %1s__%2s {\n" +
        "      clusius_ResidenceList {\n" +
        "        items {\n" +
        "          uri\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}", PREFIX, vreName), MediaType.valueOf("application/graphql")));
    ObjectNode objectNode = graphqlCall.readEntity(ObjectNode.class);
    assertThat(
      objectNode
        .get("data")
        .get("dataSets")
        .get(createDataSetId(vreName))
        .get("clusius_ResidenceList")
        .get("items").size(),
      is(20)
    );

    graphqlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(String.format("{\n" +
        "  dataSets {\n" +
        "    %1s__%2s {\n" +
        "      clusius_ResidenceList {\n" +
        "        items {\n" +
        "          tim_hasLocation {\n" +
        "            tim_name {value}\n" +
        "            _inverse_tim_hasBirthPlaceList {\n" +
        "              items {\n" +
        "                tim_gender {value}\n" +
        "              }\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}", PREFIX, vreName), MediaType.valueOf("application/graphql")));
    objectNode = graphqlCall.readEntity(ObjectNode.class);
    assertThat( //every result has a value for name
      stream(objectNode
        .get("data")
        .get("dataSets")
        .get(createDataSetId(vreName))
        .get("clusius_ResidenceList")
        .get("items").iterator())
        .map(item -> item
          .get("tim_hasLocation")
          .get("tim_name"))
        .filter(Objects::nonNull)
        .count(),
      is(20L)
    );
    //the results link to people with genders (we can traverse inverse relations correctly)
    assertThat(
      stream(objectNode
        .get("data")
        .get("dataSets")
        .get(createDataSetId(vreName))
        .get("clusius_ResidenceList")
        .get("items").iterator())
        .flatMap(item ->
          stream(item
            .get("tim_hasLocation")
            .get("_inverse_tim_hasBirthPlaceList")
            .get("items").iterator())
            .map(person -> person
              .get("tim_gender"))
        )
        .filter(Objects::nonNull)
        .count(),
      is(37L)
    );

  }

  @Test
  public void rdfUploadReplaceOldData() throws Exception {
    String dataSetName = "replacedata" + UUID.randomUUID().toString().replace("-", "_");
    String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
        "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
        new File(getResource(IntegrationTest.class, "smalldataset.nt").toURI()),
        "application/n-triples",
        ImmutableMap.of(
            "encoding", "UTF-8",
            "uri", "http://example.com/replacedata"
        )
    );
    assertThat(uploadResponse.getStatus(), is(201));

    // query person before delete
    Response queryData = call("/v5/graphql")
        .accept(MediaType.APPLICATION_JSON)
        .post(Entity.entity(jsnO(
            "query",
            jsn(
                "query {\n" +
                    "  dataSets {\n" +
                    "    " + dataSetId + "{\n" +
                    "      schema_Person(uri: \"http://example.org/person1\") {\n" +
                    "        schema_familyName {\n" +
                    "          value\n" +
                    "        }\n" +
                    "        schema_givenNameList {\n" +
                    "          items {\n" +
                    "            value\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
            )
        ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryData.readEntity(ObjectNode.class), is(jsnO(
        "data",
        jsnO(
            "dataSets",
            jsnO(
                dataSetId,
                jsnO(
                    "schema_Person", jsnO(
                        "schema_familyName",
                        jsnO(
                            "value", jsn("Jansen")
                        ),
                        "schema_givenNameList", jsnO(
                            "items", jsnA(
                                jsnO(
                                    "value", jsn("Jan")
                                )
                            )
                        )
                    )
                )
            )
        )
    )));

    uploadResponse = multipartPost(
        "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?replace=true",
        new File(getResource(IntegrationTest.class, "overridedataset.nt").toURI()),
        "application/n-triples",
        ImmutableMap.of(
            "encoding", "UTF-8",
            "uri", "http://example.com/replacedata"
        )
    );
    assertThat(uploadResponse.getStatus(), is(201));

    // person no longer in data set available
    queryData = call("/v5/graphql")
        .accept(MediaType.APPLICATION_JSON)
        .post(Entity.entity(jsnO(
            "query",
            jsn(
                "query {\n" +
                    "  dataSets {\n" +
                    "    " + dataSetId + "{\n" +
                    "      schema_Person(uri: \"http://example.org/person1\") {\n" +
                    "        schema_familyName {\n" +
                    "          value\n" +
                    "        }\n" +
                    "        schema_givenNameList {\n" +
                    "          items {\n" +
                    "            value\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
            )
        ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryData.readEntity(ObjectNode.class), is(jsnO(
        "data",
        jsnO(
            "dataSets",
            jsnO(
                dataSetId,
                jsnO(
                    "schema_Person", jsnO(
                        "schema_familyName",
                        null,
                        "schema_givenNameList",jsnO(
                            "items", jsnA()
                        )
                    )
                )
            )
        )
    )));

    // check new person is added
    queryData = call("/v5/graphql")
        .accept(MediaType.APPLICATION_JSON)
        .post(Entity.entity(jsnO(
            "query",
            jsn(
                "query {\n" +
                    "  dataSets {\n" +
                    "    " + dataSetId + "{\n" +
                    "      schema_Person(uri: \"http://example.org/person3\") {\n" +
                    "        schema_familyName {\n" +
                    "          value\n" +
                    "        }\n" +
                    "        schema_givenNameList {\n" +
                    "          items {\n" +
                    "            value\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
            )
        ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryData.readEntity(ObjectNode.class), is(jsnO(
        "data",
        jsnO(
            "dataSets",
            jsnO(
                dataSetId,
                jsnO(
                    "schema_Person", jsnO(
                        "schema_familyName",
                        jsnO(
                            "value", jsn("Pietersen")
                        ),
                        "schema_givenNameList", jsnO(
                            "items", jsnA(
                                jsnO(
                                    "value", jsn("Piet")
                                )
                            )
                        )
                    )
                )
            )
        )
    )));

  }

  @Test
  public void synchronousUnsuccessfulRdfUpload2X() throws Exception {
    String vreName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + vreName + "/upload/rdf?forceCreation=true&async=false",
      new File(getResource(IntegrationTest.class, "unknown_prefix1.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius"
      )
    );

    assertThat(uploadResponse.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    String content = IOUtils.toString((InputStream) uploadResponse.getEntity());
    assertThat(content, containsString("Namespace prefix 'wrong_in_1' used but not defined"));

    uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + vreName + "/upload/rdf?forceCreation=true&async=false",
      new File(getResource(IntegrationTest.class, "unknown_prefix2.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius"
      )
    );
    assertThat(uploadResponse.getStatus(), is(Response.Status.BAD_REQUEST.getStatusCode()));
    content = IOUtils.toString((InputStream) uploadResponse.getEntity());
    assertThat(content, containsString("Namespace prefix 'wrong_in_2' used but not defined"));
    assertThat(content.contains("Namespace prefix 'wrong_in_1' used but not defined"), is(false));
  }

  @Test
  public void synchronousUnsuccessfulRdfUploadWithGraphql() throws Exception {
    String vreName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + vreName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "unknown_prefix1.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius"
      )
    );

    Response graphqlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(String.format("{\n" +
          "  dataSetMetadata(dataSetId: \"%1s__%2s\")\n" +
          "  {\n" +
          "    importStatus(id: \"0\") {\n" +
          "      status\n" +
          "      errors\n" +
          "    }\n" +
          "  }\n" +
          "}\n",
        PREFIX, vreName), MediaType.valueOf("application/graphql")));
    ObjectNode objectNode = graphqlCall.readEntity(ObjectNode.class);
    String status = objectNode.get("data")
                              .get("dataSetMetadata")
                              .get("importStatus")
                              .get("status").asText();
    assertThat(status, is("DONE"));

    List<String> errors = Lists.newArrayList();
    objectNode.get("data")
              .get("dataSetMetadata")
              .get("importStatus")
              .get("errors").forEach(error -> errors.add(error.asText()));

    assertThat(errors, hasSize(1));
  }

  @Test
  public void succeedingNQuadUdUploadWithGraphql() throws Exception {
    String vreName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + vreName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.nqud").toURI()),
      "application/vnd.timbuctoo-rdf.nquads_unified_diff",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius.nqud"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    Response graphqlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(String.format("{\n" +
        "  dataSets {\n" +
        "    %1s__%2s {\n" +
        "      http___timbuctoo_huygens_knaw_nl_datasets_clusius_ResidenceList {\n" +
        "        items { uri }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}", PREFIX, vreName), MediaType.valueOf("application/graphql")));
    ObjectNode objectNode = graphqlCall.readEntity(ObjectNode.class);
    assertThat(
      objectNode
        .get("data")
        .get("dataSets")
        .get(createDataSetId(vreName))
        .get("http___timbuctoo_huygens_knaw_nl_datasets_clusius_ResidenceList")
        .get("items")
        .size(),
      is(20)
    );

    graphqlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(String.format("{\n" +
        "  dataSets {\n" +
        "    %1s__%2s {\n" +
        "      http___timbuctoo_huygens_knaw_nl_datasets_clusius_ResidenceList {\n" +
        "        items {\n" +
        "          http___timbuctoo_huygens_knaw_nl_properties_hasLocation {\n" +
        "            http___timbuctoo_huygens_knaw_nl_properties_name {\n" +
        "              value\n" +
        "            }\n" +
        "          }\n" +
        "        }\n" +
        "      }\n" +
        "    }\n" +
        "  }\n" +
        "}", PREFIX, vreName), MediaType.valueOf("application/graphql")));
    objectNode = graphqlCall.readEntity(ObjectNode.class);
    assertThat(
      stream(objectNode
        .get("data")
        .get("dataSets")
        .get(createDataSetId(vreName))
        .get("http___timbuctoo_huygens_knaw_nl_datasets_clusius_ResidenceList")
        .get("items").iterator())
        .map(item -> item
          .get("http___timbuctoo_huygens_knaw_nl_properties_hasLocation")
          .get("http___timbuctoo_huygens_knaw_nl_properties_name"))
        .filter(Objects::nonNull)
        .count(),
      is(20L)
    );
  }

  @Test
  public void resourcesyncImportsValidDatasets() throws Exception {
    String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    String dataSetId = createDataSetId(dataSetName);

    call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation Publish($dataSetId: String!) {" +
            "  publish(dataSetId: $dataSetId) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId)
        )
      ).toString(), MediaType.valueOf("application/json")));

    String capabilityListUri = format("http://localhost:%d/v5/resourcesync/%s/%s/capabilitylist.xml",
      APP.getLocalPort(),
      PREFIX,
      dataSetName
    );

    Response resourceSyncCall = call("/v2.1/remote/rs/import?forceCreation=true&async=false")
      .accept(MediaType.APPLICATION_JSON)
      .header("authorization", "fake")
      .post(Entity.entity(jsnO(
        "source", jsn(capabilityListUri),
        "userId", jsn(PREFIX),
        "dataSetId", jsn("datasettest")
      ).toString(), MediaType.valueOf("application/json")));

    assertThat("Successful resourcesync import", resourceSyncCall.getStatus(), is(200));

  }

  @Test
  public void resourceSyncChangeListGenerationTest() throws Exception {
    String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    String dataSetId = createDataSetId(dataSetName);

    call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation Publish($dataSetId: String!) {" +
            "  publish(dataSetId: $dataSetId) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId)
        )
      ).toString(), MediaType.valueOf("application/json")));

    Response uploadResponse2 = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf",
      new File(getResource(IntegrationTest.class, "test.nqud").toURI()),
      "application/n-quads",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse2.getStatus(), is(201));

    Response getChangeListCall = call("/v5/resourcesync/" + PREFIX + "/" + dataSetName + "/changelist.xml")
      .get();

    assertThat(getChangeListCall.getStatus(), is(200));

    String changeFiles = getChangeListCall.readEntity(String.class);

    assertThat(changeFiles, containsString("/changes/changes0.nqud"));
    assertThat(changeFiles, containsString("/changes/changes1.nqud"));

    Response getChangesCall = call("/v5/resourcesync/" + PREFIX + "/" + dataSetName + "/changes/changes1.nqud")
      .get();

    assertThat(getChangesCall.getStatus(), is(200));

    String changes = getChangesCall.readEntity(String.class);

    String graph = "http://example.org/datasets/" + PREFIX + "/" + dataSetName;

    assertThat(changes, is("+<http://one.example/subject1> <http://one.example/predicate1>" +
      " <http://one.example/object1> " + "<" + graph + ">" + " .\n" +
      "+<http://one.example/subject2> <http://one.example/predicate2>" +
      " <http://one.example/object2> " + "<" + graph + ">" + " .\n"));
  }

  @Test
  public void resourceSyncDataSet() throws Exception {
    String dataSetName = "resourcesync" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
        "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
        new File(getResource(IntegrationTest.class, "test.nqud").toURI()),
        "application/n-quads",
        ImmutableMap.of(
            "encoding", "UTF-8"
        )
    );
    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    Response getDataSet = call("/v5/resourcesync/" + PREFIX + "/" + dataSetName + "/dataset.nq").get();
    assertThat(getDataSet.getStatus(), is(200));

    final String dataSet = getDataSet.readEntity(String.class);

    String graph = "<http://example.org/datasets/" + PREFIX + "/" + dataSetName + ">";
    String subject1 = "<http://one.example/subject1>";
    String subject2 = "<http://one.example/subject2>";
    String object1 = "<http://one.example/object1>";
    String object2 = "<http://one.example/object2>";
    String predicate1 = "<http://one.example/predicate1>";
    String predicate2 = "<http://one.example/predicate2>";
    assertThat(dataSet, is(
        subject1 + " " + predicate1 + " " + object1 + " " + graph +  " .\n" +
        subject2 + " " + predicate2 + " " + object2 + " " + graph +  " .\n"
    ));
  }

  @Test
  public void succeedingRdfUploadResourceSync() throws Exception {
    String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius.rdfp"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    Response sourceDescResponse = call("/v5/resourcesync/sourceDescription.xml").get();
    assertThat(sourceDescResponse.getStatus(), is(200));
    Node sourceDesc = streamToXml(sourceDescResponse.readEntity(InputStream.class));
    // TODO should be ends-with, but that is not supported in xpath v1
    assertThat(
      sourceDesc,
      hasXPath("//urlset/url/loc/text()[contains(. , '" + PREFIX + "/" + dataSetName + "/capabilitylist.xml')]")
    );

    Response capabilityListResp = call("/v5/resourcesync/" + PREFIX + "/" + dataSetName + "/capabilitylist.xml").get();
    assertThat(capabilityListResp.getStatus(), is(200));
    Node capabilityList = streamToXml(capabilityListResp.readEntity(InputStream.class));
    // TODO should be ends-with, but that is not supported in xpath v1
    assertThat(
      capabilityList,
      hasXPath("//urlset/url/loc/text()[contains(. , '" + PREFIX + "/" + dataSetName + "/resourcelist.xml')]")
    );

    Response resourceListResp = call("/v5/resourcesync/" + PREFIX + "/" + dataSetName + "/resourcelist.xml")
      .get();
    assertThat(resourceListResp.getStatus(), is(200));
    Node resourceList = streamToXml(resourceListResp.readEntity(InputStream.class));
    assertThat(
      resourceList,
      hasXPath("//urlset/url/loc/text()[contains(. , '" + PREFIX + "/" + dataSetName + "/dataset.nq')]")
    );

    // call resourceSync without authorization
    sourceDescResponse = callWithoutAuthentication("/v5/resourcesync/sourceDescription.xml").get();
    assertThat(sourceDescResponse.getStatus(), is(200));
    sourceDesc = streamToXml(sourceDescResponse.readEntity(InputStream.class));
    // TODO should be ends-with, but that is not supported in xpath v1
    assertThat(
      sourceDesc, not(
        hasXPath("//urlset/url/loc/text()[contains(. , '" + PREFIX + "/" + dataSetName + "/capabilitylist.xml')]"))
    );

    capabilityListResp =
      callWithoutAuthentication("/v5/resourcesync/" + PREFIX + "/" + dataSetName + "/capabilitylist.xml")
        .get();
    assertThat(capabilityListResp.getStatus(), is(Response.Status.UNAUTHORIZED.getStatusCode()));

    resourceListResp =
      callWithoutAuthentication("/v5/resourcesync/" + PREFIX + "/" + dataSetName + "/resourcelist.xml").get();
    assertThat(resourceListResp.getStatus(), is(Response.Status.UNAUTHORIZED.getStatusCode()));
  }

  private Document streamToXml(InputStream is) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();

    return documentBuilder.parse(is);
  }

  @Test
  public void tabularUploadCreatesTabularCollections() {
    final String dataSetName = "dataset" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = PREFIX + "__" + dataSetName;
    FormDataMultiPart multiPart = new FormDataMultiPart();
    multiPart.setMediaType(MediaType.MULTIPART_FORM_DATA_TYPE);
    FileDataBodyPart fileDataBodyPart = new FileDataBodyPart(
      "file",
      new File(getResource(IntegrationTest.class, "2017_04_17_BIA_Clusius.xlsx").getFile()),
      new MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    );
    multiPart.bodyPart(fileDataBodyPart);
    multiPart.field("type", "xlsx");


    Response response = call("/v5/" + PREFIX + "/" + dataSetName + "/upload/table?forceCreation=true")
      .header(HttpHeaders.AUTHORIZATION, "fake")
      .post(Entity.entity(multiPart, multiPart.getMediaType()));

    assertThat(response.getStatus(), Matchers.is(201));

    Response query = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query tabularData {\n" +
            "dataSets {\n" +
            "    " + dataSetId + " {\n" +
            "    \thttp___timbuctoo_huygens_knaw_nl_static_v5_types_tabularFileList {\n" +
            "        items {\n" +
            "          tim_hasCollectionList {\n" +
            "            items {\n" +
            "              ... on " + dataSetId +
            "_http___timbuctoo_huygens_knaw_nl_static_v5_types_tabularCollection {\n" +
            "                title {\n" +
            "                  value\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.valueOf("application/json")));

    ObjectNode data = query.readEntity(ObjectNode.class);
    List<JsonNode> tabularFile = stream(
      data.get("data")
          .get("dataSets")
          .get(dataSetId)
          .get("http___timbuctoo_huygens_knaw_nl_static_v5_types_tabularFileList")
          .get("items")
          .iterator()
    ).collect(toList());

    Stream<JsonNode> rawCollections = stream(tabularFile.get(0).get("tim_hasCollectionList").get("items").iterator());
    assertThat(rawCollections.collect(toList()), containsInAnyOrder(jsnO(
      "title", jsnO("value", jsn("Persons"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Persons_name_variants"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Occupation"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Residence"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Education"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Biography"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Membership"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Provenance"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Provenance_type"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Places"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Place_name_variants"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Institutes"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Institute_name_variants"))
      ),
      jsnO(
        "title", jsnO("value", jsn("Fields_of_interest"))
      )));
  }

  @Test
  public void viewConfigCanBeChangedWithGraphQl() throws Exception {
    final String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.nqud").toURI()),
      "application/vnd.timbuctoo-rdf.nquads_unified_diff",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius.nqud"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    final String collectionUri = "http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons";
    final String type = "test3";
    final String value = "test4";
    Response graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation setViewConfig($dataSetId: String!, $collectionUri: String!, $type: String!, $value: String) " +
            "{setViewConfig(dataSetId: $dataSetId, collectionUri: $collectionUri, viewConfig: [{type: $type, value: " +
            "$value, subComponents: [], formatter: []}]){   type    value}}"),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId),
          "collectionUri", jsn(collectionUri),
          "type", jsn(type),
          "value", jsn(value)
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(graphQlCall.getStatus(), is(200));
    assertThat(graphQlCall.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO("setViewConfig", jsnA(
        jsnO(
          "type", jsn("test3"),
          "value", jsn("test4")
        )
        )
      )
    )));

    graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(
        Entity.entity(
          String.format("{\"query\":\"{\\n  dataSetMetadata" +
            "(dataSetId:\\\"%s\\\") {\\n    collectionList {\\n      " +
            "items {\\n        uri\\n        viewConfig {\\n          type\\n          value\\n        }\\n " +
            "  " +
            "  " +
            " }\\n    }\\n  }\\n}\",\"variables\":null,\"operationName\":null}", dataSetId),
          MediaType.valueOf("application/json")
        )
      );

    assertThat(graphQlCall.getStatus(), is(200));
    ObjectNode metaData = graphQlCall.readEntity(ObjectNode.class);
    assertThat(
      stream(metaData
        .get("data")
        .get("dataSetMetadata")
        .get("collectionList")
        .get("items").iterator()).collect(toList()),
      hasItem(jsnO(
        "uri", jsn(collectionUri),
        "viewConfig", jsnA(
          jsnO(
            "type", jsn(type),
            "value", jsn(value)
          )
        )
      ))
    );
  }

  @Test
  public void schemaCanBeCustomizedWithGraphQl() throws Exception {
    final String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.nqud").toURI()),
      "application/vnd.timbuctoo-rdf.nquads_unified_diff",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius.nqud"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    ObjectNode customSchemaField = jsnO(
      "uri", jsn("test:test"),
      "isList", jsn(false),
      "values", jsnA(jsn("String"))
    );

    ObjectNode customSchemaField2 = jsnO(
      "uri", jsn("http://www.test2.com"),
      "isList", jsn(false),
      "values", jsnA(jsn("String"))
    );

    ObjectNode customSchema = jsnO(
      "collectionId", jsn("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons"),
      "fields", jsnA(customSchemaField, customSchemaField2)
    );
    Response graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation extendSchema($dataSetId: String!, $customSchema: [CustomSchemaTypeInput!]!) { " +
            "   extendSchema(dataSetId: $dataSetId, customSchema:$customSchema){\n" +
            "    message\n" +
            "   }" +
            "}"),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId),
          "customSchema", customSchema
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(graphQlCall.getStatus(), is(200));

    ObjectNode returnedData = graphQlCall.readEntity(ObjectNode.class);

    assertThat(returnedData.get("data").get("extendSchema").get("message").toString(),
      is("\"Schema extended successfully.\""));


    Response retrieveExtendedSchema = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(String.format(
          "query retrieveExtendedSchema  {\n" +
            "  dataSets {\n" +
            "    %s {\n" +
            "      http___timbuctoo_huygens_knaw_nl_datasets_clusius_PersonsList {\n" +
            "        items {\n" +
            "          test_test {\n" +
            "            type\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}", dataSetId))
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(retrieveExtendedSchema.getStatus(), is(200));

    ObjectNode retrievedData = retrieveExtendedSchema.readEntity(ObjectNode.class);

    assertThat(retrievedData.get("data").get("dataSets").get(dataSetId)
                            .get("http___timbuctoo_huygens_knaw_nl_datasets_clusius_PersonsList")
                            .get("items").get(0).get("test_test").isNull(),
      is(true));

    ObjectNode customSchemaField3 = jsnO(
      "uri", jsn("test_test3"),
      "isList", jsn(false),
      "values", jsnA(jsn("String"))
    );

    ObjectNode customSchema2 = jsnO(
      "collectionId", jsn("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons"),
      "fields", jsnA(customSchemaField3)
    );

    Response graphQlCall2 = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation extendSchema($dataSetId: String!, $customSchema: [CustomSchemaTypeInput!]!) { " +
            "   extendSchema(dataSetId: $dataSetId, customSchema:$customSchema){\n" +
            "    message\n" +
            "   }" +
            "}"),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId),
          "customSchema", customSchema2
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(graphQlCall2.getStatus(), is(200));

    Response retrieveExtendedSchema2 = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(String.format(
          "query retrieveExtendedSchema  {\n" +
            "  dataSets {\n" +
            "    %s {\n" +
            "      http___timbuctoo_huygens_knaw_nl_datasets_clusius_PersonsList {\n" +
            "        items {\n" +
            "          test_test {\n" +
            "            type\n" +
            "          },\n" +
            "          test_test3 {\n" +
            "            type\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}", dataSetId))
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(retrieveExtendedSchema2.getStatus(), is(200));

    ObjectNode retrievedData2 = retrieveExtendedSchema2.readEntity(ObjectNode.class);

    assertThat(retrievedData2.get("data").get("dataSets").get(dataSetId)
                             .get("http___timbuctoo_huygens_knaw_nl_datasets_clusius_PersonsList")
                             .get("items").get(0).get("test_test3").isNull(),
      is(true));

  }

  @Test
  public void provenanceSchemaCanBeCustomizedWithGraphQl() throws Exception {
    final String dataSetName = "prov" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "mutatedataset.nt").toURI()),
      "application/n-triples",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    ObjectNode provenanceSchema = jsnO(
      "fields", jsnA(
        jsnO(
          "uri", jsn("http://example.org/foundIn"),
          "isList", jsn(false),
          "object", jsnO(
            "type", jsn("http://example.org/Book"),
            "fields", jsnA(
              jsnO(
                "uri", jsn("http://example.org/remarks"),
                "isList", jsn(false),
                "valueType", jsn("http://www.w3.org/2001/XMLSchema#string")
              )
            )
          )
        )
      ));

    Response graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation setCustomProvenance($entity: CustomProvenanceInput!) { " +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      setCustomProvenance(customProvenance: $entity){\n" +
            "        message\n" +
            "      }" +
            "    }" +
            "  }" +
            "}"),
        "variables",
        jsnO("entity", provenanceSchema)
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(graphQlCall.getStatus(), is(200));

    ObjectNode returnedData = graphQlCall.readEntity(ObjectNode.class);

    assertThat(
      returnedData.get("data").get("dataSets").get(dataSetId).get("setCustomProvenance").get("message").toString(),
      is("\"Custom provenance is set.\""));

    // Add provenance
    Response mutation = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation Provenance($uri:String! $entity:" + dataSetId + "_schema_PersonEditInput! ) {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person {\n" +
            "        edit(uri: $uri entity: $entity) {\n" +
            "          schema_familyName {\n" +
            "            value\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        ),
        "variables", jsnO(
          "uri", jsn("http://example.org/person1"),
          "entity", jsnO(
            "provenance", jsnO(
              "http___example_org_foundIn", jsnO(
                "http___example_org_remarks", jsnO(
                  "type", jsn("xsd_string"),
                  "value", jsn("My remarks")
                )
              )
            )
          )
        ),
        "operationName", jsn("Provenance")
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(mutation.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "edit", jsnO(
                "schema_familyName", jsnO(
                  "value", jsn("Jansen")
                )
              )
            )
          )
        )
      )
    )));

    // query person after mutation
    Response queryAfterMutation = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person(uri: \"http://example.org/person1\") {\n" +
            "        tim_pred_latestRevision {\n" +
            "          http___example_org_foundIn {\n" +
            "            http___example_org_remarks {\n" +
            "              value\n" +
            "            }\n" +
            "          }\n" +
            "          _inverse_prov_generated {\n" +
            "            prov_qualifiedAssociation {\n" +
            "              prov_hadPlan  {\n" +
            "                tim_pred_hasCustomProv {\n" +
            "                  http___example_org_foundIn {\n" +
            "                    http___example_org_remarks {\n" +
            "                      value\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryAfterMutation.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "tim_pred_latestRevision", jsnO(
                "http___example_org_foundIn", jsnO(
                  "http___example_org_remarks", jsnO(
                    "value", jsn("My remarks")
                  )
                ),
                "_inverse_prov_generated", jsnO(
                  "prov_qualifiedAssociation", jsnO(
                    "prov_hadPlan", jsnO(
                      "tim_pred_hasCustomProv", jsnO(
                        "http___example_org_foundIn", jsnO(
                          "http___example_org_remarks", jsnO(
                            "value", jsn("My remarks")
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )));
  }

  private String createDataSetId(String dataSetName) {
    return PREFIX + "__" + dataSetName;
  }

  @Test
  public void authorizationReadFromAuthorizationsFileIfVreFileNotPresent() throws Exception {
    // Create a dataset
    Client client = ClientBuilder.newBuilder().build();
    String dataSetName = "dataset" + UUID.randomUUID().toString().replace("-", "_");


    Response graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation CreateDataSet($dataSetName: String!) {" +
            "  createDataSet(dataSetName: $dataSetName) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetName", jsn(dataSetName)
        )
      ).toString(), MediaType.valueOf("application/json")));


    assertThat(graphQlCall.getStatus(), is(200));
    // check if the dataset is created
    List<String> dataSetNamesOfDummy = getDataSetNamesOfDummy();
    assertThat(dataSetNamesOfDummy, hasItem(PREFIX + "__" + dataSetName));

  }

  @Test
  public void indexConfigCanBeChangedWithGraphQl() throws Exception {
    final String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.nqud").toURI()),
      "application/vnd.timbuctoo-rdf.nquads_unified_diff",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius.nqud"
      )
    );

    assertThat("Successful upload of rdf", uploadResponse.getStatus(), is(201));

    final String collectionUri = "http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons";
    Response graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation SetIndexConfig($dataSetId: String!, $collectionUri: String!, $indexConfig: IndexConfigInput!) {\n" +
            "  setIndexConfig(dataSetId: $dataSetId, collectionUri:$collectionUri, indexConfig: $indexConfig) {\n" +
            "    facet {\n" +
            "      paths\n" +
            "      type\n" +
            "    }\n" +
            "  }\n" +
            "}"),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId),
          "collectionUri", jsn(collectionUri),
          "indexConfig", jsnO(
            "facet", jsnA(
              jsnO(
                "paths", jsnA(jsn("tim_name.value")),
                "type", jsn("MultiSelect")
              )
            ),
            "fullText", jsnA()
          )
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(graphQlCall.getStatus(), is(200));
    assertThat(graphQlCall.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "setIndexConfig", jsnO(
          "facet", jsnA(
            jsnO(
              "paths", jsnA(jsn("tim_name.value")),
              "type", jsn("MultiSelect")
            )
          )
        )
      )
    )));

    graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(
        Entity.entity(
          jsnO(
            "query", jsn(
              "query metadata($dataSetId: ID!) {\n" +
                "  dataSetMetadata(dataSetId: $dataSetId) {\n" +
                "    collectionList {\n" +
                "      items {\n" +
                "        uri\n" +
                "        indexConfig {\n" +
                "          facet {\n" +
                "            paths\n" +
                "            type\n" +
                "          }\n" +
                "          fullText {\n" +
                "            fields {\n" +
                "              path\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}"
            ),
            "variables", jsnO(
              "dataSetId", jsn(dataSetId)
            )
          ),
          MediaType.valueOf("application/json")
        )
      );

    assertThat(graphQlCall.getStatus(), is(200));
    ObjectNode metaData = graphQlCall.readEntity(ObjectNode.class);
    assertThat(
      stream(metaData
        .get("data")
        .get("dataSetMetadata")
        .get("collectionList")
        .get("items").iterator()).collect(toList()),
      hasItem(jsnO(
        "uri", jsn(collectionUri),
        "indexConfig", jsnO(
          "facet", jsnA(
            jsnO(
              "paths", jsnA(jsn("tim_name.value")),
              "type", jsn("MultiSelect")
            )
          ),
          "fullText", jsnA()
        )
      ))
    );

  }

  @Test
  public void dataSetCanBeCreatedWithGraphQl() {
    final String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);

    Response graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation CreateDataSet($dataSetName: String!) {" +
            "  createDataSet(dataSetName: $dataSetName) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetName", jsn(dataSetName)
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(graphQlCall.getStatus(), is(200));
    assertThat(graphQlCall.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "createDataSet", jsnO(
          "dataSetId", jsn(dataSetId)
        )
      )
    )));

    graphQlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(
        Entity.entity(
          jsnO(
            "query", jsn(
              "query metadata($dataSetId: ID!) {\n" +
                "  dataSetMetadata(dataSetId: $dataSetId) {\n" +
                "    dataSetId" +
                "  }\n" +
                "}"
            ),
            "variables", jsnO(
              "dataSetId", jsn(dataSetId)
            )
          ),
          MediaType.valueOf("application/json")
        )
      );

    assertThat(graphQlCall.getStatus(), is(200));
    ObjectNode metaData = graphQlCall.readEntity(ObjectNode.class);
    assertThat(metaData, is(
      jsnO(
        "data", jsnO(
          "dataSetMetadata", jsnO(
            "dataSetId", jsn(dataSetId)
          )
        )
      )
    ));

  }

  @Test
  public void dataSetCanBeDeletedWithGraphQl() {
    final String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);

    Response createCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation CreateDataSet($dataSetName: String!) {" +
            "  createDataSet(dataSetName: $dataSetName) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetName", jsn(dataSetName)
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(createCall.getStatus(), is(200));
    assertThat(createCall.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "createDataSet", jsnO(
          "dataSetId", jsn(dataSetId)
        )
      )
    )));

    Response retrieveBeforeDelete = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "{\n" +
            "  allDataSets {\n" +
            "    dataSetId\n" +
            "  }\n" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetName", jsn(dataSetName)
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(retrieveBeforeDelete.getStatus(), is(200));
    ObjectNode dataSetsBeforeDelete = retrieveBeforeDelete.readEntity(ObjectNode.class);
    assertThat(stream(dataSetsBeforeDelete
        .get("data")
        .get("allDataSets").iterator()
      ).collect(toList()),
      hasItem((JsonNode) jsnO("dataSetId", jsn(dataSetId)))
    );

    Response deleteCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation DeleteDataSet($dataSetId: String!) {" +
            "  deleteDataSet(dataSetId: $dataSetId) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId)
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(deleteCall.getStatus(), is(200));
    assertThat(deleteCall.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "deleteDataSet", jsnO(
          "dataSetId", jsn(dataSetId)
        )
      )
    )));

    Response retrieveAfterDelete = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "{\n" +
            "  allDataSets {\n" +
            "    dataSetId\n" +
            "  }\n" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetName", jsn(dataSetName)
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(retrieveAfterDelete.getStatus(), is(200));
    ObjectNode dataSetsAfterDelete = retrieveAfterDelete.readEntity(ObjectNode.class);
    assertThat(stream(dataSetsAfterDelete
        .get("data")
        .get("allDataSets").iterator()
      ).collect(toList()),
      not(hasItem((JsonNode) jsnO("dataSetId", jsn(dataSetId))))
    );
  }

  @Test
  public void resourceSyncShowsAllThePublicDataSets() throws Exception {
    final String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.nqud").toURI()),
      "application/vnd.timbuctoo-rdf.nquads_unified_diff",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius.nqud"
      )
    );
    assertThat(uploadResponse.getStatus(), is(201));

    call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation Publish($dataSetId: String!) {" +
            "  publish(dataSetId: $dataSetId) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId)
        )
      ).toString(), MediaType.valueOf("application/json")));

    Response checkPublishedCall = callWithoutAuthentication("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query MetaData($dataSetId: ID!) {" +
            "  dataSetMetadata(dataSetId: $dataSetId) {" +
            "    dataSetId" +
            "  }" +
            "}"
        ),
        "variables",
        jsnO(
          "dataSetId", jsn(dataSetId)
        )
      ).toString(), MediaType.valueOf("application/json")));

    assertThat(checkPublishedCall.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSetMetadata", jsnO(
          "dataSetId", jsn(dataSetId)
        )
      )
    )));

    Response resourceSyncCall = callWithoutAuthentication("/v5/resourcesync/sourceDescription.xml").get();

    ByteArrayInputStream value = new ByteArrayInputStream(resourceSyncCall.readEntity(String.class).getBytes());
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(value);
    XPath xpath = XPathFactory.newInstance().newXPath();

    NodeList uriNodes = (NodeList) xpath.compile("/urlset/url/loc").evaluate(document, XPathConstants.NODESET);
    Set<String> dataSets = new HashSet<>();
    for (int i = 0; i < uriNodes.getLength(); i++) {
      dataSets.add(uriNodes.item(i).getTextContent());
    }

    assertThat(dataSets, hasItem(endsWith(dataSetName + "/capabilitylist.xml")));
  }

  @Test
  public void summaryPropertiesCanBeOverriddenWithGraphQl() throws Exception {
    final String dataSetName = "clusius_" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "bia_clusius.ttl").toURI()),
      "text/turtle",
      ImmutableMap.of(
        "encoding", "UTF-8",
        "uri", "http://example.com/clusius.nqud"
      )
    );

    assertThat(uploadResponse.getStatus(), is(201));

    Response setTitleForPersons = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation SetSummaryProps($dataSetId: String! $collectionUri: String! $data:SummaryPropertiesInput!) {\n" +
            "  setSummaryProperties(dataSetId: $dataSetId, collectionUri: $collectionUri, summaryProperties: $data)" +
            "{ \n" +
            "    title {\n" +
            "      path {\n" +
            "        step\n" +
            "        direction\n" +
            "      }" +
            "    }\n" +
            "  }\n" +
            "}\n"
        ),
        "variables",
        jsnO(
          "dataSetId",
          jsn(dataSetId),
          "collectionUri",
          jsn("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons"),
          "data",
          jsnO(
            "title",
            jsnO(
              "path",
              jsnA(
                jsnO(
                  "step", jsn("http://timbuctoo.huygens.knaw.nl/properties/birthDate"),
                  "direction", jsn("OUT")
                )
              ),
              "type",
              jsn("DirectionalPath")
            )
          )
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(setTitleForPersons.getStatus(), is(200));

    // we know the birth date (therefore we also know the title) of the person used in the query beneath
    Response queryData = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query bia_clusius {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      clusius_Persons(uri: \"http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons_PE00002125\") {\n" +
            "        tim_birthDate {\n" +
            "          value\n" +
            "        }\n" +
            "        title {\n" +
            "          value\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryData.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "clusius_Persons",
            jsnO(
              "tim_birthDate",
              jsnO(
                "value",
                jsn("1538")
              ),
              "title",
              jsnO(
                "value",
                jsn("1538")
              )
            )
          )
        )
      )
    )));
  }

  @Test
  public void createDataWithGraphQl() throws Exception {
    // upload dataset
    final String dataSetName = "create" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "mutatedataset.nt").toURI()),
      "application/n-triples",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );

    assertThat(uploadResponse.getStatus(), is(201));

    // query person does not exist yet
    Response queryData = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person(uri: \"http://example.org/person_new\") {\n" +
            "        schema_familyName {\n" +
            "          value\n" +
            "        }\n" +
            "        schema_givenNameList {\n" +
            "          items {\n" +
            "            value\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryData.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "schema_familyName", jsn(null),
              "schema_givenNameList", jsnO(
                "items", jsnA()
              )
            )
          )
        )
      )
    )));

    // create
    Response create = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation Create($uri:String! $entity:" + dataSetId + "_schema_PersonCreateInput! ) {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person {\n" +
            "        create(uri: $uri entity: $entity) {\n" +
            "          schema_familyName {\n" +
            "            value\n" +
            "          }\n" +
            "          schema_givenNameList {\n" +
            "            items {\n" +
            "              value\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        ),
        "variables", jsnO(
          "uri", jsn("http://example.org/person_new"),
          "entity", jsnO(
            "creations", jsnO(
              "schema_familyName", jsnO(
                "type", jsn("xsd_string"),
                "value", jsn("Test2")
              ),
              "schema_givenNameList", jsnA(jsnO(
                "type", jsn("xsd_string"),
                "value", jsn("Jan")
              ))
            )
          )
        ),
        "operationName", jsn("Create")
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(create.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "create", jsnO(
                "schema_familyName", jsnO(
                  "value", jsn("Test2")
                ),
                "schema_givenNameList", jsnO(
                  "items", jsnA(
                    jsnO(
                      "value", jsn("Jan")
                    )
                  )
                )
              )
            )
          )
        )
      )
    )));

    // query person after create
    Response queryAfterCreate = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + " {\n" +
            "      schema_Person(uri: \"http://example.org/person_new\") {\n" +
            "        schema_familyName {\n" +
            "          value\n" +
            "        }\n" +
            "        schema_givenNameList {\n" +
            "          items {\n" +
            "            value\n" +
            "          }\n" +
            "        }\n" +
            "        tim_pred_latestRevision {\n" +
            "          _inverse_prov_generated {\n" +
            "            prov_associatedWith {\n" +
            "              uri\n" +
            "            }\n" +
            "            prov_qualifiedAssociation {\n" +
            "              prov_hadPlan {\n" +
            "                tim_pred_additions {\n" +
            "                  tim_pred_hasAdditionList {\n" +
            "                    items {\n" +
            "                      tim_pred_hasKey {\n" +
            "                        uri\n" +
            "                      }\n" +
            "                      tim_pred_hasValue {\n" +
            "                        tim_pred_rawValue {\n" +
            "                          value\n" +
            "                        }\n" +
            "                        tim_pred_type {\n" +
            "                          value\n" +
            "                        }\n" +
            "                      }\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    ObjectNode queryAfterCreateNode = queryAfterCreate.readEntity(ObjectNode.class);
    JsonNode hasAdditionListNode = queryAfterCreateNode
      .get("data")
      .get("dataSets")
      .get(dataSetId)
      .get("schema_Person")
      .get("tim_pred_latestRevision")
      .get("_inverse_prov_generated")
      .get("prov_qualifiedAssociation")
      .get("prov_hadPlan")
      .get("tim_pred_additions")
      .get("tim_pred_hasAdditionList");

    assertThat(hasAdditionListNode.get("items"), containsInAnyOrder(
      jsnO(
        "tim_pred_hasKey", jsnO(
          "uri", jsn("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
        ),
        "tim_pred_hasValue", jsnO(
          "tim_pred_rawValue", jsnO(
            "value", jsn("http://schema.org/Person")
          ),
          "tim_pred_type", null
        )
      ),
      jsnO(
        "tim_pred_hasKey", jsnO(
          "uri", jsn("http://schema.org/familyName")
        ),
        "tim_pred_hasValue", jsnO(
          "tim_pred_rawValue", jsnO(
            "value", jsn("Test2")
          ),
          "tim_pred_type", jsnO(
            "value", jsn("http://www.w3.org/2001/XMLSchema#string")
          )
        )
      ),
      jsnO(
        "tim_pred_hasKey", jsnO(
          "uri", jsn("http://schema.org/givenName")
        ),
        "tim_pred_hasValue", jsnO(
          "tim_pred_rawValue", jsnO(
            "value", jsn("Jan")
          ),
          "tim_pred_type", jsnO(
            "value", jsn("http://www.w3.org/2001/XMLSchema#string")
          )
        )
      )
    ));

    ((ObjectNode) hasAdditionListNode).set("items", jsnA());

    assertThat(queryAfterCreateNode, is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "schema_familyName", jsnO(
                "value", jsn("Test2")
              ),
              "schema_givenNameList", jsnO(
                "items", jsnA(
                  jsnO(
                    "value", jsn("Jan")
                  )
                )
              ),
              "tim_pred_latestRevision", jsnO(
                "_inverse_prov_generated", jsnO(
                  "prov_associatedWith", jsnO(
                    "uri", jsn(format("http://127.0.0.1:%d/users/" + USER_ID, APP.getLocalPort()))
                  ),
                  "prov_qualifiedAssociation", jsnO(
                    "prov_hadPlan", jsnO(
                      "tim_pred_additions", jsnO(
                        "tim_pred_hasAdditionList", jsnO(
                          "items", jsnA()
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )));
  }

  @Test
  public void editDataWithGraphQl() throws Exception {
    // upload dataset
    final String dataSetName = "edit" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "mutatedataset.nt").toURI()),
      "application/n-triples",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );

    assertThat(uploadResponse.getStatus(), is(201));

    // query person before edit
    Response queryData = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person(uri: \"http://example.org/person1\") {\n" +
            "        schema_familyName {\n" +
            "          value\n" +
            "        }\n" +
            "        schema_givenNameList {\n" +
            "          items {\n" +
            "            value\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryData.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "schema_familyName",
              jsnO(
                "value", jsn("Jansen")
              ),
              "schema_givenNameList", jsnO(
                "items", jsnA(
                  jsnO(
                    "value", jsn("Jan")
                  )
                )
              )
            )
          )
        )
      )
    )));

    // edit
    Response edit = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation Edit($uri:String! $entity:" + dataSetId + "_schema_PersonEditInput! ) {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person {\n" +
            "        edit(uri: $uri entity: $entity) {\n" +
            "          schema_familyName {\n" +
            "            value\n" +
            "          }\n" +
            "          schema_givenNameList {\n" +
            "            items {\n" +
            "              value\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        ),
        "variables", jsnO(
          "uri", jsn("http://example.org/person1"),
          "entity", jsnO(
            "replacements", jsnO(
              "schema_familyName", jsnO(
                "type", jsn("xsd_string"),
                "value", jsn("Test2")
              )
            ),
            "additions", jsnO(
              "schema_givenNameList", jsnA(jsnO(
                "type", jsn("xsd_string"),
                "value", jsn("Janus")
              ))
            ),
            "deletions", jsnO(
              "schema_givenNameList", jsnA(jsnO(
                "type", jsn("xsd_string"),
                "value", jsn("Jan")
              ))
            )
          )
        ),
        "operationName", jsn("Edit")
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(edit.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "edit", jsnO(
                "schema_familyName", jsnO(
                  "value", jsn("Test2")
                ),
                "schema_givenNameList", jsnO(
                  "items", jsnA(
                    jsnO(
                      "value", jsn("Janus")
                    )
                  )
                )
              )
            )
          )
        )
      )
    )));

    // query person after edit
    Response queryAfterEdit = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person(uri: \"http://example.org/person1\") {\n" +
            "        schema_familyName {\n" +
            "          value\n" +
            "        }\n" +
            "        schema_givenNameList {\n" +
            "          items {\n" +
            "            value\n" +
            "          }\n" +
            "        }\n" +
            "        tim_pred_latestRevision {\n" +
            "          _inverse_prov_generated {\n" +
            "            prov_associatedWith {\n" +
            "              uri\n" +
            "            }\n" +
            "            prov_qualifiedAssociation {\n" +
            "              prov_hadPlan {\n" +
            "                tim_pred_replacements {\n" +
            "                  tim_pred_hasReplacement {\n" +
            "                    tim_pred_hasKey {\n" +
            "                      uri\n" +
            "                    }\n" +
            "                    tim_pred_hasValue {\n" +
            "                      tim_pred_rawValue {\n" +
            "                        value\n" +
            "                      }\n" +
            "                      tim_pred_type {\n" +
            "                        value\n" +
            "                      }\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "                tim_pred_additions {\n" +
            "                  tim_pred_hasAddition {\n" +
            "                    tim_pred_hasKey {\n" +
            "                      uri\n" +
            "                    }\n" +
            "                    tim_pred_hasValue {\n" +
            "                      tim_pred_rawValue {\n" +
            "                        value\n" +
            "                      }\n" +
            "                      tim_pred_type {\n" +
            "                        value\n" +
            "                      }\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "                tim_pred_deletions {\n" +
            "                  tim_pred_hasDeletion {\n" +
            "                    tim_pred_hasKey {\n" +
            "                      uri\n" +
            "                    }\n" +
            "                    tim_pred_hasValue {\n" +
            "                      tim_pred_rawValue {\n" +
            "                        value\n" +
            "                      }\n" +
            "                      tim_pred_type {\n" +
            "                        value\n" +
            "                      }\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryAfterEdit.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "schema_familyName", jsnO(
                "value", jsn("Test2")
              ),
              "schema_givenNameList", jsnO(
                "items", jsnA(
                  jsnO(
                    "value", jsn("Janus")
                  )
                )
              ),
              "tim_pred_latestRevision", jsnO(
                "_inverse_prov_generated", jsnO(
                  "prov_associatedWith", jsnO(
                    "uri", jsn(format("http://127.0.0.1:%d/users/" + USER_ID, APP.getLocalPort()))
                  ),
                  "prov_qualifiedAssociation", jsnO(
                    "prov_hadPlan", jsnO(
                      "tim_pred_replacements", jsnO(
                        "tim_pred_hasReplacement", jsnO(
                          "tim_pred_hasKey", jsnO(
                            "uri", jsn("http://schema.org/familyName")
                          ),
                          "tim_pred_hasValue", jsnO(
                            "tim_pred_rawValue", jsnO(
                              "value", jsn("Test2")
                            ),
                            "tim_pred_type", jsnO(
                              "value", jsn("http://www.w3.org/2001/XMLSchema#string")
                            )
                          )
                        )
                      ),
                      "tim_pred_additions", jsnO(
                        "tim_pred_hasAddition", jsnO(
                          "tim_pred_hasKey", jsnO(
                            "uri", jsn("http://schema.org/givenName")
                          ),
                          "tim_pred_hasValue", jsnO(
                            "tim_pred_rawValue", jsnO(
                              "value", jsn("Janus")
                            ),
                            "tim_pred_type", jsnO(
                              "value", jsn("http://www.w3.org/2001/XMLSchema#string")
                            )
                          )
                        )
                      ),
                      "tim_pred_deletions", jsnO(
                        "tim_pred_hasDeletion", jsnO(
                          "tim_pred_hasKey", jsnO(
                            "uri", jsn("http://schema.org/givenName")
                          ),
                          "tim_pred_hasValue", jsnO(
                            "tim_pred_rawValue", jsnO(
                              "value", jsn("Jan")
                            ),
                            "tim_pred_type", jsnO(
                              "value", jsn("http://www.w3.org/2001/XMLSchema#string")
                            )
                          )
                        )
                      )
                    )
                  )
                )
              )
            )
          )
        )
      )
    )));
  }

  @Test
  public void deleteDataWithGraphQl() throws Exception {
    // upload dataset
    final String dataSetName = "delete" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "mutatedataset.nt").toURI()),
      "application/n-triples",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );

    assertThat(uploadResponse.getStatus(), is(201));

    // query person before delete
    Response queryData = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person(uri: \"http://example.org/person1\") {\n" +
            "        schema_familyName {\n" +
            "          value\n" +
            "        }\n" +
            "        schema_givenNameList {\n" +
            "          items {\n" +
            "            value\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryData.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "schema_familyName",
              jsnO(
                "value", jsn("Jansen")
              ),
              "schema_givenNameList", jsnO(
                "items", jsnA(
                  jsnO(
                    "value", jsn("Jan")
                  )
                )
              )
            )
          )
        )
      )
    )));

    // delete
    Response edit = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation Delete($uri:String!) {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person {\n" +
            "        delete(uri: $uri) {\n" +
            "          uri \n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        ),
        "variables", jsnO(
          "uri", jsn("http://example.org/person1")
        ),
        "operationName", jsn("Delete")
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(edit.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "delete", jsnO(
                "uri", jsn("http://example.org/person1")
              )
            )
          )
        )
      )
    )));

    // query person after delete
    Response queryOnEntityAfterDelete = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      schema_Person(uri: \"http://example.org/person1\") {\n" +
            "        schema_familyName {\n" +
            "          value\n" +
            "        }\n" +
            "        schema_givenNameList {\n" +
            "          items {\n" +
            "            value\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    assertThat(queryOnEntityAfterDelete.readEntity(ObjectNode.class), is(jsnO(
      "data",
      jsnO(
        "dataSets",
        jsnO(
          dataSetId,
          jsnO(
            "schema_Person", jsnO(
              "schema_familyName", jsn(null),
              "schema_givenNameList", jsnO(
                "items", jsnA()
              )
            )
          )
        )
      )
    )));

    // query person provenance after delete
    Response queryProvAfterDelete = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "  dataSets {\n" +
            "    " + dataSetId + "{\n" +
            "      tim_unknown(uri: \"http://example.org/person1\") {\n" +
            "        tim_pred_latestRevision {\n" +
            "          _inverse_prov_generated {\n" +
            "            prov_associatedWith {\n" +
            "              uri\n" +
            "            }\n" +
            "            prov_qualifiedAssociation {\n" +
            "              prov_hadPlan {\n" +
            "                tim_pred_deletions {\n" +
            "                  tim_pred_hasDeletionList {\n" +
            "                    items {\n" +
            "                      tim_pred_hasKey {\n" +
            "                        uri\n" +
            "                      }\n" +
            "                      tim_pred_hasValue {\n" +
            "                        tim_pred_rawValue {\n" +
            "                          value\n" +
            "                        }\n" +
            "                        tim_pred_type {\n" +
            "                          value\n" +
            "                        }\n" +
            "                      }\n" +
            "                    }\n" +
            "                  }\n" +
            "                }\n" +
            "              }\n" +
            "            }\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    ObjectNode queryAfterDeleteNode = queryProvAfterDelete.readEntity(ObjectNode.class);
    JsonNode hasDeletionList = queryAfterDeleteNode
      .get("data")
      .get("dataSets")
      .get(dataSetId)
      .get("tim_unknown")
      .get("tim_pred_latestRevision")
      .get("_inverse_prov_generated")
      .get("prov_qualifiedAssociation")
      .get("prov_hadPlan")
      .get("tim_pred_deletions")
      .get("tim_pred_hasDeletionList");

    assertThat(hasDeletionList.get("items"), containsInAnyOrder(
      jsnO(
        "tim_pred_hasKey", jsnO(
          "uri", jsn("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")
        ),
        "tim_pred_hasValue", jsnO(
          "tim_pred_rawValue", jsnO(
            "value", jsn("http://schema.org/Person")
          ),
          "tim_pred_type", null
        )
      ),
      jsnO(
        "tim_pred_hasKey", jsnO(
          "uri", jsn("http://schema.org/familyName")
        ),
        "tim_pred_hasValue", jsnO(
          "tim_pred_rawValue", jsnO(
            "value", jsn("Jansen")
          ),
          "tim_pred_type", jsnO(
            "value", jsn("http://www.w3.org/2001/XMLSchema#string")
          )
        )
      ),
      jsnO("tim_pred_hasKey", jsnO(
        "uri", jsn("http://schema.org/givenName")
        ),
        "tim_pred_hasValue", jsnO(
          "tim_pred_rawValue", jsnO(
            "value", jsn("Jan")
          ),
          "tim_pred_type", jsnO(
            "value", jsn("http://www.w3.org/2001/XMLSchema#string")
          ))
      )
    ));
  }

  @Test
  public void dataSetWithJustUnknownsMustNotCorruptTheGraphQlSchema() throws Exception {
    // upload dataset
    final String dataSetName = "unknown" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
      "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
      new File(getResource(IntegrationTest.class, "unknowndataset.nt").toURI()),
      "application/n-triples",
      ImmutableMap.of(
        "encoding", "UTF-8"
      )
    );
    assertThat(uploadResponse.getStatus(), is(201));

    // query graphql
    Response queryData = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "dataSetMetadataList(promotedOnly:false publishedOnly:false) {\n" +
            "    dataSetId\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    Iterable<JsonNode> datasets = () -> queryData.readEntity(ObjectNode.class)
                                                 .get("data")
                                                 .get("dataSetMetadataList")
                                                 .elements();
    List<String> dataSetIds =
      StreamSupport.stream(datasets.spliterator(), false).map(dataSet -> dataSet.get("dataSetId").asText())
                   .collect(toList());
    assertThat(dataSetIds, hasItem(dataSetId));
  }

  @Test
  public void publishedDatasetsAreReadableForEveryone() {
    // upload dataset
    final String dataSetName = "unknown" + UUID.randomUUID().toString().replace("-", "_");
    final String dataSetId = createDataSetId(dataSetName);

    // create new data set, this will be private
    call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation createDataSet {\n" +
            "  createDataSet(dataSetName:\"" + dataSetName + "\") {\n" +
            "    dataSetId\n" +
            "  }\n" +
            "}\n"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    // retrieve data sets as a user that is not logged in before publishing the data set
    Response queryData = callWithoutAuthentication("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "dataSetMetadataList(promotedOnly:false publishedOnly:false) {\n" +
            "    dataSetId\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    List<JsonNode> datasetsBeforePublish = Lists.newArrayList(queryData.readEntity(ObjectNode.class)
                                                                       .get("data")
                                                                       .get("dataSetMetadataList")
                                                                       .elements());
    assertThat(datasetsBeforePublish, is(empty()));

    // publish the data set
    call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "mutation publish {\n" +
            "  publish(dataSetId: \"" + dataSetId + "\") {\n" +
            "    uri\n" +
            "  }\n" +
            "}\n"
        )
      ).toString(), MediaType.APPLICATION_JSON));


    // retrieve data sets as a user that is not logged in after publishing
    Response queryDataAfterPublish = callWithoutAuthentication("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity(jsnO(
        "query",
        jsn(
          "query {\n" +
            "dataSetMetadataList(promotedOnly:false publishedOnly:false) {\n" +
            "    dataSetId\n" +
            "  }\n" +
            "}"
        )
      ).toString(), MediaType.APPLICATION_JSON));

    Iterable<JsonNode> datasetsAfterPublish = () -> queryDataAfterPublish.readEntity(ObjectNode.class)
                                                                         .get("data")
                                                                         .get("dataSetMetadataList")
                                                                         .elements();
    List<String> dataSetIds =
      StreamSupport.stream(datasetsAfterPublish.spliterator(), false).map(dataSet -> dataSet.get("dataSetId").asText())
                   .collect(toList());

    assertThat(dataSetIds, hasItem(dataSetId));
  }

  @Test
  public void csvExportIsPossibleForLists() throws Exception {
    String dataSetName = "replacedata" + UUID.randomUUID().toString().replace("-", "_");
    String dataSetId = createDataSetId(dataSetName);
    Response uploadResponse = multipartPost(
        "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
        new File(getResource(IntegrationTest.class, "smalldataset.nt").toURI()),
        "application/n-triples",
        ImmutableMap.of(
            "encoding", "UTF-8",
            "uri", "http://example.com/replacedata"
        )
    );
    assertThat(uploadResponse.getStatus(), is(201));

    Response queryData = call("/v5/graphql")
        .accept("text/csv")
        .post(Entity.entity(jsnO(
            "query",
            jsn(
                "query {\n" +
                    "  dataSets {\n" +
                    "    " + dataSetId + "{\n" +
                    "      schema_PersonList {\n" +
                    "        items {\n" +
                    "          schema_familyName {\n" +
                    "            value\n" +
                    "          }\n" +
                    "          schema_givenNameList {\n" +
                    "            items {\n" +
                    "              value\n" +
                    "            }\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
            )
        ).toString(), MediaType.APPLICATION_JSON));

    final String data = queryData.readEntity(String.class);
    assertThat(data, is(
        "schema_familyName,schema_givenNameList.0,schema_givenNameList.1,schema_givenNameList.2\r\n" +
        "Jansen,Jan,,\r\n" +
        "van Hasselt,Alexander,Michiel,Willem\r\n"
    ));
  }

  @Test
  public void collectionHasCursors() throws Exception {
    String dataSetName = "cursors" + UUID.randomUUID().toString().replace("-", "_");
    String dataSetId = createDataSetId(dataSetName);
    multipartPost(
        "/v5/" + PREFIX + "/" + dataSetName + "/upload/rdf?forceCreation=true",
        new File(getResource(IntegrationTest.class, "bia_clusius.ttl").toURI()),
        "text/turtle",
        ImmutableMap.of(
            "encoding", "UTF-8",
            "uri", "http://example.com/replacedata"
        )
    );

    Response nextCursorResponse = call("/v5/graphql")
        .accept(MediaType.APPLICATION_JSON)
        .post(Entity.entity(jsnO(
            "query",
            jsn(
                "query {\n" +
                    "  dataSets {\n" +
                    "    " + dataSetId + "{\n" +
                    "      clusius_PersonsList {\n" +
                    "        nextCursor" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
            )
        ).toString(), MediaType.APPLICATION_JSON));
    ObjectNode responseData = nextCursorResponse.readEntity(ObjectNode.class);
    JsonNode nextCursor = responseData.findValue("nextCursor");
    assertThat(nextCursor.isTextual(), is(true));


    Response prevCursorResponse = call("/v5/graphql")
        .accept(MediaType.APPLICATION_JSON)
        .post(Entity.entity(jsnO(
            "query",
            jsn(
                "query($cursor: ID)  {\n" +
                    "  dataSets {\n" +
                    "    " + dataSetId + " {\n" +
                    "      clusius_PersonsList(cursor: $cursor) {\n" +
                    "        prevCursor\n" +
                    "        nextCursor\n" +
                    "        items {\n" +
                    "          title {\n" +
                    "            value\n" +
                    "          }\n" +
                    "        }\n" +
                    "      }\n" +
                    "    }\n" +
                    "  }\n" +
                    "}"
            ),
            "variables", jsnO("cursor", jsn(nextCursor.asText()))
        ).toString(), MediaType.APPLICATION_JSON));
    responseData = prevCursorResponse.readEntity(ObjectNode.class);
    JsonNode prevCursor = responseData.findValue("prevCursor");
    assertThat(prevCursor.isTextual(), is(true));
  }

  private List<String> getDataSetNamesOfDummy() {
    Response graphqlCall = call("/v5/graphql")
      .accept(MediaType.APPLICATION_JSON)
      .post(Entity.entity("{aboutMe{dataSets{dataSetId}}}", MediaType.valueOf("application/graphql")));
    ObjectNode objectNode = graphqlCall.readEntity(ObjectNode.class);

    assertThat(graphqlCall.getStatus(), is(200));
    return stream(
      objectNode
        .get("data")
        .get("aboutMe")
        .get("dataSets").iterator())
      .map(x -> x.get("dataSetId").asText())
      .collect(toList());
  }

  private Response multipartPost(String path, File resource, String mediaType, Map<String, String> arguments)
    throws ParseException {
    MultiPart formdata = new MultiPart();
    arguments.forEach((key, value) -> formdata.bodyPart(new FormDataBodyPart(key, value)));

    formdata.bodyPart(new FormDataBodyPart(
      new FormDataContentDisposition(
        "form-data; name=\"file\"; filename=\"" + resource.getName().replace("\"", "") + "\""
      ),
      resource,
      MediaType.valueOf(mediaType)
    ));

    Response result = call(path)
      .post(Entity.entity(formdata, "multipart/form-data; boundary=Boundary_1_498293219_1483974344746"));
    return result;
  }

  private Invocation.Builder call(String path) {
    String server;
    if (path.startsWith("http://") || path.startsWith("https://")) {
      server = path;
    } else {
      int localPort = APP.getLocalPort();
      // int localPort = 8080;
      server = format("http://localhost:%d" + path, localPort);
    }
    return client
      .target(server)
      .register(MultiPartFeature.class)
      // .register(new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true))
      .request()
      .header("Authorization", AUTH);
  }


  private Invocation.Builder callWithoutAuthentication(String path) {
    String server;
    if (path.startsWith("http://") || path.startsWith("https://")) {
      server = path;
    } else {
      int localPort = APP.getLocalPort();
      server = format("http://localhost:%d" + path, localPort);
    }
    return client
      .target(server)
      .register(MultiPartFeature.class)
      // .register(new LoggingFilter(Logger.getLogger(LoggingFilter.class.getName()), true))
      .request();
  }
}
