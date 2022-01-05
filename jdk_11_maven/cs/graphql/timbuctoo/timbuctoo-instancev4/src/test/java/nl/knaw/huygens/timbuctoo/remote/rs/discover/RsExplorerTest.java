package nl.knaw.huygens.timbuctoo.remote.rs.discover;


import nl.knaw.huygens.timbuctoo.remote.rs.xml.RsMd;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.RsRoot;
import org.junit.Test;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import javax.xml.bind.JAXBException;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

public class RsExplorerTest extends AbstractRemoteTest {

  @Test
  public void findSourceDescription() throws Exception {
    String path = "/.well-known/resourcesync";
    verifyFindSourceDescription(path);
  }

  @Test
  public void findSourceDescriptionWithoutExplicitUrl() throws Exception {
    String path = "";
    verifyFindSourceDescription(path);
  }

  @Test
  public void findSourceDescriptionWithoutExplicitUrlVariation() throws Exception {
    String path = "/";
    verifyFindSourceDescription(path);
  }

  private void verifyFindSourceDescription(String path) {
    getMockServer()
      .when(HttpRequest.request()
          .withMethod("GET")
          .withPath(path),
        Times.exactly(1))

      .respond(HttpResponse.response()
        .withStatusCode(200)
        .withBody(createValidSourceDescription())
      );

    URI uri = composeUri(path);

    RsExplorer explorer = new RsExplorer(getHttpclient(), getRsContext());
    ResultIndex index = new ResultIndex();
    Result<RsRoot> result = explorer.explore(uri, index, null);

    result.getErrors().forEach(Throwable::printStackTrace);

    assertThat(result.getUri(), equalTo(uri));
    assertThat(result.getStatusCode(), equalTo(200));
    assertThat(result.getErrors().isEmpty(), is(true));
    assertThat(result.getContent().isPresent(), is(true));
    assertThat(result.getContent().map(RsRoot::getMetadata).flatMap(RsMd::getCapability).orElse("invalid"),
      equalTo("description"));

    // index should contain the describedBy uri of the source description:
    assertThat(index.contains(composeUri("/info_about_source.xml")), is(true));
    // result should contain the describedByResult:
    Result<Description> descriptionResult = result.getDescriptionResult().get();
    assertThat(descriptionResult.getUri(), equalTo(composeUri("/info_about_source.xml")));
    assertThat(descriptionResult.getContent().isPresent(), is(false));
    assertThat(descriptionResult.getStatusCode(), equalTo(404));
  }

  @Test
  public void findWrongParentDocument() throws Exception {
    String path1 = "/foo/resourcedump.xml";
    String path2 = "/bla/resourcedump.xml";

    getMockServer()
      .when(HttpRequest.request()
          .withMethod("GET")
          .withPath(path1),
        Times.exactly(1))

      .respond(HttpResponse.response()
        .withStatusCode(200)
        .withBody(createValidResourceDump(path2))
      );

    getMockServer()
      .when(HttpRequest.request()
          .withMethod("GET")
          .withPath(path2),
        Times.exactly(1))

      .respond(HttpResponse.response()
        .withStatusCode(200)
        .withBody(createValidResourceDump("/bar/whatever.xml"))
      );

    URI uri = composeUri(path1);

    RsExplorer explorer = new RsExplorer(getHttpclient(), getRsContext());
    ResultIndex index = new ResultIndex();
    Result<RsRoot> result = explorer.explore(uri, index, null);

    //result.listErrors().forEach(Throwable::printStackTrace);

    assertThat(result.getUri(), equalTo(uri));
    assertThat(result.getStatusCode(), equalTo(200));
    assertThat(result.getErrors().isEmpty(), is(false));
    assertThat(result.getErrors().get(0).getMessage(), containsString("invalid up relation:"));
    assertThat(result.getContent().isPresent(), is(true));
    assertThat(result.getContent().map(RsRoot::getMetadata).flatMap(RsMd::getCapability).orElse("invalid"),
      equalTo("resourcedump"));

    URI uri2 = composeUri(path2);
    Result<RsRoot> parentResult = (Result<RsRoot>) result.getParents().get(uri2);

    parentResult.getErrors().forEach(Throwable::printStackTrace);

    assertThat(parentResult.getUri(), equalTo(uri2));
    assertThat(parentResult.getStatusCode(), equalTo(200));
    assertThat(parentResult.getErrors().isEmpty(), is(true));
    assertThat(parentResult.getContent().isPresent(), is(true));
    assertThat(parentResult.getContent().map(RsRoot::getMetadata).flatMap(RsMd::getCapability).orElse("invalid"),
      equalTo("resourcedump"));
  }

