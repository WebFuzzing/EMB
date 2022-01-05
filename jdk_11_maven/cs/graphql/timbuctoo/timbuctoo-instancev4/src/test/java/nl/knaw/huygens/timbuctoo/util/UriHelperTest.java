package nl.knaw.huygens.timbuctoo.util;

import org.junit.Test;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UriHelperTest {
  @Test
  public void keepsEverythingFromBaseUriExceptQuery() {
    UriHelper instance = new UriHelper(URI.create("http://user:password@example.com:8080/path1?bar=baz"));

    URI resourceUri = UriBuilder.fromUri("/path2/path3").queryParam("q1", "v1").queryParam("q2", "v2").build();
    String uri = instance.fromResourceUri(resourceUri).toString();

    assertThat(uri, is("http://user:password@example.com:8080/path1/path2/path3?q1=v1&q2=v2"));
  }

  @Test
  public void keepsOnlyPathAndQueryFromResourceUri() {
    UriHelper instance = new UriHelper(URI.create("http://acme.org/path1"));

    String uri = instance.fromResourceUri(URI.create("http://user:password@example.com:8080/path2?foo=bar")).toString();

    assertThat(uri, is("http://acme.org/path1/path2?foo=bar"));
  }


  @Test
  public void normalizesSlashes() {
    UriHelper helperOpen = new UriHelper(URI.create("http://example.com/path1"));
    UriHelper helperSlash = new UriHelper(URI.create("http://example.com/path1/"));

    URI resourceOpen = UriBuilder.fromUri("/path2/path3").build();
    URI resourceSlash = UriBuilder.fromUri("path2/path3").build();

    String expectation = "http://example.com/path1/path2/path3";
    assertThat(helperOpen.fromResourceUri(resourceSlash).toString(), is(expectation));
    assertThat(helperOpen.fromResourceUri(resourceOpen).toString(), is(expectation));
    assertThat(helperSlash.fromResourceUri(resourceSlash).toString(), is(expectation));
    assertThat(helperSlash.fromResourceUri(resourceOpen).toString(), is(expectation));
  }

  @Test
  public void normalizesSlashesWithoutBaseUriPath() {
    UriHelper helperOpen = new UriHelper(URI.create("http://example.com"));
    UriHelper helperSlash = new UriHelper(URI.create("http://example.com/"));

    URI resourceOpen = UriBuilder.fromUri("/path2/path3").build();
    URI resourceSlash = UriBuilder.fromUri("path2/path3").build();

    String expectation = "http://example.com/path2/path3";
    assertThat(helperOpen.fromResourceUri(resourceSlash).toString(), is(expectation));
    assertThat(helperOpen.fromResourceUri(resourceOpen).toString(), is(expectation));
    assertThat(helperSlash.fromResourceUri(resourceSlash).toString(), is(expectation));
    assertThat(helperSlash.fromResourceUri(resourceOpen).toString(), is(expectation));
  }


}
