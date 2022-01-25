package nl.knaw.huygens.timbuctoo.remote.rs.xml;


import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.InputStream;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class RsBuilderTest {

  @Test
  public void buildAndSuccess() throws Exception {
    RsBuilder rsBuilder = new RsBuilder(new ResourceSyncContext());

    String sitemapindexXml = rsBuilder.toXml(createSitemapIndex(), true);
    //System.out.println(sitemapindexXml);
    InputStream sitemapindexIs = IOUtils.toInputStream(sitemapindexXml);
    RsRoot root = rsBuilder.setInputStream(sitemapindexIs).build().get();
    IOUtils.closeQuietly(sitemapindexIs);

    Sitemapindex smi = rsBuilder.getSitemapindex().get();
    assertThat(root, equalTo(smi));
    assertThat(rsBuilder.getQName().get(), equalTo(Sitemapindex.QNAME));
    assertThat(rsBuilder.getUrlset().isPresent(), is(false));

    // RsBuilder can be reused
    String urlsetXml = rsBuilder.toXml(createUrlset(), true);
    //System.out.println(urlsetXml);
    InputStream urlsetIs = IOUtils.toInputStream(urlsetXml);
    root = rsBuilder.setInputStream(urlsetIs).build().get();
    IOUtils.closeQuietly(urlsetIs);

    Urlset urlset = rsBuilder.getUrlset().get();
    assertThat(root, equalTo(urlset));
    assertThat(rsBuilder.getQName().get(), equalTo(Urlset.QNAME));
    assertThat(rsBuilder.getSitemapindex().isPresent(), is(false));

  }

  @Test(expected = JAXBException.class)
  public void buildAndFailure() throws Exception {
    String invalidXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"><foo>bar</foo>";
    InputStream invalidIs = IOUtils.toInputStream(invalidXml);
    RsBuilder rsBuilder = new RsBuilder(new ResourceSyncContext());
    rsBuilder.setInputStream(invalidIs).build();
  }


  private Sitemapindex createSitemapIndex() {

    return new Sitemapindex(new RsMd("resourcelist"))
      .addLink(new RsLn("up", "http://example.com/dataset1/capabilitylist.xml"))
      .addItem(new SitemapItem("http://example.com/resourcelist1.xml")
        .withMetadata(new RsMd().withAt(ZonedDateTime.now(ZoneOffset.UTC))))
      .addItem(new SitemapItem("http://example.com/resourcelist2.xml")
        .withMetadata(new RsMd().withAt(ZonedDateTime.now(ZoneOffset.UTC))));
  }

  private Urlset createUrlset() {

    RsMd rsMd = new RsMd("description")
      .withAt(ZonedDateTime.now(ZoneOffset.UTC))
      .withCompleted(ZonedDateTime.now(ZoneOffset.UTC))
      .withFrom(ZonedDateTime.now(ZoneOffset.UTC))
      .withUntil(ZonedDateTime.now(ZoneOffset.UTC));

    RsMd urlRsMd = new RsMd("capabilitylist")
      .withChange("updated")
      .withEncoding("encoding")
      .withHash("hash")
      .withLength(2L)
      .withPath("another/path")
      .withType("text/plain");

    RsLn urlLink1 = new RsLn("describedby", "http://example.com/info_about_set1_of_resources.xml")
      .withEncoding("UTF-8")
      .withHash("md5:1584abdf8ebdc9802ac0c6a7402c03b6")
      .withLength(1000L)
      .withModified(ZonedDateTime.now(ZoneOffset.UTC))
      .withPath("foo/bar")
      .withPri(1)
      .withType("application/xml");

    UrlItem url1 = new UrlItem("http://example.com/capabilitylist1.xml")
      .withMetadata(urlRsMd)
      .addLink(urlLink1)
      .addLink(new RsLn("duplicate", "http://also.com/capabilitylist1.xml"));

    UrlItem url2 = new UrlItem("http://example.com/capabilitylist2.xml");

    return new Urlset(rsMd)
      .addLink(new RsLn("describedby", "http://example.com/info_about_source.xml"))
      .addLink(new RsLn("up", "http://example.com/attic"))
      .addItem(url1)
      .addItem(url2);

  }
}