  @Test
  public void findInvalidSitemapDocument() throws Exception {
    String path = "/.well-known/resourcesync";

    getMockServer()
      .when(HttpRequest.request()
          .withMethod("GET")
          .withPath(path),
        Times.exactly(1))

      .respond(HttpResponse.response()
        .withStatusCode(200)
        .withBody(createValidXml())
      );

    RsExplorer explorer = new RsExplorer(getHttpclient(), getRsContext());
    ResultIndex index = new ResultIndex();
    Result<RsRoot> result = explorer.explore(composeUri(path), index, null);

    assertThat(result.getUri(), equalTo(composeUri(path)));
    assertThat(result.getStatusCode(), equalTo(200));
    assertThat(result.getErrors().isEmpty(), is(false));
    assertThat(result.getErrors().get(0), instanceOf(JAXBException.class));
    assertThat(result.getContent().isPresent(), is(false));
    //result.listErrors().forEach(Throwable::printStackTrace);
  }

  @Test
  public void receiveStatusError() throws Exception {
    String path = "/.well-known/resourcesync";

    getMockServer()
      .when(HttpRequest.request()
          .withMethod("GET")
          .withPath(path),
        Times.exactly(1))

      .respond(HttpResponse.response()
        .withStatusCode(404)
        .withBody("Document not found")
      );

    RsExplorer explorer = new RsExplorer(getHttpclient(), getRsContext());
    ResultIndex index = new ResultIndex();
    Result<RsRoot> result = explorer.explore(composeUri(path), index, null);

    assertThat(result.getUri(), equalTo(composeUri(path)));
    assertThat(result.getStatusCode(), equalTo(404));
    assertThat(result.getErrors().isEmpty(), is(false));
    assertThat(result.getErrors().get(0), instanceOf(RemoteException.class));
    assertThat(result.getContent().isPresent(), is(false));
    //result.getDataSetErrors().forEach(Throwable::printStackTrace);
  }

  @Test
  @SuppressWarnings({"unchecked"})
  public void findDescribedByDocuments() throws Exception {
    String path = "/.well-known/resourcesync";
    String pathDescriptionOfSource = "/info_about_source.xml";
    String pathDescriptionOfSet1 = "/info_about_set1_of_resources.xml";
    String pathDescriptionOfSet2 = "/info_about_set2_of_resources.xml";
    String pathDescriptionOfSet3 = "/info_about_set3_of_resources.xml";

    getMockServer()
      .when(HttpRequest.request()
                       .withMethod("GET")
                       .withPath(path),
        Times.exactly(1))
      .respond(HttpResponse.response()
                           .withStatusCode(200)
                           .withBody(createValidSourceDescription())
      );

    getMockServer()
      .when(HttpRequest.request()
                       .withMethod("GET")
                       .withPath(pathDescriptionOfSet1),
        Times.exactly(1))
      .respond(HttpResponse.response()
                           .withStatusCode(200)
                           .withBody(createDescriptionDocument())
      );

    getMockServer()
      .when(HttpRequest.request()
                       .withMethod("GET")
                       .withPath(pathDescriptionOfSet2),
        Times.exactly(1))
      .respond(HttpResponse.response()
                           .withStatusCode(404)
                           .withBody("Not Found")
      );

    getMockServer()
      .when(HttpRequest.request()
                       .withMethod("GET")
                       .withPath(pathDescriptionOfSet3),
        Times.exactly(1))
      .respond(HttpResponse.response()
                           .withStatusCode(200)
                           .withBody(createDescriptionDocument())
      );

    getMockServer()
      .when(HttpRequest.request()
                       .withMethod("GET")
                       .withPath(pathDescriptionOfSource),
        Times.exactly(1))
      .respond(HttpResponse.response()
                           .withStatusCode(200)
                           .withBody(createDescriptionDocument())
      );

    RsExplorer explorer = new RsExplorer(getHttpclient(), getRsContext());
    ResultIndex index = new ResultIndex();
    Result<RsRoot> result = explorer.explore(composeUri(path), index, null);

    assertThat(result.getStatusCode(), equalTo(200));
    assertThat(result.getDescriptionResult().isPresent(), is(true));
    Result<Description> describedByResult = result.getDescriptionResult().get();
    assertThat(describedByResult.getContent().isPresent(), is(true));
    assertThat(describedByResult.getContent().get().getRawContent(), equalTo(createDescriptionDocument()));

    Result<RsRoot> child1 = (Result<RsRoot>)
      result.getChildren().get(URI.create("http://example.com/capabilitylist1.xml"));
    assertThat(child1.getContent().isPresent(), is(false));
    assertThat(child1.getDescriptionResult().isPresent(), is(true));
    Result<Description> descriptionResult1 = child1.getDescriptionResult().get();
    descriptionResult1.getErrors().forEach(Throwable::printStackTrace);
    assertThat(descriptionResult1.getContent().isPresent(), is(true));
    assertThat(descriptionResult1.getContent().get().getRawContent(), equalTo(createDescriptionDocument()));

    Result<RsRoot> child3 = (Result<RsRoot>)
      result.getChildren().get(URI.create("http://example.com/capabilitylist3.xml"));
    assertThat(child3.getContent().isPresent(), is(false));
    assertThat(child3.getDescriptionResult().isPresent(), is(true));
    Result<Description> descriptionResult3 = child3.getDescriptionResult().get();
    descriptionResult3.getErrors().forEach(Throwable::printStackTrace);
    assertThat(descriptionResult3.getContent().isPresent(), is(true));
    assertThat(descriptionResult3.getContent().get().getRawContent(), equalTo(createDescriptionDocument()));

    Result<RsRoot> child2 = (Result<RsRoot>)
      result.getChildren().get(URI.create("http://example.com/capabilitylist2.xml"));
    assertThat(child2.getContent().isPresent(), is(false));
    assertThat(child2.getDescriptionResult().isPresent(), is(true));
    Result<Description> descriptionResult2 = child2.getDescriptionResult().get();
    assertThat(descriptionResult2.getContent().isPresent(), is(false));
    assertThat(descriptionResult2.getStatusCode(), is(404));
  }


