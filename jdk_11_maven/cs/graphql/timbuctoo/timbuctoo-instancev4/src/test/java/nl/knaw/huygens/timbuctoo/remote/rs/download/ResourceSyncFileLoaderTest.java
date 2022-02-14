package nl.knaw.huygens.timbuctoo.remote.rs.download;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class ResourceSyncFileLoaderTest {

  private String baseUrl = "http://127.0.0.1:8080/v5/resourcesync/u33707283d426f900d4d33707283d426f900d4d0d/clusius/";

  @Test
  public void getRemoteFilesListListsAllChangelistAndResourcelistFilesInAnObject() throws Exception {
    String capabilityList = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
      "<urlset xmlns:rs=\"http://www.openarchives.org/rs/terms/\" " +
      "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
      "  <rs:md capability=\"capabilitylist\"/>\n" +
      "  <rs:ln rel=\"up\" href=\"http://127.0.0.1:8080/.well-known/resourcesync\"/>\n" +
      "  <rs:ln rel=\"describedby\" href=\"" + baseUrl + "description.xml\" type=\"application/rdf+xml\"/>\n" +
      "  <url>\n" +
      "    <loc>" + baseUrl + "resourcelist.xml</loc>\n" +
      "    <rs:md capability=\"resourcelist\"/>\n" +
      "  </url>\n" +
      "  <url>\n" +
      "      <loc>" + baseUrl + "changelist.xml</loc>\n" +
      "      <rs:md capability=\"changelist\"/>\n" +
      "  </url>" +
      "</urlset>\n";

    String resourceList = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
      "<urlset xmlns:rs=\"http://www.openarchives.org/rs/terms/\" " +
      "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
      "  <rs:md capability=\"resourcelist\" at=\"2018-03-21T10:55:07.907Z\" " +
      "completed=\"2018-03-21T10:56:39.551Z\"/>\n" +
      "  <rs:ln rel=\"up\" href=\"" + baseUrl + "capabilitylist.xml\"/>\n" +
      "  <url>\n" +
      "    <loc>" + baseUrl + "files/dataset.nq</loc>\n" +
      "    <rs:md type=\"application/n-quads\"/>\n" +
      "  </url>\n" +
      "</urlset>\n";

    String changeList = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
      "<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\"\n" +
      "        xmlns:rs=\"http://www.openarchives.org/rs/terms/\">\n" +
      "  <rs:md capability=\"changelist\"\n" +
      "         from=\"2013-01-02T00:00:00Z\"\n" +
      "         until=\"2013-01-03T00:00:00Z\"/>\n" +
      "  <url>\n" +
      "      <loc>" + baseUrl + "files/dataset.nq</loc>\n" +
      "      <lastmod>2013-01-02T13:00:00Z</lastmod>\n" +
      "      <rs:md change=\"updated\" datetime=\"2013-01-02T13:00:00Z\"/>\n" +
      "      <rs:ln rel=\"http://www.openarchives.org/rs/terms/patch\" href=\"" + baseUrl + "files/changes1.nqud\" " +
      "      type=\"application/vnd.timbuctoo-rdf.nquads_unified_diff\" />" +
      "  </url>\n" +
      "  <url>\n" +
      "      <loc>" + baseUrl + "files/dataset.nq</loc>\n" +
      "      <lastmod>2013-01-02T13:00:00Z</lastmod>\n" +
      "      <rs:md change=\"updated\" datetime=\"2013-01-02T13:00:00Z\"/>\n" +
      "      <rs:ln rel=\"http://www.openarchives.org/rs/terms/patch\" href=\"" + baseUrl + "files/changes2.nqud\" " +
      "      type=\"application/vnd.timbuctoo-rdf.nquads_unified_diff\" />" +
      "  </url>\n" +
      "</urlset>";


    InputStream capabilityListStream = new ByteArrayInputStream(capabilityList.getBytes());

    InputStream resourceListStream = new ByteArrayInputStream(resourceList.getBytes());

    InputStream changeListStream = new ByteArrayInputStream(changeList.getBytes());

    ResourceSyncFileLoader.RemoteFileRetriever remoteFileRetriever = mock(
      ResourceSyncFileLoader.RemoteFileRetriever.class
    );

    given(remoteFileRetriever.getFile(baseUrl + "capabilitylist.xml", null)).willReturn(capabilityListStream);

    given(remoteFileRetriever.getFile(baseUrl + "resourcelist.xml", null)).willReturn(resourceListStream);

    given(remoteFileRetriever.getFile(baseUrl + "changelist.xml", null)
    ).willReturn(changeListStream);


    ResourceSyncFileLoader resourceSyncFileLoader = new ResourceSyncFileLoader(remoteFileRetriever);

    ResourceSyncFileLoader.RemoteFilesList remoteFilesList = resourceSyncFileLoader.getRemoteFilesList(
      baseUrl + "capabilitylist.xml", null);

    assertThat(remoteFilesList.getChangeList(), containsInAnyOrder(
      hasProperty("url", is(baseUrl + "files/changes1.nqud")),
      hasProperty("url", is(baseUrl + "files/changes2.nqud"))
    ));

    assertThat(remoteFilesList.getResourceList(), contains(allOf(
      hasProperty("url", is(baseUrl + "files/dataset.nq")),
      hasProperty("mimeType", is("application/n-quads"))
    )));

  }

  @Test
  public void getRemoteFilesListSupportsMultipleResourceTypes() throws Exception {
    String capabilityList = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
      "<urlset xmlns:rs=\"http://www.openarchives.org/rs/terms/\" " +
      "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
      "  <rs:md capability=\"capabilitylist\"/>\n" +
      "  <rs:ln rel=\"up\" href=\"http://127.0.0.1:8080/.well-known/resourcesync\"/>\n" +
      "  <rs:ln rel=\"describedby\" href=\"" + baseUrl + "description.xml\" type=\"application/rdf+xml\"/>\n" +
      "  <url>\n" +
      "    <loc>" + baseUrl + "resourcelist.xml</loc>\n" +
      "    <rs:md capability=\"resourcelist\"/>\n" +
      "  </url>\n" +
      "</urlset>\n";

    String resourceList = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
      "<urlset xmlns:rs=\"http://www.openarchives.org/rs/terms/\" " +
      "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
      "  <rs:md capability=\"resourcelist\" at=\"2018-03-21T10:55:07.907Z\" " +
      "completed=\"2018-03-21T10:56:39.551Z\"/>\n" +
      "  <rs:ln rel=\"up\" href=\"" + baseUrl + "capabilitylist.xml\"/>\n" +
      "  <url>\n" +
      "    <loc>" + baseUrl + "files/dataset.nq</loc>\n" +
      "    <rs:md type=\"application/n-quads\"/>\n" +
      "  </url>\n" +
      "  <url>\n" +
      "    <loc>" + baseUrl + "files/dataset.nqud</loc>\n" +
      "    <rs:md type=\"application/vnd.timbuctoo-rdf.nquads_unified_diff\"/>\n" +
      "  </url>\n" +
      "</urlset>\n";


    InputStream capabilityListStream = new ByteArrayInputStream(capabilityList.getBytes());

    InputStream resourceListStream = new ByteArrayInputStream(resourceList.getBytes());

    ResourceSyncFileLoader.RemoteFileRetriever remoteFileRetriever = mock(
      ResourceSyncFileLoader.RemoteFileRetriever.class
    );

    given(remoteFileRetriever.getFile(baseUrl + "capabilitylist.xml", null)).willReturn(capabilityListStream);

    given(remoteFileRetriever.getFile(baseUrl + "resourcelist.xml", null)).willReturn(resourceListStream);

    ResourceSyncFileLoader resourceSyncFileLoader = new ResourceSyncFileLoader(remoteFileRetriever);

    ResourceSyncFileLoader.RemoteFilesList remoteFilesList = resourceSyncFileLoader.getRemoteFilesList(
      baseUrl + "capabilitylist.xml", null);;

    assertThat(remoteFilesList.getResourceList(), containsInAnyOrder(
      allOf(
        hasProperty("url", is(baseUrl + "files/dataset.nq")),
        hasProperty("mimeType", is("application/n-quads"))
      ),
      allOf(
        hasProperty("url", is(baseUrl + "files/dataset.nqud")),
        hasProperty("mimeType", is("application/vnd.timbuctoo-rdf.nquads_unified_diff"))
      )
    ));
  }

  @Test
  public void getRemoteFilesFiltersOnMimeType() throws Exception {
    String capabilityList = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
      "<urlset xmlns:rs=\"http://www.openarchives.org/rs/terms/\" " +
      "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
      "  <rs:md capability=\"capabilitylist\"/>\n" +
      "  <rs:ln rel=\"up\" href=\"http://127.0.0.1:8080/.well-known/resourcesync\"/>\n" +
      "  <rs:ln rel=\"describedby\" href=\"" + baseUrl + "description.xml\" type=\"application/rdf+xml\"/>\n" +
      "  <url>\n" +
      "    <loc>" + baseUrl + "resourcelist.xml</loc>\n" +
      "    <rs:md capability=\"resourcelist\"/>\n" +
      "  </url>\n" +
      "</urlset>\n";

    String resourceList = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
      "<urlset xmlns:rs=\"http://www.openarchives.org/rs/terms/\" " +
      "xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n" +
      "  <rs:md capability=\"resourcelist\" at=\"2018-03-21T10:55:07.907Z\" " +
      "completed=\"2018-03-21T10:56:39.551Z\"/>\n" +
      "  <rs:ln rel=\"up\" href=\"" + baseUrl + "capabilitylist.xml\"/>\n" +
      "  <url>\n" +
      "    <loc>" + baseUrl + "files/dataset</loc>\n" +
      "    <rs:md type=\"application/vnd.timbuctoo-rdf.nquads_unified_diff\"/>\n" +
      "  </url>\n" +
      "</urlset>\n";


    InputStream capabilityListStream = new ByteArrayInputStream(capabilityList.getBytes());

    InputStream resourceListStream = new ByteArrayInputStream(resourceList.getBytes());

    ResourceSyncFileLoader.RemoteFileRetriever remoteFileRetriever = mock(
      ResourceSyncFileLoader.RemoteFileRetriever.class
    );

    given(remoteFileRetriever.getFile(baseUrl + "capabilitylist.xml", null)).willReturn(capabilityListStream);

    given(remoteFileRetriever.getFile(baseUrl + "resourcelist.xml", null)).willReturn(resourceListStream);

    ResourceSyncFileLoader resourceSyncFileLoader = new ResourceSyncFileLoader(remoteFileRetriever);

    ResourceSyncFileLoader.RemoteFilesList remoteFilesList = resourceSyncFileLoader.getRemoteFilesList(
      baseUrl + "capabilitylist.xml", null);
    ;

    assertThat(remoteFilesList.getResourceList(), contains(
      allOf(
        hasProperty("url", is(baseUrl + "files/dataset")),
        hasProperty("mimeType", is("application/vnd.timbuctoo-rdf.nquads_unified_diff"))
      )
    ));
  }

}
