package nl.knaw.huygens.timbuctoo.remote.rs.xml;

import org.apache.commons.io.IOUtils;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.io.StringWriter;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class UrlsetTest {

  private static Marshaller jaxbMarshaller;
  private static Unmarshaller jaxbUnmarshaller;

  @BeforeClass
  public static void initialize() throws Exception {
    JAXBContext jaxbContext = JAXBContext.newInstance(Urlset.class);
    jaxbMarshaller = jaxbContext.createMarshaller();
    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

    jaxbUnmarshaller = jaxbContext.createUnmarshaller();
  }

  @Test
  public void testSerializeDeserializeUrlset() throws Exception {

    Urlset urlset = createUrlset();
    String output1 = asXml(urlset);
    //System.out.println(output1);

    InputStream in = IOUtils.toInputStream(output1);
    RsRoot returned = (RsRoot) jaxbUnmarshaller.unmarshal(in);
    IOUtils.closeQuietly(in);
    String output2 = asXml(returned);
    //System.out.println(output2);

    assertThat(output2, equalTo(output1));
  }


  @Test
  public void readValuesOfLinkCaseInsensitive() throws Exception {
    Urlset urlset = createUrlset();
    //System.out.println(asXml(urlset));

    assertThat(urlset.getLinkHref("describedby"), equalTo("http://example.com/info_about_source.xml"));
    assertThat(urlset.getLinkHref("describedBy"), equalTo("http://example.com/info_about_source.xml"));

    UrlItem urlItem1 = urlset.getItemList().get(0);
    assertThat(urlItem1.getLinkHref("describedby"),
      equalTo("http://example.com/info_about_set1_of_resources.xml"));
    assertThat(urlItem1.getLinkHref("describedBy"),
      equalTo("http://example.com/info_about_set1_of_resources.xml"));
  }

  @Test
  public void readNullValue() throws Exception {
    Urlset urlset = createUrlset();
    UrlItem urlItem2 = urlset.getItemList().get(1);
    assertThat(urlItem2.getLinkHref("describedby"), equalTo(null));
  }

  private String asXml(RsRoot root) throws JAXBException {
    StringWriter writer = new StringWriter();
    jaxbMarshaller.marshal(root, writer);
    return writer.toString();
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