  private String createValidSourceDescription() {
    return
      "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n" +
        "        xmlns:rs=\"http://www.openarchives.org/rs/terms/\">\n" +
        "  <rs:ln rel=\"describedby\"\n" +
        "         href=\"" + composeUri("/info_about_source.xml") + "\"/>\n" +
        "  <rs:md capability=\"description\"/>\n" +
        "  <url>\n" +
        "      <loc>http://example.com/capabilitylist1.xml</loc>\n" +
        "      <rs:md capability=\"capabilitylist\"/>\n" +
        "      <rs:ln rel=\"describedby\"\n" +
        "             href=\"" + composeUri("/info_about_set1_of_resources.xml") + "\"/>\n" +
        "  </url>\n" +
        "  <url>\n" +
        "      <loc>http://example.com/capabilitylist2.xml</loc>\n" +
        "      <rs:md capability=\"capabilitylist\"/>\n" +
        "      <rs:ln rel=\"describedby\"\n" +
        "             href=\"" + composeUri("/info_about_set2_of_resources.xml") + "\"/>\n" +
        "  </url>\n" +
        "  <url>\n" +
        "      <loc>http://example.com/capabilitylist3.xml</loc>\n" +
        "      <rs:md capability=\"capabilitylist\"/>\n" +
        "      <rs:ln rel=\"describedby\"\n" +
        "             href=\"" + composeUri("/info_about_set3_of_resources.xml") + "\"/>\n" +
        "  </url>\n" +
        "</urlset>\n" +
        "\n";
  }

  private String createValidResourceDump(String relUpPath) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n" +
      "        xmlns:rs=\"http://www.openarchives.org/rs/terms/\">\n" +
      "  <rs:md capability=\"resourcedump\"\n" +
      "         at=\"2013-01-03T09:00:00Z\"/>\n" +
      "  <rs:ln rel=\"up\"" +
      "         href=\"" + composePath(relUpPath) + "\"/>" +
      "  <url>\n" +
      "      <loc>http://example.com/resourcedump.zip</loc>\n" +
      "      <lastmod>2013-01-03T09:00:00Z</lastmod>\n" +
      "  </url>\n" +
      "</urlset>";
  }

  private String createValidXml() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<funny xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n" +
      "        xmlns:rs=\"http://www.openarchives.org/rs/terms/\">\n" +
      "  <rs:md capability=\"description\"\n" +
      "         at=\"2013-01-03T09:00:00Z\"/>\n" +
      "</funny>";
  }

  private String createDescriptionDocument() {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<rdf:RDF\n" +
      "\txmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\">\n" +
      "\n" +
      "<rdf:Description rdf:about=\"http://example" +
      ".org/datasets/u33707283d426f900d4d33707283d426f900d4d0d/clusius3/\">\n" +
      "\t<abstract xmlns=\"http://purl.org/dc/terms/\" rdf:resource=\"http://example.org/datasets/u33707283d426f900d4d33707283d426f900d4d0d/clusius3/summaryProperties\"/>\n" +
      "\t<description xmlns=\"http://purl.org/dc/terms/\" rdf:datatype=\"http://www" +
      ".w3.org/2001/XMLSchema#string\">Biographical data of the Digital Web Centre for the History of Science (DWC)" +
      "</description>\n" +
      "\t<license xmlns=\"http://purl.org/dc/terms/\" rdf:resource=\"https://creativecommons" +
      ".org/publicdomain/zero/1.0/\"/>\n" +
      "\t<provenance xmlns=\"http://purl.org/dc/terms/\" rdf:resource=\"http://example" +
      ".org/datasets/u33707283d426f900d4d33707283d426f900d4d0d/clusius3/provenance\"/>\n" +
      "\t<rightsHolder xmlns=\"http://purl.org/dc/terms/\" rdf:resource=\"http://example" +
      ".org/datasets/u33707283d426f900d4d33707283d426f900d4d0d/clusius3/rightsHolder\"/>\n" +
      "\t<title xmlns=\"http://purl.org/dc/terms/\" rdf:datatype=\"http://www.w3.org/2001/XMLSchema#string\">DWC " +
      "Data</title>\n" +
      "\t<ContactPoint xmlns=\"http://schema.org/\" rdf:resource=\"http://example" +
      ".org/datasets/u33707283d426f900d4d33707283d426f900d4d0d/clusius3/contactPerson\"/>\n" +
      "</rdf:Description>\n" +
      "\n" +
      "</rdf:RDF>";
  }

}
